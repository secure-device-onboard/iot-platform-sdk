// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.To2Done2;

class To2Done2Test {

  To2Done2 to2Done2;
  Nonce n7;

  @BeforeEach
  void beforeEach() {
    n7 = new Nonce(new SecureRandom());
    to2Done2 = new To2Done2(n7);
  }

  @Test
  void test_Bean() {

    assertEquals(n7, to2Done2.getN7());
    to2Done2.getType();
    to2Done2.getVersion();
  }

}
