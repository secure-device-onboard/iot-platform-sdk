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
