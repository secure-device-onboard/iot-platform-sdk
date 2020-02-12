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

import java.util.Objects;

public class CipherType {

  private CipherAlgorithm algorithm;
  private MacType macType;
  private CipherBlockMode mode;

  /**
   * Constructor.
   */
  public CipherType(CipherAlgorithm algorithm, CipherBlockMode mode, MacType macType) {

    setAlgorithm(algorithm);
    setMode(mode);
    setMacType(macType);
  }

  public CipherAlgorithm getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(CipherAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  public MacType getMacType() {
    return macType;
  }

  public void setMacType(MacType macType) {
    this.macType = macType;
  }

  public CipherBlockMode getMode() {
    return mode;
  }

  public void setMode(CipherBlockMode mode) {
    this.mode = mode;
  }

  @Override
  public int hashCode() {
    return Objects.hash(algorithm, macType, mode);
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CipherType that = (CipherType) o;
    return this.algorithm == that.algorithm && this.macType == that.macType
        && this.mode == that.mode;
  }
}
