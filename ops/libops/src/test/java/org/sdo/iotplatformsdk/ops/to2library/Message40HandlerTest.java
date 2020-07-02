// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.Codec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureBlockCodec;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactory;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureService;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.EcdhKeyExchange;
import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.KeyExchange;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;

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
  String requestEntity;
  SecureRandom random;
  SecureRandomFactory secureRandom;
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
  void beforeEach() throws IOException {

    KeyPairGenerator kpg;
    try {
      kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(1024);
      keys = kpg.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      // do nothing.
    }

    asymKexCodec = Mockito.mock(AsymKexCodec.class);
    codecSignatureBlock = Mockito.mock(Codec.Encoder.class);
    en = Mockito.mock(Codec.Encoder.class);
    futureSignatureBlock = Mockito.mock(Future.class);
    ownershipProxyStorage = Mockito.mock(OwnershipProxyStorage.class);
    ownershipProxy = new OwnershipProxy();
    ownerEventHandler = Mockito.mock(OwnerEventHandler.class);
    random = new SecureRandom();
    secureRandom = new SecureRandomFactory();
    sessionStorage = Mockito.mock(SessionStorage.class);
    sgLenCodec = Mockito.mock(Codec.class);
    signatureBlock = Mockito.mock(SignatureBlock.class);
    signatureBlockCodec = Mockito.mock(SignatureBlockCodec.class);
    signatureServiceFactory = Mockito.mock(SignatureServiceFactory.class);
    signatureService = Mockito.mock(SignatureService.class);
    to2ProvingOwner = Mockito.mock(To2ProvingOwner.class);
    keyExchangeDecoder = Mockito.mock(KeyExchangeDecoder.class);
    writer = new StringWriter();
    keyExchange = new EcdhKeyExchange.P256(random);

    message40Handler = new Message40Handler(signatureServiceFactory, ownerEventHandler,
        ownershipProxyStorage, random, sessionStorage, keyExchangeDecoder);

    message40 = "{\"g2\":\"C0YHMh2qTlK803RUYmgk6g==\",\"n5\":\""
        + "QkJNMS58CqbtZqalTnxbDg==\",\"pe\":3,\"kx\":\"ECDH\","
        + "\"cs\":\"AES128/CTR/HMAC-SHA256\",\"eA\":[13,0,\"\"]}";

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

    String message41 = message40Handler.onPost(message40);
    try {
      message40Handler.onPost(message40);
    } catch (Exception e) {
      // do nothing.
    }
  }

  @Test
  void test_Message40_BadRequest() {

    String message41;
    try {
      message40Handler.onPost(requestEntity);
    } catch (Exception e) {
      // do nothing.
    }

  }
}
