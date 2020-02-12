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

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.CipherText;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.To2CipherHashMac;

class EncryptedMessageTest {

  CipherText ct;
  To2CipherHashMac hmac;
  EncryptedMessage encryptedMessage;

  @BeforeEach
  void beforeEach() {
    ct = new CipherText(ByteBuffer.allocate(0), ByteBuffer.wrap("Test".getBytes()));
    hmac = new To2CipherHashMac();
    encryptedMessage = new EncryptedMessage(ct, hmac);
  }

  @Test
  void test_Bean() {
    assertEquals(ct, encryptedMessage.getCt());
    assertEquals(hmac, encryptedMessage.getHmac());
  }

}
