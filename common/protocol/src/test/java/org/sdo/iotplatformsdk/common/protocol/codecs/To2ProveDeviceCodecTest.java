// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2ProveDeviceCodec;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To2ProveDevice;

class To2ProveDeviceCodecTest {

  To2ProveDeviceCodec to2ProveDeviceCodec;
  To2ProveDevice to2ProveDevice;
  Nonce nonce;
  SecureRandom secureRandom;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    secureRandom = new SecureRandom();
    nonce = new Nonce(secureRandom);
    to2ProveDevice = new To2ProveDevice(ByteBuffer.allocate(8), nonce, nonce, UUID.randomUUID(), 1,
        ByteBuffer.allocate(8));
    to2ProveDeviceCodec = new To2ProveDeviceCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    to2ProveDeviceCodec.encoder().apply(writer, to2ProveDevice);
  }

  @Test
  void test_Decoder() throws IOException {
    to2ProveDeviceCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
