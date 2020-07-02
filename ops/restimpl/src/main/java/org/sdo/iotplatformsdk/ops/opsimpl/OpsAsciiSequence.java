// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.sdo.iotplatformsdk.ops.rest.RestClient;

/**
 * Represents an ASCII encoding version of OpsCharSequence.
 */
public class OpsAsciiSequence extends OpsCharSequence {

  public OpsAsciiSequence(RestClient client, String valueId, UUID deviceId) {
    super(client, valueId, deviceId);
  }

  @Override
  protected OpsCharSequence newSequence() {
    return new OpsAsciiSequence(getClient(), getValueId(), getDeviceId());
  }

  @Override
  protected String getContent(byte[] data) {
    return new String(data, StandardCharsets.US_ASCII);
  }
}
