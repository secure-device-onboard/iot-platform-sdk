// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

public class EpidSignatureParameterSpec implements AlgorithmParameterSpec {

  Nonce nonce;
  ByteBuffer taId;

  public EpidSignatureParameterSpec(Nonce nonce, ByteBuffer taId) {
    this.nonce = nonce;
    this.taId = Buffers.clone(taId);
  }

  public Nonce getNonce() {
    return nonce;
  }

  public void setNonce(Nonce nonce) {
    this.nonce = nonce;
  }

  public ByteBuffer getTaId() {
    return taId.asReadOnlyBuffer();
  }

  public void setTaId(ByteBuffer taId) {
    this.taId = Buffers.clone(taId);
  }
}
