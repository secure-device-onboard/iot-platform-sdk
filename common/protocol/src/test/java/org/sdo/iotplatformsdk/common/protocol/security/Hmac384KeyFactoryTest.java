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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.security.Hmac384KeyFactory;

class Hmac384KeyFactoryTest {

  Hmac384KeyFactory hmac384KeyFactory;
  ByteBuffer sharedSecret;

  @BeforeEach
  void beforeEach() {

    sharedSecret = ByteBuffer.allocate(128);
    hmac384KeyFactory = new Hmac384KeyFactory(sharedSecret);
  }

  @Test
  void test_Build() throws IOException, InvalidKeyException, NoSuchAlgorithmException {

    hmac384KeyFactory.build();

  }

}
