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
 * Class to manage ProtocolError message.
 */
public class ProtocolError {

  private int ec;
  private int emsg;
  private String em;

  /**
   * Returns the SDO error code.
   *
   * @return error code.
   */
  public int getEc() {
    return ec;
  }

  /**
   * Stores the specified SDO error code.
   *
   * @param ec error code.
   */
  public void setEc(int ec) {
    this.ec = ec;
  }

  /**
   * Returns the protocol message at which error occurred.
   *
   * @return message.
   */
  public int getEmsg() {
    return emsg;
  }

  /**
   * Store the specified protocol message at which error occurred.
   *
   * @param emsg message.
   */
  public void setEmsg(int emsg) {
    this.emsg = emsg;
  }

  /**
   * Returns the SDO protocol error message.
   *
   * @return error message.
   */
  public String getEm() {
    return em;
  }

  /**
   * Stores the specified SDO protocol error message.
   *
   * @param em error message.
   */
  public void setEm(String em) {
    this.em = em;
  }
}
