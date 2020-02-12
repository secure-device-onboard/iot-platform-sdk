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
