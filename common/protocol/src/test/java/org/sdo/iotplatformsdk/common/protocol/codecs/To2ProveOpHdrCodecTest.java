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
import org.sdo.iotplatformsdk.common.protocol.codecs.To2ProveOpHdrCodec;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.SigInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureType;
import org.sdo.iotplatformsdk.common.protocol.types.To2ProveOpHdr;

class To2ProveOpHdrCodecTest {

  To2ProveOpHdrCodec to2ProveOpHdrCodec;
  To2ProveOpHdr to2ProveOpHdr;
  SigInfo sigInfo;
  SignatureType signatureType;
  HashMac hashMac;
  MacType macType;
  Nonce nonce;
  SecureRandom secureRandom;
  OwnershipProxyHeader ownershipProxyHeader;
  static StringWriter writer;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    secureRandom = new SecureRandom();
    sigInfo = new SigInfo(signatureType.ECDSA_P_256, ByteBuffer.allocate(8));
    hashMac = new HashMac(macType.HMAC_SHA256, ByteBuffer.allocate(8));
    nonce = new Nonce(secureRandom);
    ownershipProxyHeader = new OwnershipProxyHeader();
    to2ProveOpHdr = new To2ProveOpHdr(1, ownershipProxyHeader, hashMac, nonce, nonce, sigInfo,
        ByteBuffer.allocate(8));
    to2ProveOpHdrCodec = new To2ProveOpHdrCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    to2ProveOpHdrCodec.encoder().apply(writer, to2ProveOpHdr);
  }

  @Test
  void test_Decoder() throws IOException {
    to2ProveOpHdrCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
