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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodec;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class SdoResponseErrorHandler extends DefaultResponseErrorHandler {

  @Override
  public void handleError(ClientHttpResponse response, HttpStatus status) throws IOException {

    StringBuilder builder = new StringBuilder();

    try (InputStream body = response.getBody(); Reader reader = new InputStreamReader(body)) {
      int i;
      while ((i = reader.read()) >= 0) {
        builder.append((char) i);
      }
    }

    SdoError err;
    try {
      err = new SdoErrorCodec().decoder().apply(CharBuffer.wrap(builder.toString()));

    } catch (IOException e) {

      SdoErrorCode ec;
      if (status.is5xxServerError()) {
        ec = SdoErrorCode.InternalError;

      } else {
        ec = SdoErrorCode.MessageRefused;
      }

      err = new SdoError(ec, MessageType.ERROR, builder.toString());
    }

    throw new SdoProtocolException(err);
  }
}
