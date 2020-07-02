// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.util.List;

/**
 * Class to manage Ownership Voucher entries.
 */
public class OwnerVoucherEntryBodyObject {
  private List<Object> hp;
  private List<Object> hc;
  private List<Object> pk;

  public List<Object> getHp() {
    return hp;
  }

  public void setHp(List<Object> hp) {
    this.hp = hp;
  }

  public List<Object> getHc() {
    return hc;
  }

  public void setHc(List<Object> hc) {
    this.hc = hc;
  }

  public List<Object> getPk() {
    return pk;
  }

  public void setPk(List<Object> pk) {
    this.pk = pk;
  }

}
