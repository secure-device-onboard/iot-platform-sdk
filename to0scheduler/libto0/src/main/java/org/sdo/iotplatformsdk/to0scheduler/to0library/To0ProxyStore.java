// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.to0library;

import java.io.IOException;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;

/**
 * Interface that provides a method to retrieve the ownership voucher.
 */
public interface To0ProxyStore {

  /**
   * Returns a {@link OwnershipProxy} object for the specified device identifier.
   *
   * @param deviceId the device identifier.
   * @return ownership voucher.
   * @throws IOException when an error occurs while retrieving the ownership voucher.
   */
  public OwnershipProxy getProxy(String deviceId) throws IOException;
}
