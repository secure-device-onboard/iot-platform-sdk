// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;

public class To2ReceivingDeviceServiceInfo implements To2Session {

  private final To2CipherContext deviceCipherContext;
  private final Nonce n7;
  private final long nn;
  private final To2CipherContext ownerCipherContext;
  private final OwnershipProxy ownershipProxy;

  To2ReceivingDeviceServiceInfo(To2CipherContext deviceCipherContext,
      To2CipherContext ownerCipherContext, Nonce n7, long nn, OwnershipProxy ownershipProxy) {

    this.deviceCipherContext = deviceCipherContext;
    this.ownerCipherContext = ownerCipherContext;
    this.n7 = n7;
    this.nn = nn;
    this.ownershipProxy = ownershipProxy;
  }

  public To2CipherContext getDeviceCipherContext() {
    return deviceCipherContext;
  }

  public long getNn() {
    return nn;
  }

  public To2CipherContext getOwnerCipherContext() {
    return ownerCipherContext;
  }

  @Override
  public OwnershipProxy getOwnershipProxy() {
    return ownershipProxy;
  }

  Nonce getN7() {
    return n7;
  }
}
