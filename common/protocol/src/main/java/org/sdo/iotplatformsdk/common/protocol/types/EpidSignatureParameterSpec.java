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
