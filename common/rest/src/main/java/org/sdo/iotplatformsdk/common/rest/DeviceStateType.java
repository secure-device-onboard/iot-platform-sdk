// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
