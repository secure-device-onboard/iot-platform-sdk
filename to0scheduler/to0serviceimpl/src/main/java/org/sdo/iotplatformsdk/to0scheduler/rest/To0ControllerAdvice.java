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

package org.sdo.iotplatformsdk.to0scheduler.rest;

import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class To0ControllerAdvice {

  public Logger getLogger() {
    return LoggerFactory.getLogger(getClass());
  }

  /**
   * Exception handler class for REST calls.
   *
   * @param t exception
   * @return response
   * @throws SdoProtocolException when {@link SdoProtocolException} occurs
   */
  @ExceptionHandler
  public ResponseEntity<?> handleException(Throwable t) throws SdoProtocolException {

    getLogger().debug(t.getMessage(), t);

    final SdoError err;
    final HttpStatus status;

    if (t instanceof SdoProtocolException) {
      err = ((SdoProtocolException) t).getError();
      status = HttpStatus.BAD_REQUEST;

    } else {

      final StackTraceElement ste = t.getStackTrace()[0];
      final String at = " in " + ste.getClassName() + "." + ste.getMethodName() + " at "
          + ste.getFileName() + ":" + ste.getLineNumber();
      final String errMessage;
      if (null != t.getMessage()) {
        errMessage = t.getMessage() + at;
      } else {
        errMessage = t.getClass().getName() + at;
      }

      err = new SdoError(SdoErrorCode.InternalError, MessageType.ERROR, errMessage);
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    // Don't take a risk building an error object. The server's already thrown
    // an exception. Build the response as trivially as possible.
    final String body = "{\"ec\":" + err.getEc().toInteger() + ",\"emsg\":" + err.getEmsg()
        + ",\"em\":\"" + err.getEm() + "\"}";
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
  }
}
