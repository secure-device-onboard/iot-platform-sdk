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
 * Class to manage TO0Request.
 *
 * <p>Used as an message object to schedule TO0 for an array of device identifiers with their
 * corresponding wait seconds.
 */
public class To0Request {

  private String[] guids;
  private String waitSeconds;

  public To0Request() {}

  /**
   * Returns an array of device identifiers (guid).
   *
   * @return device identifiers.
   */
  public String[] getGuids() {
    return guids;
  }

  /**
   * Stores an array of device identifiers (guid).
   *
   * @param guids device identifiers.
   */
  public void setGuids(String[] guids) {
    this.guids = guids;
  }

  /**
   * Returns the number of wait seconds.
   *
   * @return TO0 expiration wait seconds.
   */
  public String getWaitSeconds() {
    return waitSeconds;
  }

  /**
   * Stores the specified wait seconds.
   *
   * @param waitSeconds TO0 expiration wait seconds.
   */
  public void setWaitSeconds(String waitSeconds) {
    this.waitSeconds = waitSeconds;
  }

}
