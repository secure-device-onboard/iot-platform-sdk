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

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.sdo.iotplatformsdk.common.protocol.codecs.ByteArrayCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.CipherTypeCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.PublicKeyCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.StringCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2HelloDeviceCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2ProveOpHdrCodec;
import org.sdo.iotplatformsdk.common.protocol.config.ClientHttpRequestFactoryCreatingFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.EpidOptionBean;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.rest.AuthToken;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.AsymmetricKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.DiffieHellmanKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.EcdhKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.KeyExchange;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Message40Handler {

  private final SignatureServiceFactory signatureServiceFactory;
  private OwnerEventHandler ownerEventHandler;
  private OwnershipProxyStorage ownershipProxyStorage;
  private SecureRandom secureRandom;
  private SessionStorage sessionStorage;
  private KeyExchangeDecoder keyExchangeDecoder;

  /**
   * Constructor.
   *
   * @param signatureServiceFactory {@link SignatureServiceFactory}
   */
  public Message40Handler(final SignatureServiceFactory signatureServiceFactory) {
    this.signatureServiceFactory = signatureServiceFactory;
  }

  /**
   * The REST endpoint for message 40, TO2.HelloDevice.
   */
  @PostMapping("mp/113/msg/40")
  @SuppressWarnings("unused")
  public Callable<ResponseEntity<?>> onPostAsync(final RequestEntity<String> requestEntity) {
    return () -> onPost(requestEntity);
  }

  @Autowired
  public void setClientHttpRequestFactoryCreatingFactoryBean(
      ClientHttpRequestFactoryCreatingFactoryBean httpRequestFactoryCreatingFactoryBean) {
    EpidSecurityProvider.setHttpRequestFactory(httpRequestFactoryCreatingFactoryBean.getObject());
  }

  @Autowired
  public void setEpidOptions(EpidOptionBean epidOptions) {
    EpidSecurityProvider.setEpidOptions(epidOptions.getEpidOnlineUrl(), epidOptions.getTestMode());
  }

  private Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  private Optional<OwnerEventHandler> getOwnerEventHandler() {
    return Optional.ofNullable(ownerEventHandler);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setOwnerEventHandler(OwnerEventHandler ownerEventHandler) {
    this.ownerEventHandler = ownerEventHandler;
  }

  private OwnershipProxyStorage getOwnershipProxyStorage() {
    return Objects.requireNonNull(ownershipProxyStorage);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setOwnershipProxyStorage(OwnershipProxyStorage ownershipProxyStorage) {
    this.ownershipProxyStorage = ownershipProxyStorage;
  }

  private SecureRandom getSecureRandom() {
    return Objects.requireNonNull(secureRandom);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setSecureRandom(SecureRandom secureRandom) {
    this.secureRandom = secureRandom;
  }

  private SessionStorage getSessionStorage() {
    return Objects.requireNonNull(sessionStorage);
  }

  @Autowired
  @SuppressWarnings("unused")
  public void setSessionStorage(SessionStorage sessionStorage) {
    this.sessionStorage = sessionStorage;
  }

  @Autowired
  public void setKeyExchangeDecoder(KeyExchangeDecoder keyExchangeDecoder) {
    this.keyExchangeDecoder = keyExchangeDecoder;
  }

  private KeyExchangeDecoder getKeyExchangeDecoder() {
    return this.keyExchangeDecoder;
  }

  /**
   * Receives message 40 from device Decodes the Request body Verifies the GUID is present in
   * Ownership voucher Store, if not raises an error If GUID is successfully verified,
   * TO2.ProveOPHdr (message 41) is composed The message is then encoded and sent to the device
   */
  private ResponseEntity<?> onPost(RequestEntity<String> requestEntity)
      throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
      ExecutionException, InterruptedException, TimeoutException {

    getLogger().info(requestEntity.toString());
    final String requestBody = null != requestEntity.getBody() ? requestEntity.getBody() : "";

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
      StringWriter writer = new StringWriter();
      new SdoErrorCodec().encoder().apply(writer, err);
      return ResponseEntity.badRequest().body(writer.toString());
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

    ResponseEntity<?> responseEntity =
        ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, new AuthToken(g2).toString())
            .contentType(MediaType.APPLICATION_JSON).body(responseBody);
    getLogger().info(responseEntity.toString());

    getOwnerEventHandler().ifPresent((handler) -> handler.call(new To2BeginEvent(proxy)));

    return responseEntity;
  }
}
