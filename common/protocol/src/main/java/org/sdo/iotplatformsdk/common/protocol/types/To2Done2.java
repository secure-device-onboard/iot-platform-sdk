// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

public class To2Done2 implements Message {

  private final Nonce n7;

  public To2Done2(Nonce n7) {
    this.n7 = n7;
  }

  public Nonce getN7() {
    return n7;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.TO2_DONE2;
  }
}
