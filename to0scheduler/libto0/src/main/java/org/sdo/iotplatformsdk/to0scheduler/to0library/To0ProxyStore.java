/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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
