// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodeCodec;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;

class SdoErrorCodeCodecTest {

  SdoErrorCodeCodec sdoErrorCodeCodec;
  SdoErrorCode sdoErrorCode;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {
    sdoErrorCodeCodec = new SdoErrorCodeCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    sdoErrorCodeCodec.encoder().apply(writer, SdoErrorCode.InvalidGuid);
  }

  @Test
  void test_Decoder() throws IOException {
    sdoErrorCodeCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
