// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;

/**
 * Event that is used to mark the end of TO2 for an Ownership voucher.
 */
public class To2EndEvent implements OwnerEvent {

  private OwnershipProxy newOwnershipProxy;
  private OwnershipProxy oldOwnershipProxy;

  public To2EndEvent(OwnershipProxy oldProxy, OwnershipProxy newProxy) {
    this.oldOwnershipProxy = oldProxy;
    this.newOwnershipProxy = newProxy;
  }

  public OwnershipProxy getNewOwnershipProxy() {
    return newOwnershipProxy;
  }

  public void setNewOwnershipProxy(OwnershipProxy newOwnershipProxy) {
    this.newOwnershipProxy = newOwnershipProxy;
  }

  public OwnershipProxy getOldOwnershipProxy() {
    return oldOwnershipProxy;
  }

  public void setOldOwnershipProxy(OwnershipProxy oldOwnershipProxy) {
    this.oldOwnershipProxy = oldOwnershipProxy;
  }
}
