// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.CipherTypeCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.StringCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2HelloDeviceCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2ProveOpHdrCodec;
import org.sdo.iotplatformsdk.common.protocol.config.EpidOptionBean;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.AsymmetricKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.DiffieHellmanKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.EcdhKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.KeyExchange;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.SigInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To2HelloDevice;
import org.sdo.iotplatformsdk.common.protocol.types.To2ProveOpHdr;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.epid.EpidSecurityProvider;
import org.sdo.iotplatformsdk.ops.epid.SigInfoResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Message40Handler {

  private final SignatureServiceFactory signatureServiceFactory;
  private final OwnerEventHandler ownerEventHandler;
  private final OwnershipProxyStorage ownershipProxyStorage;
  private final SecureRandom secureRandom;
  private final SessionStorage sessionStorage;
  private final KeyExchangeDecoder keyExchangeDecoder;

  /**
   * Constructor.
   */
  public Message40Handler(final SignatureServiceFactory signatureServiceFactory,
      OwnerEventHandler ownerEventHandler, final OwnershipProxyStorage ownershipProxyStorage,
      final SecureRandom secureRandom, final SessionStorage sessionStorage,
      final KeyExchangeDecoder keyExchangeDecoder) {
    this.signatureServiceFactory = signatureServiceFactory;
    this.ownerEventHandler = ownerEventHandler;
    this.ownershipProxyStorage = ownershipProxyStorage;
    this.secureRandom = secureRandom;
    this.sessionStorage = sessionStorage;
    this.keyExchangeDecoder = keyExchangeDecoder;
  }

  /**
   * Set the value of {@link EpidOptionBean}.
   *
   * @param epidOptions {@link EpidOptionBean}
   */
  public void setEpidOptions(EpidOptionBean epidOptions) {
    EpidSecurityProvider.setEpidOptions(epidOptions.getEpidOnlineUrl(), epidOptions.getTestMode());
  }

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private Optional<OwnerEventHandler> getOwnerEventHandler() {
    return Optional.ofNullable(ownerEventHandler);
  }

  private OwnershipProxyStorage getOwnershipProxyStorage() {
    return Objects.requireNonNull(ownershipProxyStorage);
  }

  private SecureRandom getSecureRandom() {
    return Objects.requireNonNull(secureRandom);
  }

  private SessionStorage getSessionStorage() {
    return Objects.requireNonNull(sessionStorage);
  }

  private KeyExchangeDecoder getKeyExchangeDecoder() {
    return this.keyExchangeDecoder;
  }

  /**
   * Performs operations as per Type 40 (TO2.HelloDevice) for Transfer Ownership Protocol 2, and
   * returns encoded Type 41 (TO2.ProveOPHdr) as response.
   *
   * @param requestBody           String request containing Type 40. Errors out otherwise.
   * @return                      String response containing Type 41.
   * @throws SdoProtocolException {@link SdoProtocolException} when an exception is thrown.
   */
  public String onPost(final String requestBody) throws SdoProtocolException {
    try {
      if (null == requestBody) {
        throw new IOException("invalid request");
      }
      getLogger().debug(requestBody);
      final To2HelloDevice helloDevice =
          new To2HelloDeviceCodec().decoder().apply(CharBuffer.wrap(requestBody));

      // Do we recognize this UUID?
      final UUID g2 = helloDevice.getG2();
      final OwnershipProxyStorage proxyMap = getOwnershipProxyStorage();
      final OwnershipProxy proxy;
      String ecdhPublicKey = null;
      String ecdhPrivateKey = null;
      String ecdhRandom = null;
      String dhPublicKey = null;
      String dhPrivateKey = null;
      String asymRandom = null;

      if (null != proxyMap) {
        proxy = proxyMap.load(g2);

      } else {
        proxy = null;
      }

      if (null == proxy) {
        SdoError err = new SdoError(SdoErrorCode.ResourceNotFound, helloDevice.getType(),
            "proxy " + g2 + " not found");
        throw new SdoProtocolException(err);
      }

      // Initialize the key exchange, which won't be performed until step 44.
      final KeyExchange keyExchange =
          getKeyExchangeDecoder().getKeyExchangeType(helloDevice.getKx(), g2);

      int sz = proxy.getEn().size();
      OwnershipProxyHeader oh = proxy.getOh();
      HashMac hmac = proxy.getHmac();
      Nonce n5 = helloDevice.getN5();
      Nonce n6 = new Nonce(getSecureRandom());
      SigInfo eb;
      try {
        eb = new SigInfoResponder().apply(helloDevice.getEa());

      } catch (UnsatisfiedLinkError e) {
        throw new SdoProtocolException(
            new SdoError(SdoErrorCode.InternalError, helloDevice.getType(), e.getMessage()), e);
      }

      ByteBuffer xa = keyExchange.getMessage();

      To2ProveOpHdr proveOpHdr = new To2ProveOpHdr(sz, oh, hmac, n5, n6, eb, xa);
      StringWriter writer = new StringWriter();
      new To2ProveOpHdrCodec().encoder().apply(writer, proveOpHdr);

      final SignatureBlock signedProveOpHdr =
          signatureServiceFactory.build(g2).sign(writer.toString()).get();

      writer = new StringWriter();
      final PublicKeyCodec.Encoder pkEncoder = new PublicKeyCodec.Encoder(oh.getPe());
      final SignatureBlockCodec.Encoder sgEncoder = new SignatureBlockCodec.Encoder(pkEncoder);
      sgEncoder.encode(writer, signedProveOpHdr);
      final String responseBody = writer.toString();

      writer = new StringWriter();
      new OwnershipProxyCodec.OwnershipProxyEncoder().encode(writer, proxy);

      StringWriter xaWriter = new StringWriter();
      new KexParamCodec().encoder().apply(xaWriter, xa);

      StringWriter csWriter = new StringWriter();
      new CipherTypeCodec().encoder().apply(csWriter, helloDevice.getCs());

      StringWriter kxWriter = new StringWriter();
      new StringCodec().encoder().apply(kxWriter, helloDevice.getKx().toString());

      if (keyExchange instanceof EcdhKeyExchange) {
        ecdhPublicKey = ((EcdhKeyExchange) keyExchange).getMyPublicKey();
        ecdhPrivateKey = ((EcdhKeyExchange) keyExchange).getMyPrivateKey();
        ecdhRandom = ((EcdhKeyExchange) keyExchange).getMyRandomAsString();
      } else if (keyExchange instanceof DiffieHellmanKeyExchange) {
        dhPublicKey = ((DiffieHellmanKeyExchange) keyExchange).getPublicKey();
        dhPrivateKey = ((DiffieHellmanKeyExchange) keyExchange).getPrivateKey();
      } else if (keyExchange instanceof AsymmetricKeyExchange) {
        final StringWriter asymWriter = new StringWriter();
        new ByteArrayCodec().encoder().apply(asymWriter,
            ByteBuffer.wrap(((AsymmetricKeyExchange) keyExchange).getXa()));
        asymRandom = asymWriter.toString();
      }

      final Message41Store message41Store = new Message41Store(n6.toString(), kxWriter.toString(),
          writer.toString(), csWriter.toString(), ecdhPublicKey, ecdhPrivateKey, ecdhRandom,
          dhPublicKey, dhPrivateKey, asymRandom);

      final To2DeviceSessionInfo to2DeviceSessionInfo = new To2DeviceSessionInfo();
      to2DeviceSessionInfo.setMessage41Store(message41Store);
      getSessionStorage().store(g2, to2DeviceSessionInfo);
      getLogger().debug("Returning response: " + responseBody);

      getOwnerEventHandler().ifPresent((handler) -> handler.call(new To2BeginEvent(proxy)));

      return responseBody;
    } catch (SdoProtocolException sp) {
      getLogger().debug(sp.getMessage(), sp);
      throw sp;
    } catch (Exception e) {
      getLogger().debug(e.getMessage(), e);
      throw new SdoProtocolException(
          new SdoError(SdoErrorCode.InternalError, MessageType.TO2_HELLO_DEVICE, e.getMessage()),
          e);
    }
  }
}
