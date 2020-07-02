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
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.EncryptedMessageCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetNextDeviceServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2NextDeviceServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2SetupDeviceCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2SetupDeviceNohCodec;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextDeviceServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2NextDeviceServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2SetupDevice;
import org.sdo.iotplatformsdk.common.protocol.types.To2SetupDeviceNoh;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMultiSink;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoSink;
import org.sdo.iotplatformsdk.ops.to2library.SetupDeviceService.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message46Handler {

  private final SignatureServiceFactory signatureServiceFactory;
  private Set<ServiceInfoModule> serviceInfoModules = new HashSet<>();
  private final SessionStorage sessionStorage;
  private final SetupDeviceService setupDeviceService;
  private final SecureRandom secureRandom;
  private final KeyExchangeDecoder keyExchangeDecoder;

  /**
   * Constructor.
   */
  public Message46Handler(SignatureServiceFactory signatureServiceFactory,
      SessionStorage sessionStorage, SetupDeviceService setupDeviceService,
      SecureRandom secureRandom, KeyExchangeDecoder keyExchangeDecoder,
      Set<ServiceInfoModule> serviceInfoModules) {
    this.signatureServiceFactory = signatureServiceFactory;
    this.sessionStorage = sessionStorage;
    this.setupDeviceService = setupDeviceService;
    this.secureRandom = secureRandom;
    this.keyExchangeDecoder = keyExchangeDecoder;
    this.serviceInfoModules = serviceInfoModules;
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

  public SecureRandom getSecureRandom() {
    return secureRandom;
  }

  private KeyExchangeDecoder getKeyExchangeDecoder() {
    return this.keyExchangeDecoder;
  }

  /**
   * Performs operations as per Type 46 (TO2.NextDeviceServiceInfo) for Transfer Ownership Protocol
   * 2, and returns encoded Type 47 (TO2.SetupDevice) as response.
   *
   * @param request               String request containing Type 46. Errors out otherwise.
   * @param sessionId             Identifier for which requestBody is processed.
   * @return                      String response containing Type 47.
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

      final To2NextDeviceServiceInfo nextDeviceServiceInfo =
          new To2NextDeviceServiceInfoCodec().decoder().apply(decryptedText);

      final OwnershipProxy proxy = new OwnershipProxyCodec.OwnershipProxyDecoder()
          .decode(CharBuffer.wrap(session.getMessage41Store().getOwnershipProxy()));
      if (null == proxy) {
        throw new SdoProtocolException(new SdoError(SdoErrorCode.MessageRefused,
            nextDeviceServiceInfo.getType(), "OwnershipVoucher must not be null"));
      }

      for (Object serviceInfoObject : getServiceInfoModules()) {

        if (serviceInfoObject instanceof ServiceInfoSink) {
          final ServiceInfoSink sink = (ServiceInfoSink) serviceInfoObject;

          for (ServiceInfoEntry entry : nextDeviceServiceInfo.getDsi()) {
            sink.putServiceInfo(entry);
          }

        } else if (serviceInfoObject instanceof ServiceInfoMultiSink) {
          final ServiceInfoMultiSink sink = (ServiceInfoMultiSink) serviceInfoObject;
          final ServiceInfo serviceInfos = new ServiceInfo();
          for (ServiceInfoEntry entry : nextDeviceServiceInfo.getDsi()) {
            serviceInfos.add(entry);
          }
          sink.putServiceInfo(proxy.getOh().getG(), serviceInfos);

        }
      }

      String responseBody;
      final OwnershipProxy newProxy;
      int nn = nextDeviceServiceInfo.getNn() + 1;
      if (nn < session.getMessage45Store().getNn()) {
        final StringWriter writer = new StringWriter();
        final To2GetNextDeviceServiceInfo getNextDeviceServiceInfo =
            new To2GetNextDeviceServiceInfo(nn, new PreServiceInfo());
        new To2GetNextDeviceServiceInfoCodec().encoder().apply(writer, getNextDeviceServiceInfo);
        newProxy = null;
        responseBody = writer.toString();

      } else {

        final OwnershipProxy currentProxy = proxy;
        final Setup devSetup =
            setupDeviceService.setup(currentProxy.getOh().getG(), currentProxy.getOh().getR());
        final To2SetupDeviceNoh setupDeviceNoh = new To2SetupDeviceNoh(devSetup.r3(), devSetup.g3(),
            new Nonce(CharBuffer.wrap(session.getMessage45Store().getN7())));

        final StringWriter bo = new StringWriter();
        new To2SetupDeviceNohCodec().encoder().apply(bo, setupDeviceNoh);

        final SignatureBlock noh =
            signatureServiceFactory.build(proxy.getOh().getG()).sign(bo.toString()).get();

        final OwnerServiceInfoHandler ownerServiceInfoHandler =
            new OwnerServiceInfoHandler(getServiceInfoModules(), proxy.getOh().getG());
        final int osinn = ownerServiceInfoHandler.getOwnerServiceInfoEntryCount();

        final To2SetupDevice setupDevice = new To2SetupDevice(osinn, noh);
        final StringWriter writer = new StringWriter();
        final PublicKeyCodec.Encoder pkEncoder =
            new PublicKeyCodec.Encoder(currentProxy.getOh().getPe());
        final SignatureBlockCodec.Encoder sgEncoder = new SignatureBlockCodec.Encoder(pkEncoder);
        new To2SetupDeviceCodec.Encoder(sgEncoder).encode(writer, setupDevice);
        responseBody = writer.toString();

        final OwnershipProxyHeader currentOh = currentProxy.getOh();
        final OwnershipProxyHeader newOh = new OwnershipProxyHeader(currentOh.getPe(),
            devSetup.r3(), devSetup.g3(), currentOh.getD(), noh.getPk(), currentOh.getHdc());
        newProxy = new OwnershipProxy(newOh, new HashMac(MacType.NONE, ByteBuffer.allocate(0)),
            currentProxy.getDc(), new LinkedList<>());
      }
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
            nextDeviceServiceInfo.getType(), "no cipher initialization vector found"));
      }
      final ByteBuffer responseBodyBuf = US_ASCII.encode(responseBody);
      final EncryptedMessage ownerEncryptedMessage = cipherContext.write(responseBodyBuf);

      final StringWriter writer = new StringWriter();
      encryptedMessageCodec.encoder().apply(writer, ownerEncryptedMessage);
      responseBody = writer.toString();

      final StringWriter opWriter = new StringWriter();
      if (null != newProxy) {
        new OwnershipProxyCodec.OwnershipProxyEncoder().encode(opWriter, newProxy);
      }
      final StringWriter ctrNonceWriter = new StringWriter();
      new ByteArrayCodec().encoder().apply(ctrNonceWriter,
          ByteBuffer.wrap(cipherContext.getCtrNonce()));

      final DeviceCryptoInfo deviceCryptoInfo =
          new DeviceCryptoInfo(ctrNonceWriter.toString(), cipherContext.getCtrCounter());
      final Message47Store message47Store = new Message47Store(opWriter.toString());
      final To2DeviceSessionInfo to2DeviceSessionInfo = new To2DeviceSessionInfo();
      to2DeviceSessionInfo.setMessage47Store(message47Store);
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
          MessageType.TO2_NEXT_DEVICE_SERVICE_INFO, e.getMessage()), e);
    }
  }
}
