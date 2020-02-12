/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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
