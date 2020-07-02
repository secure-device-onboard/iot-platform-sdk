// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.KeyTypeCodec;
import org.sdo.iotplatformsdk.common.protocol.types.KeyType;

class KeyTypeCodecTest {

  KeyTypeCodec keyTypeCodec;
  static StringWriter writer;
  KeyType keyType;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    keyTypeCodec = new KeyTypeCodec();
  }

  @Test
  void test_Encode() throws IOException {
    keyTypeCodec.encoder().apply(writer, KeyType.DH);
  }

  @Test
  void test_Decoder() throws IOException {

    keyTypeCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
