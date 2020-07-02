// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

/**
 * Class to manage the messages for ServiceInfo modules.
 *
 * <p>Represents a message-value pair for the given module name.
 */
public class ModuleMessage {
  private String module;
  private String msg;
  private String value;

  /**
   * Returns the module name.
   *
   * @return module name.
   */
  public String getModule() {
    return module;
  }

  /**
   * Stores the specified module name.
   *
   * @param name module name.
   */
  public void setModule(String name) {
    this.module = name;
  }

  /**
   * Returns the actual message of this module.
   *
   * @return
   */
  public String getMsg() {
    return msg;
  }

  /**
   * Stores the actual message for this module..
   *
   * @param msg message.
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * Returns the value of this module.
   *
   * @return value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Stores the value for this module.
   *
   * @param value value to be stored.
   */
  public void setValue(String value) {
    this.value = value;
  }
}
