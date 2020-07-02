// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.security.cert.CertPath;
import java.util.Collections;
import java.util.List;

/**
 * PM.OwnershipProxy.
 *
 * @see "SDO Protocol Specification, 1.13b, 5.2.3: PM.OwnershipProxy"
 */
public class OwnershipProxy {

  private CertPath dc;
  private List<SignatureBlock> en;
  private HashMac hmac;
  private OwnershipProxyHeader oh;

  /**
   * No-arg constructor.
   */
  public OwnershipProxy() {
    this.oh = new OwnershipProxyHeader();
    this.hmac = new HashMac();
    this.dc = null;
    this.en = Collections.emptyList();
  }

  /**
   * Multi-argument constructor.
   */
  public OwnershipProxy(OwnershipProxyHeader oh, HashMac hmac, CertPath dc,
      List<SignatureBlock> en) {

    this.oh = oh;
    this.hmac = hmac;
    this.dc = dc;
    this.en = en;
  }

  public CertPath getDc() {
    return dc;
  }

  public void setDc(CertPath dc) {
    this.dc = dc;
  }

  public List<SignatureBlock> getEn() {
    return en;
  }

  public void setEn(List<SignatureBlock> en) {
    this.en = en;
  }

  public HashMac getHmac() {
    return hmac;
  }

  public void setHmac(HashMac hmac) {
    this.hmac = hmac;
  }

  public OwnershipProxyHeader getOh() {
    return oh;
  }

  public void setOh(OwnershipProxyHeader oh) {
    this.oh = oh;
  }
}
