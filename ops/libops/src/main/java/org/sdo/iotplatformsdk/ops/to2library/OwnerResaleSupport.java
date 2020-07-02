// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

public interface OwnerResaleSupport {

  /**
   * Check if the 'Resale' protocol is supported by the Owner for the given device Identifier,
   * as per the protocol specification.
   *
   * @param deviceId device identifier
   * @return boolean value
   */
  public boolean ownerResaleSupported(String deviceId);
}
