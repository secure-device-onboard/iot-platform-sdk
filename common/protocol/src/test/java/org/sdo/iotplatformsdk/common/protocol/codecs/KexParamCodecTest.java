// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.KexParamCodec;

class KexParamCodecTest {

  KexParamCodec kexParamCodec;
  ByteBuffer out;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    kexParamCodec = new KexParamCodec();
    out = ByteBuffer.allocate(8);
  }

  @Test
  void test_Encoder() throws IOException {

    kexParamCodec.encoder().apply(writer, out);
  }

  @Test
  void test_Decoder() throws IOException {

    ByteBuffer decoded = kexParamCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
    assertEquals(out, decoded);
  }
}
