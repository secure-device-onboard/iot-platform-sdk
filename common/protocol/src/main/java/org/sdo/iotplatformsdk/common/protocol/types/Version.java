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
