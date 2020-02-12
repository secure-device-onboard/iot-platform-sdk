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

package org.sdo.iotplatformsdk.common.protocol.util;

import java.math.BigInteger;
import java.util.Arrays;

public class BigIntegers {

  /**
   * Converts a BigInteger into a byte[].
   *
   * <p>BigInteger arrays can be sign extended and shorter or longer than the expected
   * number of bytes.
   *
   * <p>Trim or pad these arrays as needed.
   *
   * @param shx BigInteger
   * @param arraySize size of array.
   * @return
   */
  public static byte[] toByteArray(BigInteger shx, int arraySize) {

    byte[] shxBytes = shx.toByteArray();

    int delta = arraySize - shxBytes.length;

    if (delta < 0) {
      // shxBytes is too long. Truncate it.
      return Arrays.copyOfRange(shxBytes, -delta, shxBytes.length);

    } else if (delta > 0) {
      // shxBytes is too short. Sign-extend it.
      byte[] result = new byte[arraySize];
      byte fillByte = shx.signum() < 0 ? (byte) 0xff : (byte) 0;
      Arrays.fill(result, 0, delta, fillByte);
      System.arraycopy(shxBytes, 0, result, delta, shxBytes.length);
      return result;

    } else {
      return shxBytes;
    }
  }
}
