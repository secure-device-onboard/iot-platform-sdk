// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.Codec;
import org.sdo.iotplatformsdk.common.protocol.codecs.SigInfoCodec;
import org.sdo.iotplatformsdk.common.protocol.types.SigInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureType;

class SigInfoCodecTest {

  SigInfoCodec sigInfoCodec;
  SignatureType signatureType;
  ByteBuffer byteBuffer;
  static StringWriter writer;
  SigInfo sigInfo;
  Codec<Number> lengthcodec;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {
    sigInfoCodec = new SigInfoCodec();
    byteBuffer = ByteBuffer.allocate(8);
    lengthcodec = Mockito.mock(Codec.class);
    sigInfo = Mockito.mock(SigInfo.class);
  }

  @Test
  void test_Encoder() throws IOException {
    Mockito.when(sigInfo.getInfo()).thenReturn(ByteBuffer.allocate(8));
    Mockito.when(sigInfo.getSgType()).thenReturn(signatureType.ECDSA_P_256);
    sigInfoCodec.encoder().apply(writer, sigInfo);
  }

  @Test
  void test_Decoder() throws IOException {

    sigInfoCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
