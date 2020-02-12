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

package org.sdo.iotplatformsdk.ops.to2library;

import java.util.Iterator;
import java.util.function.Supplier;

import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;

public class To2SendingOwnerServiceInfo implements To2Session {

  private final To2CipherContext deviceCipherContext;
  private final OwnershipProxy newOwnershipProxy;
  private final To2CipherContext ownerCipherContext;
  private final OwnershipProxy ownershipProxy;
  private final Iterator<Supplier<ServiceInfo>> serviceInfoSupplierIt;

  To2SendingOwnerServiceInfo(To2CipherContext deviceCipherContext,
      To2CipherContext ownerCipherContext, Iterator<Supplier<ServiceInfo>> serviceInfoSupplierIt,
      OwnershipProxy ownershipProxy, OwnershipProxy newOwnershipProxy) {
    this.deviceCipherContext = deviceCipherContext;
    this.ownerCipherContext = ownerCipherContext;
    this.serviceInfoSupplierIt = serviceInfoSupplierIt;
    this.ownershipProxy = ownershipProxy;
    this.newOwnershipProxy = newOwnershipProxy;
  }

  public To2CipherContext getDeviceCipherContext() {
    return deviceCipherContext;
  }

  public To2CipherContext getOwnerCipherContext() {
    return ownerCipherContext;
  }

  @Override
  public OwnershipProxy getOwnershipProxy() {
    return ownershipProxy;
  }

  OwnershipProxy getNewOwnershipProxy() {
    return newOwnershipProxy;
  }

  Iterator<Supplier<ServiceInfo>> getServiceInfoSupplierIt() {
    return serviceInfoSupplierIt;
  }
}
