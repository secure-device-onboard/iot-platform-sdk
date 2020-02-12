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
import org.sdo.iotplatformsdk.common.protocol.codecs.CipherTypeCodec;
import org.sdo.iotplatformsdk.common.protocol.codecs.Codec;
import org.sdo.iotplatformsdk.common.protocol.types.CipherAlgorithm;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.sdo.iotplatformsdk.common.protocol.types.CipherType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

class CipherTypeCodecTest {

  CipherType cipherType;
  CipherTypeCodec cipherTypeCodec;
  Codec<Number> lengthCodec;
  Codec<Number>.Decoder lengthCodecDecoder;
  ByteBuffer out;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() {

    cipherType = Mockito.mock(CipherType.class);
    cipherTypeCodec = new CipherTypeCodec();
    lengthCodec = Mockito.mock(Codec.class);
    lengthCodecDecoder = Mockito.mock(Codec.Decoder.class);
    out = ByteBuffer.allocate(8);
  }

  @Test
  void test_Encoder() throws IOException {

    Mockito.when(cipherType.getAlgorithm()).thenReturn(CipherAlgorithm.AES128);
    Mockito.when(cipherType.getMode()).thenReturn(CipherBlockMode.CBC);
    Mockito.when(cipherType.getMacType()).thenReturn(MacType.HMAC_SHA256);

    cipherTypeCodec.encoder().apply(writer, cipherType);
  }

  @Test
  void test_Decoder() throws IOException {

    cipherTypeCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
