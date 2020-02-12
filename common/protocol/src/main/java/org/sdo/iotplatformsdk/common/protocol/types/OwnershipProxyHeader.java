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

import java.security.PublicKey;
import java.util.UUID;

/**
 * PM.OwnershipProxy.oh.
 *
 * @see "SDO Protocol Specification, 1.13b, 5.2.3: PM.OwnershipProxy"
 */
public class OwnershipProxyHeader {

  public static final Version THIS_VERSION = Version.VERSION_1_13;
  private String deviceInfo;
  private UUID guid;
  private HashDigest hdc;
  private KeyEncoding pe;
  private PublicKey pk;
  private RendezvousInfo rendezvousInfo;

  /**
   * Default constructor.
   */
  public OwnershipProxyHeader() {
    this.pe = KeyEncoding.NONE;
    this.rendezvousInfo = new RendezvousInfo();
    this.guid = Uuids.buildRandomUuid();
    this.deviceInfo = "";
    this.pk = null;
    this.hdc = null;
  }

  /**
   * Multi-argument constructor.
   */
  public OwnershipProxyHeader(KeyEncoding pe, RendezvousInfo r, UUID g, String d, PublicKey pk,
      HashDigest hdc) {

    this.pe = pe;
    this.rendezvousInfo = r;
    this.guid = g;
    this.deviceInfo = d;
    this.pk = pk;
    this.hdc = hdc;
  }

  public String getD() {
    return deviceInfo;
  }

  public void setD(String d) {
    this.deviceInfo = d;
  }

  public UUID getG() {
    return guid;
  }

  public void setG(UUID g) {
    this.guid = g;
  }

  public HashDigest getHdc() {
    return hdc;
  }

  public void setHdc(HashDigest hdc) {
    this.hdc = hdc;
  }

  public KeyEncoding getPe() {
    return pe;
  }

  public void setPe(KeyEncoding pe) {
    this.pe = pe;
  }

  public PublicKey getPk() {
    return pk;
  }

  public void setPk(PublicKey pk) {
    this.pk = pk;
  }

  public Version getPv() {
    return THIS_VERSION;
  }

  public RendezvousInfo getR() {
    return rendezvousInfo;
  }

  public void setR(RendezvousInfo r) {
    this.rendezvousInfo = r;
  }
}
