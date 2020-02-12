/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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
