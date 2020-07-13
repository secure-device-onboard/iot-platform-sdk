// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetNextOwnerServiceInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextOwnerServiceInfo;

class To2GetNextOwnerServiceInfoCodecTest {

  To2GetNextOwnerServiceInfoCodec to2GetNextOwnerServiceInfoCodec;
  static StringWriter writer;
  To2GetNextOwnerServiceInfo to2GetNextOwnerServiceInfo;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {
    to2GetNextOwnerServiceInfoCodec = new To2GetNextOwnerServiceInfoCodec();
    to2GetNextOwnerServiceInfo = Mockito.mock(To2GetNextOwnerServiceInfo.class);
  }

  @Test
  void test_Encoder() throws IOException {
    Mockito.when(to2GetNextOwnerServiceInfo.getNn()).thenReturn(1);
    to2GetNextOwnerServiceInfoCodec.encoder().apply(writer, to2GetNextOwnerServiceInfo);
  }

  @Test
  void test_Decoder() throws IOException {

    CharBuffer charBufferto2GetNextOwnerServiceInfoCodec = Mockito.mock(CharBuffer.class);
    Mockito.when(charBufferto2GetNextOwnerServiceInfoCodec.get()).thenReturn('{').thenReturn('"')
        .thenReturn('n').thenReturn('n').thenReturn('"').thenReturn(':').thenReturn('}');
    to2GetNextOwnerServiceInfoCodec.decoder().apply(charBufferto2GetNextOwnerServiceInfoCodec);
  }
}
