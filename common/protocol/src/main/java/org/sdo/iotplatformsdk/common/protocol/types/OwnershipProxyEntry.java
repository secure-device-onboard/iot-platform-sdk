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

/**
 * PM.OwnershipProxy.en.
 *
 * @see "SDO Protocol Specification, 1.13b, 5.2.3: PM.OwnershipProxy"
 */
public class OwnershipProxyEntry {

  private HashDigest hc;
  private HashDigest hp;
  private PublicKey pk;

  /**
   * Constructor.
   */
  public OwnershipProxyEntry(HashDigest hp, HashDigest hc, PublicKey pk) {
    this.hp = hp;
    this.hc = hc;
    this.pk = pk;
  }

  public HashDigest getHc() {
    return hc;
  }

  public HashDigest getHp() {
    return hp;
  }

  public PublicKey getPk() {
    return pk;
  }

  public void setHc(HashDigest hc) {
    this.hc = hc;
  }

  public void setHp(HashDigest hp) {
    this.hp = hp;
  }

  public void setPk(PublicKey pk) {
    this.pk = pk;
  }
}
