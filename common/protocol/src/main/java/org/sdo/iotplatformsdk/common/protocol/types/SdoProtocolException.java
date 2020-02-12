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

package org.sdo.iotplatformsdk.common.protocol.types;

import java.io.IOException;
import java.io.StringWriter;

import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodec;

@SuppressWarnings("serial")
public class SdoProtocolException extends RuntimeException {

  private SdoError error;

  public SdoProtocolException(SdoError err) {
    super(buildMessage(err));
    setError(err);
  }

  public SdoProtocolException(SdoError err, Throwable cause) {
    super(buildMessage(err), cause);
    setError(err);
  }

  private static String buildMessage(SdoError err) {

    try {
      StringWriter writer = new StringWriter();
      new SdoErrorCodec().encoder().apply(writer, err);
      return writer.toString();

    } catch (IOException e) {
      return err.getEm();
    }
  }

  public SdoError getError() {
    return error;
  }

  protected void setError(SdoError err) {
    this.error = err;
  }
}
