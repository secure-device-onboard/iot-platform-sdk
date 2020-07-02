// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.util.List;

/**
 * Each entry object for OwnerVoucherEntry.
 */
public class OwnerVoucherEntry {
  private OwnerVoucherEntryBodyObject bo;
  private List<Object> pk;
  private List<Object> sg;

  public OwnerVoucherEntryBodyObject getBo() {
    return bo;
  }

  public void setBo(OwnerVoucherEntryBodyObject bo) {
    this.bo = bo;
  }

  public List<Object> getPk() {
    return pk;
  }

  public void setPk(List<Object> pk) {
    this.pk = pk;
  }

  public List<Object> getSg() {
    return sg;
  }

  public void setSg(List<Object> sg) {
    this.sg = sg;
  }

}
