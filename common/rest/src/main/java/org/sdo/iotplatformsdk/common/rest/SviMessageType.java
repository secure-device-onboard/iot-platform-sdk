// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

/**
 * Enumerations for different ServiceInfo message types.
 */
public enum SviMessageType {

  EXEC(":exec"),
  FILEDESC(":filedesc"),
  WRITE(":write"),
  MODULE("sdo_sys");

  private String name;

  private SviMessageType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
