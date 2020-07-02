// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.CipherAlgorithm;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.sdo.iotplatformsdk.common.protocol.types.CipherType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

class CipherTypeTest {

  CipherType cipherType;

  @BeforeEach
  void beforeEach() {

    cipherType = new CipherType(CipherAlgorithm.AES128, CipherBlockMode.CBC, MacType.HMAC_SHA256);
  }

  @Test
  void test_Bean() {

    cipherType.setAlgorithm(CipherAlgorithm.AES128);
    cipherType.setMacType(MacType.HMAC_SHA256);
    cipherType.setMode(CipherBlockMode.CBC);

    assertEquals(CipherAlgorithm.AES128, cipherType.getAlgorithm());
    assertEquals(MacType.HMAC_SHA256, cipherType.getMacType());
    assertEquals(CipherBlockMode.CBC, cipherType.getMode());
    cipherType.hashCode();
    cipherType.equals(cipherType);
    cipherType.equals(null);
    cipherType
        .equals(new CipherType(CipherAlgorithm.AES128, CipherBlockMode.CBC, MacType.HMAC_SHA256));
  }

}
