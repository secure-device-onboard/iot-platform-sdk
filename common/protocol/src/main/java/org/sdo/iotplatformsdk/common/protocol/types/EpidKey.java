// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;

@SuppressWarnings("serial")
public class EpidKey extends EncodedKeySpec implements PublicKey, PrivateKey {

  private static final byte[] EPID2_TEST_GROUP =
      {0x00, 0x00, 0x00, 0x0d, (byte) 0xdd, (byte) 0xdd, (byte) 0xcc, (byte) 0xcc, 0x00, 0x00, 0x00,
          0x00, (byte) 0xee, (byte) 0xee, (byte) 0xee, 0x05};

  public static final EpidKey EPID2_TEST_KEY = new EpidKey(EPID2_TEST_GROUP);

  public EpidKey(byte[] groupId) {
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

  public KeyType getType() {
    return KeyType.EPIDV2_0;
  }
}
