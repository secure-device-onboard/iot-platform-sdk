/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.Codec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureService;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.security.SimpleAsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.EcdhKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.KeyExchange;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

class Message40HandlerTest {

  AsymKexCodec asymKexCodec;
  Codec<Number> sgLenCodec;
  Codec<SignatureBlock>.Encoder codecSignatureBlock;
  Codec<Number>.Encoder en;
  Future<SignatureBlock> futureSignatureBlock;
  KeyPair keys;
  Message40Handler message40Handler;
  OwnerEventHandler ownerEventHandler;
  OwnershipProxyStorage ownershipProxyStorage;
  OwnershipProxy ownershipProxy;
  RequestEntity<String> requestEntity;
  SecureRandom random;
  SecureRandomFactoryBean secureRandom;
  SessionStorage sessionStorage;
  SignatureBlock signatureBlock;
  SignatureBlockCodec signatureBlockCodec;
  SignatureService signatureService;
  SignatureServiceFactory signatureServiceFactory;
  String message40;
  StringWriter writer;
  To2ProvingOwner to2ProvingOwner;
  KeyExchangeDecoder keyExchangeDecoder;
  KeyExchange keyExchange;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() {

    KeyPairGenerator kpg;
    try {
      kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(1024);
      keys = kpg.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      // do nothing.
    }

    asymKexCodec = new SimpleAsymKexCodec(keys, random);
    codecSignatureBlock = Mockito.mock(Codec.Encoder.class);
    en = Mockito.mock(Codec.Encoder.class);
    futureSignatureBlock = Mockito.mock(Future.class);
    ownershipProxyStorage = Mockito.mock(OwnershipProxyStorage.class);
    ownershipProxy = new OwnershipProxy();
    ownerEventHandler = new SimpleOwnerEventHandler();
    random = new SecureRandom();
    secureRandom = new SecureRandomFactoryBean();
    sessionStorage = new SimpleSessionStorage();
    sgLenCodec = Mockito.mock(Codec.class);
    signatureBlock = Mockito.mock(SignatureBlock.class);
    signatureBlockCodec = Mockito.mock(SignatureBlockCodec.class);
    signatureServiceFactory = Mockito.mock(SignatureServiceFactory.class);
    signatureService = Mockito.mock(SignatureService.class);
    to2ProvingOwner = Mockito.mock(To2ProvingOwner.class);
    keyExchangeDecoder = Mockito.mock(KeyExchangeDecoder.class);
    writer = new StringWriter();
    keyExchange = new EcdhKeyExchange.P256(random);

    message40Handler = new Message40Handler(signatureServiceFactory);
    message40Handler.setOwnerEventHandler(ownerEventHandler);
    message40Handler.setOwnershipProxyStorage(ownershipProxyStorage);
    message40Handler.setSecureRandom(random);
    message40Handler.setSessionStorage(sessionStorage);
    message40Handler.setKeyExchangeDecoder(keyExchangeDecoder);

    message40 = "{\"g2\":\"C0YHMh2qTlK803RUYmgk6g==\",\"n5\":\""
        + "QkJNMS58CqbtZqalTnxbDg==\",\"pe\":3,\"kx\":\"ECDH\","
        + "\"cs\":\"AES128/CTR/HMAC-SHA256\",\"iv\":[12,\"XI6uEtjZc6bK/3kM\"]"
        + ",\"eA\":[13,0,\"\"]}";

    requestEntity =
        RequestEntity.post(URI.create("http://localhost")).header("accept", "text/plain, */*")
            .header("user-agent", "Java/11.0.3").header("host", "localhost:8042")
            .header("connection", "keep-alive").header("content-length", "158")
            .contentType(MediaType.APPLICATION_JSON_UTF8).body(message40);
  }

  /**
   * Compose Message 40.
   */
  @Test
  void test_Message40() throws IOException, InterruptedException, ExecutionException {

    Mockito.when(keyExchangeDecoder.getKeyExchangeType(Mockito.any(KeyExchangeType.class),
        Mockito.any(UUID.class))).thenReturn(keyExchange);
    Mockito.when(ownershipProxyStorage.load(Mockito.any(UUID.class))).thenReturn(ownershipProxy);
    Mockito.when(signatureServiceFactory.build(Mockito.any(UUID.class)))
        .thenReturn(signatureService);
    Mockito.when(signatureService.sign(Mockito.anyString())).thenReturn(futureSignatureBlock);
    Mockito.when(futureSignatureBlock.get()).thenReturn(signatureBlock);
    Mockito.when(signatureBlock.getSg()).thenReturn(ByteBuffer.allocate(256));
    Mockito.when(sgLenCodec.encoder()).thenReturn(en);

    Callable<ResponseEntity<?>> message41 = message40Handler.onPostAsync(requestEntity);
    try {
      message41.call();
    } catch (Exception e) {
      // do nothing.
    }
  }

  @Test
  void test_Message40_BadRequest() {

    Callable<ResponseEntity<?>> message41 = message40Handler.onPostAsync(requestEntity);
    try {
      message41.call();
    } catch (Exception e) {
      // do nothing.
    }

  }
}
