// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.Codec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SignatureTypeCodec;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureType;

class SignatureTypeCodecTest {

  SignatureTypeCodec signatureTypeCodec;
  SignatureType signatureType;
  static StringWriter writer;
  Codec<Number> codeclength;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {
    signatureTypeCodec = new SignatureTypeCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    signatureTypeCodec.encoder().apply(writer, SignatureType.ECDSA_P_256);
  }

  @Test
  void test_Decoder() throws IOException {

    signatureTypeCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
