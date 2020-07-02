// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2Done2Codec;
import org.sdo.iotplatformsdk.common.protocol.types.Message;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To2Done2;
import org.sdo.iotplatformsdk.common.protocol.types.Version;

class To2Done2CodecTest {

  To2Done2Codec to2Done2Codec;
  To2Done2 to2Done2;
  Message message;
  MessageType messageType;
  Version version;
  Nonce n7;
  static SecureRandom secureRandom;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
    secureRandom = new SecureRandom();
  }

  @BeforeEach
  void beforeEach() {
    to2Done2Codec = new To2Done2Codec();
    message = Mockito.mock(Message.class);
    to2Done2 = Mockito.mock(To2Done2.class);
    n7 = new Nonce(secureRandom);
  }

  @Test
  void test_Encoder() throws IOException {
    Mockito.when(to2Done2.getType()).thenReturn(messageType);
    Mockito.when(to2Done2.getVersion()).thenReturn(version);
    Mockito.when(to2Done2.getN7()).thenReturn(n7);
    to2Done2Codec.encoder().apply(writer, to2Done2);
  }

  @Test
  void test_Decoder() throws IOException {
    writer.append("{\"n7\":\"L0uKuTbiGzHExix8l93cPQ==\"}");
    to2Done2Codec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
