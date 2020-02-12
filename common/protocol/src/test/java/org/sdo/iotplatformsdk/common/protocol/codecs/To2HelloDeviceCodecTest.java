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
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2HelloDeviceCodec;
import org.sdo.iotplatformsdk.common.protocol.types.CipherAlgorithm;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.sdo.iotplatformsdk.common.protocol.types.CipherType;
import org.sdo.iotplatformsdk.common.protocol.types.KeyEncoding;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.SigInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureType;
import org.sdo.iotplatformsdk.common.protocol.types.To2HelloDevice;

class To2HelloDeviceCodecTest {

  To2HelloDeviceCodec to2HelloDeviceCodec;
  To2HelloDevice to2HelloDevice;
  CipherType cipherType;
  MacType macType;
  CipherBlockMode cipherBlockMode;
  CipherAlgorithm cipherAlgorithm;
  ByteBuffer byteBuffer;
  SignatureType signatureType;
  KeyExchangeType keyExchangeType;
  KeyEncoding keyEncoding;
  UUID uuid;
  Nonce nonce;
  SecureRandom secureRandom;
  SigInfo sigInfo;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @SuppressWarnings("static-access")
  @BeforeEach
  void beforeEach() {

    uuid = UUID.randomUUID();
    secureRandom = new SecureRandom();
    nonce = new Nonce(secureRandom);
    cipherType = new CipherType(cipherAlgorithm.AES128, cipherBlockMode.CBC, macType.HMAC_SHA256);
    sigInfo = new SigInfo(signatureType.ECDSA_P_256, byteBuffer.allocate(8));
    to2HelloDevice = new To2HelloDevice(uuid, nonce, keyEncoding.EPID, keyExchangeType.ASYMKEX,
        cipherType, sigInfo);
    to2HelloDeviceCodec = new To2HelloDeviceCodec();
  }

  @Test
  void test_Encoder() throws IOException {

    to2HelloDeviceCodec.encoder().apply(writer, to2HelloDevice);
  }

  @Test
  void test_Decoder() throws IOException {

    to2HelloDeviceCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
