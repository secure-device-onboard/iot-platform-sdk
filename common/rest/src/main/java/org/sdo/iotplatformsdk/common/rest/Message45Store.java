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

public class Message45Store {

  private String n7;
  private Integer nn;
  private String xb;

  public Message45Store() {}

  /**
   * Constructor.
   */
  public Message45Store(String n7, Integer nn, String xb) {
    this.n7 = n7;
    this.nn = nn;
    this.xb = xb;
  }

  public String getN7() {
    return n7;
  }

  public void setN7(String n7) {
    this.n7 = n7;
  }

  public Integer getNn() {
    return nn;
  }

  public void setNn(Integer nn) {
    this.nn = nn;
  }

  public String getXb() {
    return xb;
  }

  public void setXb(String xb) {
    this.xb = xb;
  }

}
