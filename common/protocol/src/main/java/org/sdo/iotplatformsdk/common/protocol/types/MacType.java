// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.util.NoSuchElementException;

/**
 * SDO MAC-hash type.
 *
 * @see DigestType
 * @see "SDO Protocol Specification, 1.13b, 3.2.1: Hash Types and HMAC Types"
 */
public enum MacType {
  NONE(0, "", ""),
  HMAC_SHA256(108, "HmacSHA256", "HMAC-SHA256"),
  HMAC_SHA512(110, "HmacSHA512", "HMAC-SHA512"),
  HMAC_SHA384(114, "HmacSHA384", "HMAC-SHA384");

  private final int code;
  private final String jceName;
  private final String sdoName;

  MacType(int code, String jceName, String sdoName) {
    this.code = code;
    this.jceName = jceName;
    this.sdoName = sdoName;
  }

  /**
   * Utility method to return the {@link MacType} corresponding to the input name.
   *
   * @param jceName the mac-type name
   * @return {@link MacType}
   */
  public static MacType fromJceName(final String jceName) {
    for (MacType t : MacType.values()) {
      if (t.getJceName().equals(jceName)) {
        return t;
      }
    }

    throw new NoSuchElementException(jceName);
  }

  /**
   * Utility method to return the {@link MacType} corresponding to the input sdo name.
   *
   * @param sdoName the mac-type name as per sdo
   * @return {@link MacType}
   */
  public static MacType fromSdoName(final String sdoName) {

    for (MacType t : MacType.values()) {
      if (t.getSdoName().equals(sdoName)) {
        return t;
      }
    }

    throw new NoSuchElementException(sdoName);
  }

  /**
   * Utility method to return the {@link MacType} corresponding to the input number.
   *
   * @param n the number
   * @return {@link MacType}
   */
  public static MacType fromNumber(final Number n) {
    int i = n.intValue();

    for (MacType t : MacType.values()) {
      if (t.getCode() == i) {
        return t;
      }
    }

    throw new NoSuchElementException(n.toString());
  }

  public String getJceName() {
    return jceName;
  }

  public String getSdoName() {
    return sdoName;
  }

  public int getCode() {
    return code;
  }
}
