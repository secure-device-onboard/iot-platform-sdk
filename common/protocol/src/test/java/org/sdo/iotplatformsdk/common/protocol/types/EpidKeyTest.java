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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey;

class EpidKeyTest {

  byte[] groupId = {0x00, 0x00, 0x00, 0x0d, (byte) 0xdd, (byte) 0xdd, (byte) 0xcc, (byte) 0xcc,
      0x00, 0x00, 0x00, 0x00, (byte) 0xee, (byte) 0xee, (byte) 0xee, 0x05};
  EpidKey epidKey;

  @BeforeEach
  void beforeEach() {

    epidKey = new EpidKey(groupId);
  }

  @Test
  void test_Bean() {

    epidKey.getAlgorithm();
    epidKey.getFormat();
    epidKey.getType();
  }

}
