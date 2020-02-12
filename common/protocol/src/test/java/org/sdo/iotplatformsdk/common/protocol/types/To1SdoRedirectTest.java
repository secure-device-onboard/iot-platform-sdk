/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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
