// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.rest;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * This class is used to add headers to the HTTP(S) requests.
 */
public class RestHeaderRequestInterceptor implements ClientHttpRequestInterceptor {

  private final String headerName;
  private final String headerValue;

  public RestHeaderRequestInterceptor(String headerName, String headerValue) {
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
