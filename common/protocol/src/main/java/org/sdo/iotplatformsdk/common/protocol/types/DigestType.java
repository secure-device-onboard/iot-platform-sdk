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

package org.sdo.iotplatformsdk.common.protocol.types;

import java.util.NoSuchElementException;

/**
 * SDO digest-hash type.
 *
 * @see MacType
 * @see "SDO Protocol Specification, 1.13b, 3.2.1: Hash Types and HMAC Types"
 */
public enum DigestType {
  NONE(0, ""),
  SHA1(3, "SHA-1"),
  SHA256(8, "SHA-256"),
  SHA512(10, "SHA-512"),
  SHA384(14, "SHA-384");

  private final int code;
  private final String jceAlgo;

  DigestType(int code, String jceAlgo) {
    this.code = code;
    this.jceAlgo = jceAlgo;
  }

  public String toJceAlgorithm() {
    return jceAlgo;
  }

  /**
   * Utility method to return the {@link DigestType} corresponding to the input number.
   *
   * @param n the input number corresponding to a Digest type
   * @return  {@link DigestType}
   */
  public static DigestType fromNumber(final Number n) {
    int i = n.intValue();
    for (DigestType t : DigestType.values()) {
      if (t.toInteger() == i) {
        return t;
      }
    }

    throw new NoSuchElementException(n.toString());
  }

  public int toInteger() {
    return code;
  }
}
