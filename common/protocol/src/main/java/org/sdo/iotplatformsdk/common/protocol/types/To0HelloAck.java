// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

public class To0HelloAck {

  public static final Integer ID = 21;

  private Nonce n3;

  public To0HelloAck(Nonce n3) {
    setN3(n3);
  }

  public Nonce getN3() {
    return n3;
  }

  public void setN3(Nonce n3) {
    this.n3 = n3;
  }
}
