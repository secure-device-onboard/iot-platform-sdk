// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

public enum CipherOperation {
  // Represents the encipher operation.
  ENCIPHER("encipher"),

  // Represents the decipher operation.
  DECIPHER("decipher");

  private final String name;

  private CipherOperation(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
