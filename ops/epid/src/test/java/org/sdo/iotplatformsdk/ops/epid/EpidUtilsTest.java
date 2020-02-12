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
