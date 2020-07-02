// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
