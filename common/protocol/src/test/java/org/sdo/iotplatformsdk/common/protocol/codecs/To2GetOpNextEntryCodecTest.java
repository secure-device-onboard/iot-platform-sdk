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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2GetOpNextEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetOpNextEntry;

class To2GetOpNextEntryCodecTest {

  To2GetOpNextEntryCodec to2GetOpNextEntryCodec;
  static StringWriter writer;
  To2GetOpNextEntry to2GetOpNextEntry;

  @BeforeAll
  static void beforeAll() {
    writer = new StringWriter();
  }

  @BeforeEach
  void beforeEach() {

    to2GetOpNextEntry = new To2GetOpNextEntry(1);
    to2GetOpNextEntryCodec = new To2GetOpNextEntryCodec();
  }

  @Test
  void test_Encoder() throws IOException {
    to2GetOpNextEntryCodec.encoder().apply(writer, to2GetOpNextEntry);
  }

  @Test
  void test_Decoder() throws IOException {

    to2GetOpNextEntryCodec.decoder().apply(CharBuffer.wrap(writer.toString()));
  }
}
