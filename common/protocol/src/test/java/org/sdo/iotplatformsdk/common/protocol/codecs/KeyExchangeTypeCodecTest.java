// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.KeyExchangeTypeCodec;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;

public class KeyExchangeTypeCodecTest {

  KeyExchangeTypeCodec keyExchangeTypecodec;
  KeyExchangeType keyExchangeType;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    keyExchangeTypecodec = new KeyExchangeTypeCodec();
  }

  @Test
  public void test_Encoder() throws IOException {
    keyExchangeTypecodec.encoder().apply(writer, KeyExchangeType.ASYMKEX);
  }

  @Test
  public void test_Decoder() throws IOException {
    keyExchangeTypecodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
