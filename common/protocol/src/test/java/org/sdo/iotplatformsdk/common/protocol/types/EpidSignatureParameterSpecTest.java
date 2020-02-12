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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.EpidSignatureParameterSpec;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;

class EpidSignatureParameterSpecTest {

  ByteBuffer taId;
  EpidSignatureParameterSpec epidSignatureParameterSpec;
  Nonce nonce;

  @BeforeEach
  void beforeEach() {

    nonce = new Nonce(new SecureRandom());
    taId = ByteBuffer.wrap("Test".getBytes());
    epidSignatureParameterSpec = new EpidSignatureParameterSpec(nonce, taId);
  }

  @Test
  void test_Bean() {

    epidSignatureParameterSpec.setNonce(nonce);
    epidSignatureParameterSpec.setTaId(taId);

    assertEquals(nonce, epidSignatureParameterSpec.getNonce());
    assertEquals(taId, epidSignatureParameterSpec.getTaId());
  }

}
