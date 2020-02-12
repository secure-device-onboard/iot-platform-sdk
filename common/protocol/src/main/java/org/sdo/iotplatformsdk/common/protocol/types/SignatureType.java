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
 * SDO "Signature Type."
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2.5: Device Attestation Signature and Mechanism"
 */
public enum SignatureType {
  ECDSA_P_256(13),
  ECDSA_P_384(14),
  EPID10(90),
  EPID11(91),
  EPID20(92);

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
