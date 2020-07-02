// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.epid;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.ops.epid.EpidUtils;

public class EpidUtilsTest {

  /**
   * Test the routine.
   */
  @Test
  public void testShortToBytes() throws Exception {
    short testVal = 1;
    byte[] result = new byte[2];
    byte[] expected = new byte[] {0x00, 0x01};

    result = EpidUtils.shortToBytes(testVal);
    assertArrayEquals(expected, result);
  }

  @Test
  public void testBytesToUint() throws Exception {
    byte[] testBytes = new byte[] {0x00, 0x00, 0x00, 0x01};
    Integer result = 1;

    result = EpidUtils.bytesToUint(testBytes);
    assertEquals((Integer) 1, result);
  }

}
