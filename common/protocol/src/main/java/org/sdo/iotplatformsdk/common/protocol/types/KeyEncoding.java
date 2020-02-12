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
 * Public-Key encoding types.
 *
 * <p>These enumerations are the key encoding names from the Protocol Specification with all
 * non-alphanumerics replaced with underscores.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2.2: Public Key Types"
 */
public enum KeyEncoding {
  /**
   * No public key present: key is PKNull.
   */
  NONE(0),
  /**
   * X.509.
   */
  X_509(1),
  /**
   * Modulus/exponent encoding of RSA2048RESTR or RSA_UR.
   */
  RSAMODEXP(3),
  /**
   * Intel EPID.
   *
   * @see <a href=https://01.org/epid-sdk>The Intel EPID SDK</a>
   */
  EPID(4);

  private final int value;

  private KeyEncoding(int value) {
    this.value = value;
  }

  /**
   * Utility method to return corresponding {@link KeyEncoding} for the input number.
   *
   * @param n number
   * @return {@link KeyEncoding}
   */
  public static KeyEncoding fromNumber(Number n) {

    int i = n.intValue();

    for (KeyEncoding e : values()) {

      if (e.toInteger() == i) {
        return e;
      }
    }

    throw new NoSuchElementException(KeyEncoding.class.getName() + ":" + i);
  }

  public int toInteger() {
    return value;
  }
}
