// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
