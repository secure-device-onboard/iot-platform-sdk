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
import java.nio.CharBuffer;
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To0HelloAckCodec;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To0HelloAck;

class To0HelloAckCodecTest {

  To0HelloAckCodec to0HelloAckCodec;
  To0HelloAck to0HelloAck;
  static StringWriter writer;
  Nonce nonceobj;
  SecureRandom secureRandom;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    to0HelloAckCodec = new To0HelloAckCodec();
    secureRandom = new SecureRandom();
    nonceobj = new Nonce(secureRandom);
    to0HelloAck = new To0HelloAck(nonceobj);
  }

  @Test
  void testEncoder() throws IOException {

    to0HelloAckCodec.encoder().apply(writer, to0HelloAck);
  }

  @Test
  void testDecoder() throws IOException {

    to0HelloAckCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
