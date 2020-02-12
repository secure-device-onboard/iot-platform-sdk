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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoResponseErrorHandler;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

class SdoResponseErrorHandlerTest {

  ClientHttpResponse response;
  HttpStatus status;
  InputStream inputstream;
  SdoError err;
  SdoProtocolException expected;
  SdoResponseErrorHandler sdoResponseErrorHandler;

  @BeforeEach
  void beforeEach() {

    inputstream = new ByteArrayInputStream(
        "{\"ec\":101,\"emsg\":255,\"em\":\"Test\"}".getBytes(StandardCharsets.UTF_8));
    response = Mockito.mock(ClientHttpResponse.class);
    sdoResponseErrorHandler = new SdoResponseErrorHandler();
    status = HttpStatus.CONFLICT;
  }

  @Test
  void test_SdoResponseErrorHandler() throws IOException {

    Mockito.when(response.getBody()).thenReturn(inputstream);

    org.junit.jupiter.api.Assertions.assertThrows(SdoProtocolException.class, () -> {
      sdoResponseErrorHandler.handleError(response, status);
    });
  }
}
