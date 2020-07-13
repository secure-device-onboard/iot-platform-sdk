// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.util.NoSuchElementException;

/**
 * SDO device states.
 *
 * @see "SDO Architecture Specification, 1.0, 4.2.2: Device States"
 */
public enum DeviceState {
  PD(0), // Permanently Disabled
  PC(1), // Pre-Configured
  D(2), // Disabled
  READY1(3), // Initial Transfer Reader
  D1(4), // Initial Transfer Disabled
  IDLE(5), // Idle
  READYN(6), // Transfer Ready
  DN(7), // Transfer Disabled
  ERROR(255);

  private int id;

  private DeviceState(int id) {
    this.id = id;
  }

  /**
   * Utility method to return the corresponding {@link DeviceState} given a number.
   *
   * @param n number
   * @return {@link DeviceState}
   */
  public static DeviceState fromNumber(Number n) {
    final int i = n.intValue();

    for (DeviceState s : DeviceState.values()) {

      if (s.getId() == i) {
        return s;
      }
    }

    throw new NoSuchElementException(n.toString());
  }

  public int toInteger() {
    return getId();
  }

  int getId() {
    return id;
  }
}
