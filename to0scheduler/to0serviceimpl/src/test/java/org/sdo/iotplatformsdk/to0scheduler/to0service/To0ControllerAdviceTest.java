// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.to0scheduler.rest.To0ControllerAdvice;

class To0ControllerAdviceTest {

  To0ControllerAdvice sdoControllerAdvice;
  Throwable throwable;

  @BeforeEach
  void beforeEach() {

    sdoControllerAdvice = new To0ControllerAdvice();
    throwable = new Throwable();
  }

  @Test
  void test_SdoControllerAdvice() throws IOException {

    sdoControllerAdvice.handleException(throwable);
  }
}
