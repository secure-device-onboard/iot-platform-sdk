// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.sdo.iotplatformsdk.ops.rest.RestClient;

/**
 * Represents an BASE-64 encoding version of OpsCharSequence.
 */
public class OpsBase64Sequence extends OpsCharSequence {

  public OpsBase64Sequence(RestClient client, String valueId, UUID deviceId) {
    super(client, valueId, deviceId);
  }

  @Override
  public void setContentLength(int length) {

    int numBlocks = length / getBytesPerBlock();
    if (0 != length % getBytesPerBlock()) {
      ++numBlocks;
    }
    this.length = numBlocks * getCharsPerBlock();
  }

  @Override
  protected String getContent(byte[] data) {
    final CharBuffer cbuf =
        StandardCharsets.UTF_8.decode(Base64.getEncoder().encode(ByteBuffer.wrap(data)));

    cbuf.position(getOffset() % getCharsPerBlock());
    cbuf.limit(cbuf.position() + length());
    return cbuf.toString();
  }

  @Override
  protected OpsCharSequence newSequence() {
    return new OpsBase64Sequence(getClient(), getValueId(), getDeviceId());
  }

  @Override
  protected int getCharsPerBlock() {
    return 4;
  }

  @Override
  protected int getBytesPerBlock() {
    return 3;
  }

}
