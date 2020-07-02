// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
