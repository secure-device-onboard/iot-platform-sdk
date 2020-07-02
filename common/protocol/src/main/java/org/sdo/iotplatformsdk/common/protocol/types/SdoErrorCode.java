// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
