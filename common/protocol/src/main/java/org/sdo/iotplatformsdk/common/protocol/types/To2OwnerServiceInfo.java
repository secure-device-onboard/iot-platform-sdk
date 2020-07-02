// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

public class To2OwnerServiceInfo implements Message {

  private final Integer nn;
  private final String sv;

  public To2OwnerServiceInfo(final Integer nn, final String sv) {
    this.nn = nn;
    this.sv = sv;
  }

  public Integer getNn() {
    return nn;
  }

  public String getSv() {
    return sv;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.TO2_OWNER_SERVICE_INFO;
  }
}
