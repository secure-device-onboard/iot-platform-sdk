// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
