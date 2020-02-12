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

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.security.SimpleMacService;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;


class SimpleMacServiceTest {

  ByteBuffer[] ins;
  ReadableByteChannel[] readableByteChannel;
  SimpleMacService simpleMacService;

  @BeforeEach
  void beforeEach() throws NoSuchAlgorithmException {

    ins = new ByteBuffer[2];
    ins[0] = ByteBuffer.wrap("Hello World".getBytes());
    ins[1] = ByteBuffer.wrap("Test".getBytes());
    readableByteChannel = new ReadableByteChannel[2];
    readableByteChannel[0] = Channels
        .newChannel(new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8)));
    readableByteChannel[1] =
        Channels.newChannel(new ByteArrayInputStream("Test".getBytes(StandardCharsets.UTF_8)));
    simpleMacService = new SimpleMacService(MacType.HMAC_SHA256);

  }

  @Test
  void test_DigestOf() throws IOException, InvalidKeyException, NoSuchAlgorithmException {

    simpleMacService.macOf("SampleKey".getBytes(), readableByteChannel);
    simpleMacService.macOf("SampleKey".getBytes(), ins);
  }


}
