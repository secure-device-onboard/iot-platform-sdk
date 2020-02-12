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
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2DoneCodec;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To2Done;

class To2DoneCodecTest {

  To2DoneCodec to2DoneCodec;
  To2Done to2Done;
  HashMac hashMac;
  ByteBuffer byteBuffer;
  Nonce n6;
  static SecureRandom secureRandom;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
    secureRandom = new SecureRandom();
  }

  @SuppressWarnings("static-access")
  @BeforeEach
  void beforeEach() {
    byteBuffer = ByteBuffer.wrap("Zzyx10Sq/Ny5P3c1Nmq9isyu/vUiZpaHSe3xEGXxbdY=".getBytes());
    to2DoneCodec = new To2DoneCodec();
    n6 = new Nonce(secureRandom);
    hashMac = new HashMac(MacType.HMAC_SHA256, byteBuffer);
    to2Done = new To2Done(hashMac, n6);
  }

  @Test
  void test_Encoder() throws IOException {
    to2DoneCodec.encoder().apply(writer, to2Done);
  }

  @Test
  void test_Decoder() throws IOException {
    writer.append("{\"hmac\":[32,108,\"Zzyx10Sq/Ny5P3c1Nmq9isyu/vUiZpaHSe3xEGXxbdY=\"],"
        + "\"n6\":\"9hkv3cxHAmGf8Xuy8lsq2A==\"}");
    to2DoneCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
