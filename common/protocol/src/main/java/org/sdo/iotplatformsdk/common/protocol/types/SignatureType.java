// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.util.NoSuchElementException;

/**
 * SDO "Signature Type."
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2.5: Device Attestation Signature and Mechanism"
 */
public enum SignatureType {
  ECDSA_P_256(13),
  ECDSA_P_384(14),
  EPID10(90),
  EPID11(91),
  EPID20(92),
  ON_DIE_ECDSA_384(93);

  private final int id;

  private SignatureType(int id) {
    this.id = id;
  }

  /**
   * Utiltiy method to return {@link SignatureType} corresponding to the input number.
   *
   * @param n the input number
   * @return {@link SignatureType}
   */
  public static SignatureType fromNumber(Number n) {
    int i = n.intValue();
    for (SignatureType t : SignatureType.values()) {
      if (i == t.toInteger()) {
        return t;
      }
    }
    throw new NoSuchElementException(n.toString());
  }

  public int toInteger() {
    return id;
  }
}
