// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.SigInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureType;

class SigInfoTest {

  SigInfo sigInfo;
  SignatureType sgType;

  @BeforeEach
  void beforeEach() {

    sgType = SignatureType.ECDSA_P_256;
    sigInfo = new SigInfo(sgType, ByteBuffer.wrap("Test".getBytes()));
  }

  @Test
  void test_Bean() {

    sigInfo.setInfo(ByteBuffer.wrap("Test".getBytes()));
    sigInfo.setSgType(sgType);

    assertEquals(ByteBuffer.wrap("Test".getBytes()), sigInfo.getInfo());
    assertEquals(sgType, sigInfo.getSgType());
  }

}
