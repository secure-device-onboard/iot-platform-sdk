// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;

class SignatureResponseTest {

  SignatureResponse signatureResponse;

  @BeforeEach
  void beforeEach() {
    signatureResponse = new SignatureResponse();
  }

  @Test
  void test_Bean() throws IOException {
    signatureResponse.setAlg("AES");
    signatureResponse.setPk("Test");
    signatureResponse.setSg("HMAC");

    signatureResponse.getAlg();
    signatureResponse.getPk();
    signatureResponse.getSg();

  }

}

