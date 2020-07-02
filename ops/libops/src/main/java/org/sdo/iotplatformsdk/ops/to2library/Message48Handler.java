// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.EncryptedMessageCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetNextOwnerServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2OwnerServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextOwnerServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2OwnerServiceInfo;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message48Handler {

  private final SessionStorage sessionStorage;
  private Set<ServiceInfoModule> serviceInfoModules = new HashSet<>();
  private final SecureRandom secureRandom;
  private final KeyExchangeDecoder keyExchangeDecoder;

  /**
   * Constructor.
   */
  public Message48Handler(SessionStorage sessionStorage, SecureRandom secureRandom,
      KeyExchangeDecoder keyExchangeDecoder, Set<ServiceInfoModule> serviceInfoModules) {
    this.sessionStorage = sessionStorage;
    this.secureRandom = secureRandom;
    this.keyExchangeDecoder = keyExchangeDecoder;
    this.serviceInfoModules = serviceInfoModules;
  }

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private SessionStorage getSessionStorage() {
    return Objects.requireNonNull(sessionStorage);
  }

  private Set<ServiceInfoModule> getServiceInfoModules() {
    return Objects.requireNonNull(serviceInfoModules);
  }

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }

  private KeyExchangeDecoder getKeyExchangeDecoder() {
    return this.keyExchangeDecoder;
  }

  /**
   * Performs operations as per Type 48 (TO2.GetNextOwnerServiceInfo) for Transfer Ownership
   * Protocol 2, and returns encoded Type 49 (TO2.OwnerServiceInfo) as response.
   *
   * @param request               String request containing Type 48. Errors out otherwise.
   * @param sessionId             Identifier for which requestBody is processed.
   * @return                      String response containing Type 49.
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
          || (null == session.getMessage41Store())
          || !(session.getMessage45Store() instanceof Message45Store)
          || (null == session.getMessage45Store())
          || !(session.getMessage47Store() instanceof Message47Store)
          || (null == session.getMessage47Store())
          || !(session.getDeviceCryptoInfo() instanceof DeviceCryptoInfo)
          || null == session.getDeviceCryptoInfo())) {
        throw new IOException("missing session information for " + sessionId);
      }

      final ByteBuffer xb =
          new KexParamCodec().decoder().apply(CharBuffer.wrap(session.getMessage45Store().getXb()));
      final To2CipherContext cipherContext =
          new To2CipherContextFactory(getKeyExchangeDecoder(), getSecureRandom())
              .build(session.getMessage41Store(), xb.duplicate());

      final EncryptedMessageCodec encryptedMessageCodec = new EncryptedMessageCodec();
      final EncryptedMessage deviceEncryptedMessage =
          encryptedMessageCodec.decoder().apply(CharBuffer.wrap(request));

      final ByteBuffer decryptedBytes = cipherContext.read(deviceEncryptedMessage);
      final CharBuffer decryptedText = US_ASCII.decode(decryptedBytes);
      getLogger().info(decryptedText.toString());

      final To2DeviceSessionInfo to2DeviceSessionInfo = new To2DeviceSessionInfo();
      final To2GetNextOwnerServiceInfo getNextOwnerServiceInfo =
          new To2GetNextOwnerServiceInfoCodec().decoder().apply(decryptedText);

      final int nn = getNextOwnerServiceInfo.getNn();
      final String marshalledServiceInfo;
      if (!getServiceInfoModules().isEmpty()) {
        try {
          final OwnershipProxy proxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
              .decode(CharBuffer.wrap(session.getMessage41Store().getOwnershipProxy()));
          final OwnerServiceInfoHandler ownerServiceInfoHandler =
              new OwnerServiceInfoHandler(getServiceInfoModules(), proxy.getOh().getG());
          marshalledServiceInfo = ownerServiceInfoHandler.getNextOwnerServiceInfoEntry(nn);
          if (null == marshalledServiceInfo) {
            throw new RuntimeException();
          }
        } catch (Exception e) {
          throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
              getNextOwnerServiceInfo.getType(), "too many serviceinfo requests"));
        }
      } else {
        marshalledServiceInfo = "";
      }

      final To2OwnerServiceInfo ownerServiceInfo =
          new To2OwnerServiceInfo(nn, marshalledServiceInfo);
      StringWriter writer = new StringWriter();
      new To2OwnerServiceInfoCodec().encoder().apply(writer, ownerServiceInfo);
      String responseBody = writer.toString();
      getLogger().info(responseBody);

      // if the CTR nonce is null, it means that the session's IV has been lost/corrupted. For CBC
      // it should have been all 0s, while for CTR, it should contain the current nonce.
      if (null != session.getDeviceCryptoInfo().getCtrNonce()) {
        final ByteBuffer nonce = new ByteArrayCodec().decoder()
            .apply(CharBuffer.wrap(session.getDeviceCryptoInfo().getCtrNonce()));
        cipherContext.setCtrNonce(nonce.array());
        cipherContext.setCtrCounter(session.getDeviceCryptoInfo().getCtrCounter());
      } else {
        throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
            getNextOwnerServiceInfo.getType(), "no cipher initialization vector found"));
      }
      final EncryptedMessage ownerEncryptedMessage =
          cipherContext.write(US_ASCII.encode(responseBody));
      writer = new StringWriter();
      encryptedMessageCodec.encoder().apply(writer, ownerEncryptedMessage);
      responseBody = writer.toString();

      final StringWriter ctrNonceWriter = new StringWriter();
      new ByteArrayCodec().encoder().apply(ctrNonceWriter,
          ByteBuffer.wrap(cipherContext.getCtrNonce()));
      final DeviceCryptoInfo deviceCryptoInfo =
          new DeviceCryptoInfo(ctrNonceWriter.toString(), cipherContext.getCtrCounter());
      to2DeviceSessionInfo.setDeviceCryptoInfo(deviceCryptoInfo);
      getSessionStorage().store(sessionId, to2DeviceSessionInfo);
      getLogger().debug("Returning response: " + responseBody + "\n for " + sessionId);
      return responseBody;
    } catch (SdoProtocolException sp) {
      getLogger().debug(sp.getMessage(), sp);
      throw sp;
    } catch (Exception e) {
      getLogger().debug(e.getMessage(), e);
      throw new SdoProtocolException(new SdoError(SdoErrorCode.InternalError,
          MessageType.TO2_GET_NEXT_OWNER_SERVICE_INFO, e.getMessage()), e);
    }
  }
}
