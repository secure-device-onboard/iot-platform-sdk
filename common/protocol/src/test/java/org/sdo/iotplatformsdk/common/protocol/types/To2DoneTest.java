// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To2Done;

class To2DoneTest {

  HashMac hmac;
  To2Done to2Done;
  Nonce n6;

  @BeforeEach
  void beforeEach() {
    n6 = new Nonce(new SecureRandom());
    hmac = new HashMac();
    to2Done = new To2Done(hmac, n6);
  }

  @Test
  void test_Bean() {

    assertEquals(hmac, to2Done.getHmac());
    assertEquals(n6, to2Done.getN6());
    to2Done.getType();
    to2Done.getVersion();
  }

}
