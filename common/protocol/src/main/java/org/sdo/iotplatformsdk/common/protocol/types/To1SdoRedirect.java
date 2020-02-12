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

package org.sdo.iotplatformsdk.common.protocol.types;

import java.net.InetAddress;

public class To1SdoRedirect implements Message {

  private String dns1;
  private InetAddress i1;
  private Integer port1;
  private HashDigest to0dh;

  /**
   * Constructor.
   */
  public To1SdoRedirect(InetAddress i1, String dns1, Integer port1, HashDigest to0dh) {
    this.i1 = i1;
    this.dns1 = dns1;
    this.port1 = port1;
    this.to0dh = to0dh;
  }

  public String getDns1() {
    return dns1;
  }

  public void setDns1(String dns1) {
    this.dns1 = dns1;
  }

  public InetAddress getI1() {
    return i1;
  }

  public void setI1(InetAddress i1) {
    this.i1 = i1;
  }

  public Integer getPort1() {
    return port1;
  }

  public void setPort1(Integer port1) {
    this.port1 = port1;
  }

  public HashDigest getTo0dh() {
    return to0dh;
  }

  public void setTo0dh(HashDigest to0dh) {
    this.to0dh = to0dh;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.TO1_SDO_REDIRECT;
  }
}
