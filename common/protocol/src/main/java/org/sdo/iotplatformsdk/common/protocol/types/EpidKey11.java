// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

@SuppressWarnings("serial")
public class EpidKey11 extends EpidKey {

  public EpidKey11(byte[] groupId) {
    super(groupId);
  }

  @Override
  public String getAlgorithm() {
    return getType().toString();
  }

  @Override
  public String getFormat() {
    return getType().toString();
  }

  @Override
  public KeyType getType() {
    return KeyType.EPIDV1_1;
  }
}
