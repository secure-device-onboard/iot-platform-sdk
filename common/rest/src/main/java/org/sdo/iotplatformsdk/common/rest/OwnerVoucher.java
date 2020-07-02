// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.util.List;
import java.util.Optional;

/**
 * Parses the Owner Voucher messages as JSON.
 *
 * <p>Represents the Ownership voucher as per the SDO protocol. For more information about
 * the fields. Please refer to the SDO protocol document.
 */
public class OwnerVoucher {

  private int sz;
  private OwnerVoucherHeader oh;
  private List<Object> hmac;
  private Optional<List<Object>> dc;
  private List<OwnerVoucherEntry> en;

  public OwnerVoucher() {
    this.dc = Optional.empty();
  }

  public int getSz() {
    return sz;
  }

  public void setSz(int sz) {
    this.sz = sz;
  }

  public OwnerVoucherHeader getOh() {
    return oh;
  }

  public void setOh(OwnerVoucherHeader oh) {
    this.oh = oh;
  }

  public List<Object> getHmac() {
    return hmac;
  }

  public void setHmac(List<Object> hmac) {
    this.hmac = hmac;
  }

  public Optional<List<Object>> getDc() {
    return dc;
  }

  public void setDc(List<Object> dc) {
    this.dc = Optional.of(dc);
  }

  public List<OwnerVoucherEntry> getEn() {
    return en;
  }

  public void setEn(List<OwnerVoucherEntry> en) {
    this.en = en;
  }

}
