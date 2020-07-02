// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
