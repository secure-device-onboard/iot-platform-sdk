// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;

class HashDigestTest {

  HashDigest hashDigest;
  HashDigest hashDigest1;
  HashDigest hashDigest2;

  @BeforeEach
  void beforeEach() throws IOException {

    hashDigest = new HashDigest();
    // hashDigest1 = new HashDigest(CharBuffer.wrap("{}"));
    hashDigest2 = new HashDigest(hashDigest);
  }

  @Test
  void test_Bean() {

    hashDigest.getType();
    hashDigest.hashCode();
    hashDigest.toString();

    hashDigest.equals(hashDigest);
    hashDigest.equals(null);
    hashDigest.equals(hashDigest2);
  }

}
