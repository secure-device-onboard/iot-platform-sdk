// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetOpNextEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetOpNextEntry;

class To2GetOpNextEntryCodecTest {

  To2GetOpNextEntryCodec to2GetOpNextEntryCodec;
  static StringWriter writer;
  To2GetOpNextEntry to2GetOpNextEntry;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    to2GetOpNextEntry = new To2GetOpNextEntry(1);
    to2GetOpNextEntryCodec = new To2GetOpNextEntryCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    to2GetOpNextEntryCodec.encoder().apply(writer, to2GetOpNextEntry);
  }

  @Test
  void test_Decoder() throws IOException {

    to2GetOpNextEntryCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
