// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.config;

public class EpidOptionBean {

  private String epidOnlineUrl;
  private boolean testMode;

  public EpidOptionBean() {
    this.epidOnlineUrl = "";
  }

  public void setEpidOnlineUrl(String epidOnlineUrl) {
    this.epidOnlineUrl = epidOnlineUrl;
  }

  public void setTestMode(boolean value) {
    this.testMode = value;
  }

  public String getEpidOnlineUrl() {
    return epidOnlineUrl;
  }

  public boolean getTestMode() {
    return testMode;
  }
}
