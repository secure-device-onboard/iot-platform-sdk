// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.PkX509Codec;

class PkX509CodecTest {

  KeyPairGenerator keyGen;
  KeyPair keypair;
  PublicKey publicKey;
  PkX509Codec pkX509Codec;
  ByteBuffer out;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    keypair = keyGen.genKeyPair();
    pkX509Codec = new PkX509Codec();
    publicKey = keypair.getPublic();
    out = ByteBuffer.allocate(8);
  }

  @Test
  void test_Encoder() throws IOException {

    pkX509Codec.encoder().apply(writer, publicKey);
  }

  @Test
  void test_Decoder() throws IOException {

    pkX509Codec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
