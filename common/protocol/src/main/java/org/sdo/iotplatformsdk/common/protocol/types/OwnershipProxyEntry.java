// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
