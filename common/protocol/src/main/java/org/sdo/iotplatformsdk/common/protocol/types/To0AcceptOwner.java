// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.time.Duration;

public class To0AcceptOwner {

  public static final Integer ID = 25;

  private Duration ws;

  public To0AcceptOwner(Duration ws) {
    setWs(ws);
  }

  public Duration getWs() {
    return ws;
  }

  public void setWs(Duration ws) {
    this.ws = ws;
  }
}
