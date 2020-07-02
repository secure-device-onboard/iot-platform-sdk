// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

/**
 * SDO encrypted message.
 *
 * @see "SDO Protocol Specification, 1.13b, 4.5: Encrypted Message Body"
 */
public class EncryptedMessage {

  private final CipherText ct;
  private final To2CipherHashMac hmac;

  public EncryptedMessage(CipherText ct, To2CipherHashMac hmac) {
    this.ct = ct;
    this.hmac = hmac;
  }

  public CipherText getCt() {
    return ct;
  }

  public To2CipherHashMac getHmac() {
    return hmac;
  }
}
