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
