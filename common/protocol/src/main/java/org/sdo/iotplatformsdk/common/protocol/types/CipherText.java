// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.nio.ByteBuffer;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

/**
 * SDO cipher-text envelope.
 *
 * <p>@see "SDO Protocol Specification, 1.13b, 4.5: Encrypted Message Body"
 */
public class CipherText {

  private final byte[] iv;
  private final byte[] ct;

  public CipherText(ByteBuffer iv, ByteBuffer ct) {
    this.iv = Buffers.unwrap(iv);
    this.ct = Buffers.unwrap(ct);
  }

  public ByteBuffer getIv() {
    return ByteBuffer.wrap(this.iv).duplicate();
  }

  public ByteBuffer getCt() {
    return ByteBuffer.wrap(this.ct).duplicate();
  }
}
