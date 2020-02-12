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

public enum SdoErrorCode {
  OK(0),

  // Request tokens are not valid
  InvalidToken(1),

  // One of Ownership Voucher verification checks has failed.
  InvalidOwnershipProxy(2),

  // Verification of signature of owner message failed.
  InvalidOwnerSignBody(3),

  // IP address is invalid.
  InvalidIpAddress(4),

  // GUID is invalid.
  InvalidGuid(5),

  // Requested resources are not available on the server
  ResourceNotFound(6),

  // Message body structurally unsound: JSON parse error, or
  // valid JSON, but not mapping to expected types
  SyntaxError(100),

  // Message syntactically sound, but rejected by server
  MessageRefused(101),

  // Unexpected system error
  InternalError(500);

  private final int code;

  private SdoErrorCode(int code) {
    this.code = code;
  }

  /**
   * Utility method to return the {@link SdoErrorCode} corresponding to the input number.
   *
   * @param n the input number representing an error code
   * @return {@link SdoErrorCode}
   */
  public static SdoErrorCode fromNumber(final Number n) {
    int i = n.intValue();

    for (SdoErrorCode t : SdoErrorCode.values()) {
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
