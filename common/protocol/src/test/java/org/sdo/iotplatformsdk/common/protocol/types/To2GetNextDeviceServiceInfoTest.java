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
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2GetNextDeviceServiceInfo;

class To2GetNextDeviceServiceInfoTest {

  PreServiceInfo psi;
  To2GetNextDeviceServiceInfo to2GetNextDeviceServiceInfo;

  @BeforeEach
  void beforeEach() {

    psi = new PreServiceInfo();
    to2GetNextDeviceServiceInfo = new To2GetNextDeviceServiceInfo(0, psi);
  }

  @Test
  void test_Bean() {

    assertEquals(Integer.valueOf(0), to2GetNextDeviceServiceInfo.getNn());
    assertEquals(psi, to2GetNextDeviceServiceInfo.getPsi());

    to2GetNextDeviceServiceInfo.getType();
    to2GetNextDeviceServiceInfo.getVersion();
  }

}
