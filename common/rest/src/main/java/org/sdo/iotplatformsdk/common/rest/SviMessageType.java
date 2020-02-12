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
