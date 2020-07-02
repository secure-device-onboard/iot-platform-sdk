// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey10;

class EpidKey10Test {

  byte[] groupId = {0x00, 0x00, 0x00, 0x0d, (byte) 0xdd, (byte) 0xdd, (byte) 0xcc, (byte) 0xcc,
      0x00, 0x00, 0x00, 0x00, (byte) 0xee, (byte) 0xee, (byte) 0xee, 0x05};
  EpidKey10 epidKey10;


  @BeforeEach
  void beforeEach() {

    epidKey10 = new EpidKey10(groupId);
  }

  @Test
  void test_Bean() {

    epidKey10.getAlgorithm();
    epidKey10.getFormat();
    epidKey10.getType();

  }

}
