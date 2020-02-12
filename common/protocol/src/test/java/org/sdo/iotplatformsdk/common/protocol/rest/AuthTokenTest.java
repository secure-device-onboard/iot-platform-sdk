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

package org.sdo.iotplatformsdk.common.protocol.rest;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.rest.AuthToken;

class AuthTokenTest {

  AuthToken authToken1;
  AuthToken authToken2;

  @BeforeEach
  void beforeEach() {

    authToken1 = new AuthToken("Bearer 00000000000000000000000000000000");
    authToken2 = new AuthToken(UUID.randomUUID());
  }

  @Test
  void test_Auth_Token() throws IOException {

    authToken1.toString();
    authToken2.getValue();
    authToken2.getUuid();
  }
}
