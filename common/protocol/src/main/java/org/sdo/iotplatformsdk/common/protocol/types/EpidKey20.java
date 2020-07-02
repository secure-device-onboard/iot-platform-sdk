// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

@SuppressWarnings("serial")
public class EpidKey20 extends EpidKey {

  public EpidKey20(byte[] groupId) {
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
    return KeyType.EPIDV2_0;
  }
}
