// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;
import org.sdo.iotplatformsdk.common.protocol.types.To1SdoRedirect;

class To1SdoRedirectTest {

  InetAddress i1;
  HashDigest to0dh;
  To1SdoRedirect to1SdoRedirect;

  @BeforeEach
  void beforeEach() throws UnknownHostException {

    i1 = InetAddress.getByName("www.intel.com");
    to0dh = new HashDigest();
    to1SdoRedirect = new To1SdoRedirect(i1, "Test", 8000, to0dh);
  }

  @Test
  void test_Bean() {

    to1SdoRedirect.setDns1("Test");
    to1SdoRedirect.setI1(i1);
    to1SdoRedirect.setPort1(8000);
    to1SdoRedirect.setTo0dh(to0dh);

    assertEquals(i1, to1SdoRedirect.getI1());
    assertEquals("Test", to1SdoRedirect.getDns1());
    assertEquals(Integer.valueOf(8000), to1SdoRedirect.getPort1());
    assertEquals(to0dh, to1SdoRedirect.getTo0dh());
    to1SdoRedirect.getType();
    to1SdoRedirect.getVersion();
  }

}
