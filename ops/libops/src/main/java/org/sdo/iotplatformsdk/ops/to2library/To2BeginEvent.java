// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;

/**
 * Event that is used to mark the start of TO2 for an Ownership voucher.
 */
public class To2BeginEvent implements OwnerEvent {

  private OwnershipProxy ownershipProxy;

  public To2BeginEvent(OwnershipProxy ownershipProxy) {
    this.ownershipProxy = ownershipProxy;
  }

  public OwnershipProxy getOwnershipProxy() {
    return ownershipProxy;
  }

  public void setOwnershipProxy(OwnershipProxy ownershipProxy) {
    this.ownershipProxy = ownershipProxy;
  }
}
