// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
