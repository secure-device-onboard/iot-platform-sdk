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

/*
 * WARNING: THIS FILE CONTAINS DEMO CODE THAT IS NOT INTENDED FOR SECURE DEPLOYMENT. CUSTOMERS MUST
 * REPLACE THESE CLASSES WITH AN IMPLEMENTATION THAT IS SECURE WITHIN THEIR ENVIRONMENT.
 */

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * This class is used to add headers to the HTTP(S) requests.
 */
public class RequestInterceptor implements ClientHttpRequestInterceptor {

  private final String headerName;
  private final String headerValue;

  public RequestInterceptor(String headerName, String headerValue) {
    this.headerName = headerName;
    this.headerValue = headerValue;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    request.getHeaders().set(headerName, headerValue);
    return execution.execute(request, body);
  }
}
