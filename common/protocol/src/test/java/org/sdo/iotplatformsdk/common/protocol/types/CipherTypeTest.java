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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.CipherAlgorithm;
import org.sdo.iotplatformsdk.common.protocol.types.CipherBlockMode;
import org.sdo.iotplatformsdk.common.protocol.types.CipherType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

class CipherTypeTest {

  CipherType cipherType;

  @BeforeEach
  void beforeEach() {

    cipherType = new CipherType(CipherAlgorithm.AES128, CipherBlockMode.CBC, MacType.HMAC_SHA256);
  }

  @Test
  void test_Bean() {

    cipherType.setAlgorithm(CipherAlgorithm.AES128);
    cipherType.setMacType(MacType.HMAC_SHA256);
    cipherType.setMode(CipherBlockMode.CBC);

    assertEquals(CipherAlgorithm.AES128, cipherType.getAlgorithm());
    assertEquals(MacType.HMAC_SHA256, cipherType.getMacType());
    assertEquals(CipherBlockMode.CBC, cipherType.getMode());
    cipherType.hashCode();
    cipherType.equals(cipherType);
    cipherType.equals(null);
    cipherType
        .equals(new CipherType(CipherAlgorithm.AES128, CipherBlockMode.CBC, MacType.HMAC_SHA256));
  }

}
