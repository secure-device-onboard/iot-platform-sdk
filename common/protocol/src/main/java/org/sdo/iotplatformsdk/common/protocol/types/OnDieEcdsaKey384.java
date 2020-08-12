// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;

public class OnDieEcdsaKey384 extends EncodedKeySpec implements PublicKey, PrivateKey {

  public OnDieEcdsaKey384(byte[] keyBytes) {
    super(keyBytes);
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
    return KeyType.ONDIE_ECDSA_384;
  }
}
