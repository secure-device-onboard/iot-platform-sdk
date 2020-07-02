// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

class HashMacTest {

  HashMac hashMac;
  HashMac hashMac1;

  @BeforeEach
  void beforeEach() throws IOException {

    hashMac = new HashMac();
    hashMac1 = new HashMac(MacType.HMAC_SHA256, ByteBuffer.wrap("test".getBytes()));
  }

  @Test
  void test_Bean() {

    hashMac.getHash();
    hashMac.getType();
    hashMac.hashCode();
    hashMac.toString();

    hashMac.equals(null);
    hashMac.equals(hashMac);
    hashMac.equals(hashMac1);
  }

}
