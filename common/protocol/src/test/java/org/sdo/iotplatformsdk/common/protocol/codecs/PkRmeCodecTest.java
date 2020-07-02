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
import org.sdo.iotplatformsdk.common.protocol.codecs.PkEpidCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.PkRmeCodec;

class PkRmeCodecTest {

  KeyPairGenerator keyGen;
  KeyPair keypair;
  PkEpidCodec pkEpidCodec;
  PkRmeCodec pkRmeCodec;
  PublicKey pk;
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
    pk = keypair.getPublic();
    pkRmeCodec = new PkRmeCodec();
    out = ByteBuffer.allocate(8);
  }

  @Test
  void test_Encoder() throws IOException {

    pkRmeCodec.encoder().apply(writer, pk);
  }

  @Test
  void test_Decoder() throws IOException {

    pkRmeCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
