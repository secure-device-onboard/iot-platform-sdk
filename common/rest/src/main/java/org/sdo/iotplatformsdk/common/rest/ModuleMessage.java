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
