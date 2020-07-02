// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

public class To0OwnerSign {

  public static final Integer ID = 22;

  private To0OwnerSignTo0d to0d;
  private SignatureBlock to1d;

  public To0OwnerSign(To0OwnerSignTo0d to0d, SignatureBlock to1d) {
    this.to0d = to0d;
    this.to1d = to1d;
  }

  public To0OwnerSignTo0d getTo0d() {
    return to0d;
  }

  public SignatureBlock getTo1d() {
    return to1d;
  }

  public void setTo0d(To0OwnerSignTo0d to0d) {
    this.to0d = to0d;
  }

  public void setTo1d(SignatureBlock to1d) {
    this.to1d = to1d;
  }

}
