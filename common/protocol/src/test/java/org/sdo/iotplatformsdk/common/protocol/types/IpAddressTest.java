// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.CharBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.IpAddress;

class IpAddressTest {

  InetAddress inetAddress;
  IpAddress ipAddress;
  IpAddress ipAddress1;

  @BeforeEach
  void beforeEach() throws IOException {

    inetAddress = InetAddress.getByName("www.intel.com");
    ipAddress = new IpAddress(inetAddress);
    ipAddress1 = new IpAddress(CharBuffer.wrap("[4,\"CkL3gg==\"]"));
  }

  @Test
  void test_Bean() {

    ipAddress.get();
    ipAddress.hashCode();
    ipAddress.toString();

    ipAddress.equals(ipAddress);
    ipAddress.equals(null);
    ipAddress.equals(new IpAddress(inetAddress));
  }

}
