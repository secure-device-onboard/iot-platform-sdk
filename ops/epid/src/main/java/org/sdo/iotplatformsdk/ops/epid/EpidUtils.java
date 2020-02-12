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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.bouncycastle.util.Arrays;

/**
 * Provides utilities for the Epid JNI Interface. All methods in this class are static.
 */
public class EpidUtils {

  static final ByteOrder BYTE_ORDER = ByteOrder.BIG_ENDIAN;

  /**
   * convert from a 4 byte buffer and a unsigned Int.
   *
   * @param bytes - a 4 byte bytearray contains the source data
   * @return a short value of this data, or null if bytes too long
   */
  public static Integer bytesToUint(byte[] bytes) {
    if (bytes.length > (Integer.SIZE / Byte.SIZE)) {
      throw new NumberFormatException("integers must be " + Integer.SIZE / Byte.SIZE + " bytes");
    }

    ByteBuffer buf = ByteBuffer.wrap(Arrays.copyOf(bytes, bytes.length));
    buf.order(BYTE_ORDER);
    Integer i = buf.getInt();
    return i;
  }

  /**
   * Convert a short to a 2 byte array of the lower two bytes. Little Endian order on the bytes
   *
   * @param value - a short value of this data
   * @return bytes - a 2 byte bytearray contains the source data
   */
  public static byte[] shortToBytes(short value) {
    byte[] data = new byte[Short.BYTES];
    ByteBuffer buf = ByteBuffer.wrap(data);
    buf.order(BYTE_ORDER);
    buf.putShort(value);
    return data;
  }

}
