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

package org.sdo.iotplatformsdk.common.rest;

/**
 * Represents the stages of the device during TO2 protocol execution.
 */
public enum DeviceStateType {

  // Represents the start of TO2 protocol.
  TO2BEGIN("to2begin"),

  // Represents the end of TO2 protocol.
  TO2END("to2end"),

  // Represents that an error occurred during TO2 protocol execution.
  TO2ERROR("to2error");

  private final String name;

  private DeviceStateType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
