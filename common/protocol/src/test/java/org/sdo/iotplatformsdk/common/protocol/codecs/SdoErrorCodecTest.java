// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodec;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;

class SdoErrorCodecTest {

  SdoErrorCodec sdoErrorCodec;
  static StringWriter writer;
  SdoError sdoError;
  SdoErrorCode sdoErrorCode;
  MessageType messageType;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    sdoError = new SdoError(SdoErrorCode.OK, MessageType.DI_APP_START, "TEST");
    sdoErrorCodec = new SdoErrorCodec();
  }

  @Test
  void test_Encoder() throws IOException {

    sdoErrorCodec.encoder().apply(writer, sdoError);
  }

  @Test
  void test_Decoder() throws IOException {

    sdoErrorCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
