// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;

public interface To2Session {

  /**
   * All sessions must be associated with an ownership voucher.
   *
   * @return The SDO proxy for this session.
   */
  OwnershipProxy getOwnershipProxy();
}
