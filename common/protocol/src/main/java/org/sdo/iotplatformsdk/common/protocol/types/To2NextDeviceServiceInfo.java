// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

public class To2NextDeviceServiceInfo implements Message {

  private final ServiceInfo dsi;
  private final Integer nn;

  public To2NextDeviceServiceInfo(final Integer nn, final ServiceInfo dsi) {
    this.nn = nn;
    this.dsi = dsi;
  }

  public ServiceInfo getDsi() {
    return dsi;
  }

  public Integer getNn() {
    return nn;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.TO2_NEXT_DEVICE_SERVICE_INFO;
  }
}
