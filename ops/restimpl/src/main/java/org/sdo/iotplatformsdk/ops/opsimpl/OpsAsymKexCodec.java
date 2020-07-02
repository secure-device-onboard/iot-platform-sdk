// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.nio.ByteBuffer;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.rest.CipherOperation;
import org.sdo.iotplatformsdk.ops.rest.RestClient;

public class OpsAsymKexCodec implements AsymKexCodec {

  private final RestClient restClient;

  public OpsAsymKexCodec(final RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public ByteBuffer buildDecipher(final ByteBuffer ct, UUID uuid) {
    byte[] byteArray = new byte[ct.remaining()];
    ct.get(byteArray, 0, byteArray.length);
    byte[] b = restClient.cipherOperations(byteArray, CipherOperation.DECIPHER.toString(), uuid);
    return ByteBuffer.wrap(b);
  }

  @Override
  public ByteBuffer buildEncipher(ByteBuffer pt, UUID uuid) {
    byte[] byteArray = new byte[pt.remaining()];
    pt.get(byteArray, 0, byteArray.length);
    byte[] b = restClient.cipherOperations(byteArray, CipherOperation.ENCIPHER.toString(), uuid);
    return ByteBuffer.wrap(b);
  }
}
