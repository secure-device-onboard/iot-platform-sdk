// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
