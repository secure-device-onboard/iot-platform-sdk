// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;

/**
 * Event that is used to mark an error state during TO2 for an Ownership voucher.
 */
public class To2ErrorEvent implements OwnerEvent {

  private SdoError error;
  private OwnershipProxy ownershipProxy;

  public To2ErrorEvent(SdoError error, OwnershipProxy ownershipProxy) {
    this.error = error;
    this.ownershipProxy = ownershipProxy;
  }

  public SdoError getError() {
    return error;
  }

  public void setError(SdoError error) {
    this.error = error;
  }

  public OwnershipProxy getOwnershipProxy() {
    return ownershipProxy;
  }

  public void setOwnershipProxy(OwnershipProxy ownershipProxy) {
    this.ownershipProxy = ownershipProxy;
  }
}
