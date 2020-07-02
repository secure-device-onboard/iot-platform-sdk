// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
