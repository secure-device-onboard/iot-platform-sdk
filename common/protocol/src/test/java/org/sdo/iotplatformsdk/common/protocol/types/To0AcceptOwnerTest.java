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

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.To0AcceptOwner;

class To0AcceptOwnerTest {

  To0AcceptOwner to0AcceptOwner;

  @BeforeEach
  void beforeEach() {

    to0AcceptOwner = new To0AcceptOwner(Duration.ofMillis(10000));
  }

  @Test
  void test_Bean() {

    to0AcceptOwner.setWs(Duration.ofMillis(10000));

    assertEquals(Duration.ofMillis(10000), to0AcceptOwner.getWs());
  }

}
