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
import org.sdo.iotplatformsdk.common.protocol.codecs.AppIdCodec;

class AppIdCodecTest {

  AppIdCodec appIdCodec;
  ByteBuffer out;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    appIdCodec = new AppIdCodec();
    out = ByteBuffer.allocate(8);
  }

  @Test
  void test_Encoder() throws IOException {

    appIdCodec.encoder().apply(writer, out);
  }

  @Test
  void test_Decoder() throws IOException {

    ByteBuffer decoded = appIdCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
    assertEquals(out, decoded);
  }
}
