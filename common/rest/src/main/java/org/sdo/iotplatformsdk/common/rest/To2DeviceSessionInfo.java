// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

public class To2DeviceSessionInfo {

  private Message41Store message41Store;
  private Message45Store message45Store;
  private Message47Store message47Store;
  private DeviceCryptoInfo deviceCryptoInfo;

  public To2DeviceSessionInfo() {

  }

  /**
   * Constructor.
   */
  public To2DeviceSessionInfo(Message41Store message41Store, Message45Store message45Store,
      Message47Store message47Store, DeviceCryptoInfo deviceCryptoInfo) {
    super();
    this.message41Store = message41Store;
    this.message45Store = message45Store;
    this.message47Store = message47Store;
    this.deviceCryptoInfo = deviceCryptoInfo;
  }

  public Message41Store getMessage41Store() {
    return message41Store;
  }

  public void setMessage41Store(Message41Store message41Store) {
    this.message41Store = message41Store;
  }

  public Message45Store getMessage45Store() {
    return message45Store;
  }

  public void setMessage45Store(Message45Store message45Store) {
    this.message45Store = message45Store;
  }

  public Message47Store getMessage47Store() {
    return message47Store;
  }

  public void setMessage47Store(Message47Store message47Store) {
    this.message47Store = message47Store;
  }

  public DeviceCryptoInfo getDeviceCryptoInfo() {
    return deviceCryptoInfo;
  }

  public void setDeviceCryptoInfo(DeviceCryptoInfo deviceCryptoInfo) {
    this.deviceCryptoInfo = deviceCryptoInfo;
  }

}
