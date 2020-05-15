/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.ops.to2library;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.EncryptedMessageCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetNextDeviceServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2ProveDeviceCodec;
import org.sdo.iotplatformsdk.common.protocol.config.EpidOptionBean;
import org.sdo.iotplatformsdk.common.protocol.security.Signatures;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey;
import org.sdo.iotplatformsdk.common.protocol.types.EpidSignatureParameterSpec;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextDeviceServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2ProveDevice;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.epid.EpidSecurityProvider;
import org.sdo.iotplatformsdk.ops.serviceinfo.PreServiceInfoMultiSource;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message44Handler {

  private EpidOptionBean epidOptions = new EpidOptionBean();
  private final SecureRandom secureRandom;
  private Set<ServiceInfoModule> serviceInfoModules = new HashSet<>();
  private final SessionStorage sessionStorage;
  private final KeyExchangeDecoder keyExchangeDecoder;

  /**
   * Builds {@link PreServiceInfo}.
   */
  protected PreServiceInfo buildPreServiceInfo(UUID id,
      Collection<ServiceInfoModule> serviceInfoModules) {

    PreServiceInfo preServiceInfo = new PreServiceInfo();
    serviceInfoModules.stream().filter(o -> o instanceof PreServiceInfoMultiSource)
        .map(PreServiceInfoMultiSource.class::cast).map(o -> o.getPreServiceInfo(id))
        .forEach(preServiceInfo::addAll);
    return preServiceInfo;
  }

  /**
   * Constructor.
   */
  public Message44Handler(final SecureRandom secureRandom, final SessionStorage sessionStorage,
      final KeyExchangeDecoder keyExchangeDecoder, Set<ServiceInfoModule> serviceInfoModules) {
    this.secureRandom = secureRandom;
    this.sessionStorage = sessionStorage;
    this.keyExchangeDecoder = keyExchangeDecoder;
    this.serviceInfoModules = serviceInfoModules;
  }

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }

  /**
   * Performs operations as per Type 44 (TO2.ProveDevice) for Transfer Ownership Protocol 2,
   * and returns encoded Type 45 (TO2.GetNextDeviceServiceInfo) as response.
   *
   * @param request               String request containing Type 44. Errors out otherwise.
   * @param sessionId             Identifier for which requestBody is processed.
   * @return                      String response containing Type 45.
   * @throws SdoProtocolException {@link SdoProtocolException} when an exception is thrown.
   */
  public String onPost(final String request, final String sessionId) throws SdoProtocolException {
    try {
      if (null == request || null == sessionId) {
        throw new IOException("invalid request");
      }
      getLogger().debug("Processing input " + request + "\n for " + sessionId);

      final To2DeviceSessionInfo session;
      session = getSessionStorage().load(sessionId);

      // if any instance is corrupted/absent, the session data is unavailable, so terminate the
      // connection.
      if (null != session && (!(session.getMessage41Store() instanceof Message41Store)
          || (null == session.getMessage41Store()))) {
        throw new IOException("missing session information for " + sessionId);
      }

      final SignatureBlock signedProveDevice =
          new SignatureBlockCodec.Decoder(null).decode(CharBuffer.wrap(request));
      final CharBuffer bo = signedProveDevice.getBo();
      final To2ProveDevice proveDevice =
          new To2ProveDeviceCodec().decoder().apply(bo.asReadOnlyBuffer());

      final OwnershipProxy proxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
          .decode(CharBuffer.wrap(session.getMessage41Store().getOwnershipProxy()));
      if (null == proxy) {
        throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
            proveDevice.getType(), "OwnershipVoucher must not be null"));
      }

      PublicKey pk = signedProveDevice.getPk();

      if (pk instanceof EpidKey) {
        EpidSecurityProvider.setEpidOptions(getEpidOptions().getEpidOnlineUrl(),
            getEpidOptions().getTestMode());
        EpidSecurityProvider.load();

      } else { // pk not epid

        // 5.6.6 non-epid device keys must result in a null TO2.ProveDevice.pk
        if (null == pk) {
          CertPath certPath = proxy.getDc();

          if (null != certPath) {
            List<? extends Certificate> certs = certPath.getCertificates();

            if (!certs.isEmpty()) {
              pk = certs.get(0).getPublicKey();

            } else { // certpath is empty
              throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
                  proveDevice.getType(), "OwnershipVoucher.dc must not be empty"));
            }

          } else { // certpath is null
            throw new SdoProtocolException(
                new SdoError(SdoErrorCode.MessageRefused, proveDevice.getType(),
                    "TO2.ProveDevice.pk and OwnershipVoucher.dc must not both be null"));
          }

        } else { // pk != null
          throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
              proveDevice.getType(), "TO2.ProveDevice.pk must be an EPID key, or null"));
        } // pk null?
      } // pk epid?

      final AlgorithmParameterSpec sigParams;
      if (pk instanceof EpidKey) {
        sigParams = new EpidSignatureParameterSpec(proveDevice.getN6(), proveDevice.getAi());
      } else {
        sigParams = null;
      }

      final boolean isVerified =
          Signatures.verifySignature(Buffers.unwrap(US_ASCII.encode(bo.duplicate())),
              Buffers.unwrap(signedProveDevice.getSg()), pk, sigParams);

      if (!isVerified) {
        SdoError err =
            new SdoError(SdoErrorCode.MessageRefused, proveDevice.getType(), "signature rejected");
        throw new SdoProtocolException(err);
      }

      final Nonce n6 = new Nonce(CharBuffer.wrap(session.getMessage41Store().getN6()));
      if (!Objects.equals(n6, proveDevice.getN6())) {
        SdoError err =
            new SdoError(SdoErrorCode.MessageRefused, proveDevice.getType(), "nonce rejected");
        throw new SdoProtocolException(err);
      }

      final To2CipherContext cipherContext =
          new To2CipherContextFactory(getKeyExchangeDecoder(), getSecureRandom())
              .build(session.getMessage41Store(), proveDevice.getXb().duplicate());

      // The presence of the mandatory SDO-DEV service info module means the device will always
      // send at least one service info message. We don't need to worry about the corner case
      // of nn == 0.
      final int nn = 0;
      final PreServiceInfo preServiceInfo =
          buildPreServiceInfo(proxy.getOh().getG(), getServiceInfoModules());

      final To2GetNextDeviceServiceInfo getNextDeviceServiceInfo =
          new To2GetNextDeviceServiceInfo(nn, preServiceInfo);
      StringWriter writer = new StringWriter();
      new To2GetNextDeviceServiceInfoCodec().encoder().apply(writer, getNextDeviceServiceInfo);
      String responseBody = writer.toString();
      getLogger().info(responseBody);

      final EncryptedMessage ownerEncryptedMessage =
          cipherContext.write(US_ASCII.encode(responseBody));
      writer = new StringWriter();
      new EncryptedMessageCodec().encoder().apply(writer, ownerEncryptedMessage);
      responseBody = writer.toString();

      final StringWriter xbWriter = new StringWriter();
      new KexParamCodec().encoder().apply(xbWriter, proveDevice.getXb());

      final StringWriter ctrNonceWriter = new StringWriter();
      new ByteArrayCodec().encoder().apply(ctrNonceWriter,
          ByteBuffer.wrap(cipherContext.getCtrNonce()));

      final To2DeviceSessionInfo to2DeviceSessionInfo = new To2DeviceSessionInfo();

      final Message45Store message45Store = new Message45Store(proveDevice.getN7().toString(),
          proveDevice.getNn(), xbWriter.toString());
      final DeviceCryptoInfo deviceCryptoInfo =
          new DeviceCryptoInfo(ctrNonceWriter.toString(), cipherContext.getCtrCounter());
      to2DeviceSessionInfo.setMessage45Store(message45Store);
      to2DeviceSessionInfo.setDeviceCryptoInfo(deviceCryptoInfo);
      getSessionStorage().store(sessionId, to2DeviceSessionInfo);

      getLogger().debug("Returning response: " + responseBody + "\n for " + sessionId);
      return responseBody;
    } catch (SdoProtocolException sp) {
      getLogger().debug(sp.getMessage(), sp);
      throw sp;
    } catch (Exception e) {
      getLogger().debug(e.getMessage(), e);
      throw new SdoProtocolException(
          new SdoError(SdoErrorCode.InternalError, MessageType.TO2_PROVE_DEVICE, e.getMessage()),
          e);
    }
  }

  private EpidOptionBean getEpidOptions() {
    return epidOptions;
  }

  public void setEpidOptions(EpidOptionBean epidOptions) {
    this.epidOptions = epidOptions;
  }

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private Set<ServiceInfoModule> getServiceInfoModules() {
    return Objects.requireNonNull(serviceInfoModules);
  }

  private SessionStorage getSessionStorage() {
    return Objects.requireNonNull(sessionStorage);
  }

  private KeyExchangeDecoder getKeyExchangeDecoder() {
    return this.keyExchangeDecoder;
  }
}
