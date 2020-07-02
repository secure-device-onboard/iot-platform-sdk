// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.nio.ByteBuffer;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

/**
 * SDO SigInfo.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2: Composite Types"
 */
public class SigInfo {

  private ByteBuffer info;
  private SignatureType sgType;

  public SigInfo(SignatureType sgType, ByteBuffer info) {
    this.sgType = sgType;
    this.info = Buffers.clone(info);
  }

  public ByteBuffer getInfo() {
    return info.asReadOnlyBuffer();
  }

  public void setInfo(ByteBuffer info) {
    this.info = Buffers.clone(info);
  }

  public SignatureType getSgType() {
    return sgType;
  }

  public void setSgType(SignatureType sgType) {
    this.sgType = sgType;
  }
}
