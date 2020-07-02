// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

public enum Version {

  VERSION_1_09(109),
  VERSION_1_10(110),
  VERSION_1_12(112),
  VERSION_1_13(113);

  private final int value;

  private Version(int value) {
    this.value = value;
  }

  /**
   * Utility method to return the {@link Version} corresponding to the input value.
   *
   * @param value the int value corresponding to the version
   * @return {@link Version}
   */
  public static Version valueOfInt(int value) {
    for (Version pv : Version.values()) {
      if (pv.intValue() == value) {
        return pv;
      }
    }

    throw new IllegalArgumentException(); // no match
  }

  public static Version valueOfString(String value) {
    return valueOfInt(Integer.parseInt(value));
  }

  public int intValue() {
    return value;
  }

  @Override
  public String toString() {
    return Integer.toString(intValue());
  }
}
