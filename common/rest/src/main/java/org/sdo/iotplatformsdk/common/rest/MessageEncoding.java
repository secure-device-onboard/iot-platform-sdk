// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

/**
 * Specifies the supported message encoding formats.
 *
 */
public enum MessageEncoding {

  // Base-64 encoding.
  BASE64("base64"),

  // ASCII encoding.
  ASCII("ascii");

  private final String name;

  private MessageEncoding(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
