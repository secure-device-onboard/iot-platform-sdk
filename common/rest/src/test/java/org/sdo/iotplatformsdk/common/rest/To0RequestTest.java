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

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.To0Request;

class To0RequestTest {

  To0Request to0Request;

  @BeforeEach
  void beforeEach() {
    to0Request = new To0Request();
  }

  @Test
  void test_Bean() throws IOException {
    to0Request.setGuids(null);
    to0Request.setWaitSeconds("100");

    to0Request.getGuids();
    to0Request.getWaitSeconds();
  }

}

