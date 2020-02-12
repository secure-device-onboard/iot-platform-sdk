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
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2NextDeviceServiceInfo;

class To2NextDeviceServiceInfoTest {

  ServiceInfo dsi;
  To2NextDeviceServiceInfo to2NextDeviceServiceInfo;

  @BeforeEach
  void beforeEach() {

    dsi = new ServiceInfo();
    to2NextDeviceServiceInfo = new To2NextDeviceServiceInfo(0, dsi);
  }

  @Test
  void test_Bean() {

    assertEquals(Integer.valueOf(0), to2NextDeviceServiceInfo.getNn());
    assertEquals(dsi, to2NextDeviceServiceInfo.getDsi());

    to2NextDeviceServiceInfo.getType();
    to2NextDeviceServiceInfo.getVersion();
  }

}
