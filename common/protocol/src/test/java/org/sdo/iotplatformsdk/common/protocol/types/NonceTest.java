// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.nio.CharBuffer;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;

class NonceTest {

  Nonce nonce;
  Nonce nonce1;

  @BeforeEach
  void beforeEach() {

    nonce = new Nonce(new SecureRandom());
    nonce1 = new Nonce(CharBuffer.wrap(nonce.toString()));
  }

  @Test
  void test_Bean() {

    nonce.getBytes();
    nonce.hashCode();
    nonce.toString();

    nonce.equals(null);
    nonce.equals(nonce);
    nonce.equals(new Nonce(new SecureRandom()));
  }

}
