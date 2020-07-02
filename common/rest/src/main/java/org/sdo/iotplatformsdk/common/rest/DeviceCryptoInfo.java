// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

public class DeviceCryptoInfo {

  private String ctrNonce;
  private long ctrCounter;

  public DeviceCryptoInfo() {}

  public DeviceCryptoInfo(String ctrNonce, long ctrCounter) {
    this.ctrNonce = ctrNonce;
    this.ctrCounter = ctrCounter;
  }

  public String getCtrNonce() {
    return ctrNonce;
  }

  public void setCtrNonce(String ctrIv) {
    this.ctrNonce = ctrIv;
  }

  public long getCtrCounter() {
    return ctrCounter;
  }

  public void setCtrCounter(long ctrCounter) {
    this.ctrCounter = ctrCounter;
  }

}
