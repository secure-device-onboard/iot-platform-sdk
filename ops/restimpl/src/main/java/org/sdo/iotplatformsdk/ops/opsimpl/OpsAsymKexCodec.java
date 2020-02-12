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

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.rest.CipherOperation;

public class OpsAsymKexCodec implements AsymKexCodec {

  private final RestClient restClient;

  public OpsAsymKexCodec(final RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public ByteBuffer buildDecipher(final String cipherAlgorithm, final ByteBuffer ct, UUID uuid) {
    byte[] byteArray = new byte[ct.remaining()];
    ct.get(byteArray, 0, byteArray.length);
    byte[] b = restClient.cipherOperations(byteArray, CipherOperation.DECIPHER.toString(), uuid);
    return ByteBuffer.wrap(b);
  }

  @Override
  public ByteBuffer buildEncipher(String cipherAlgorithm, ByteBuffer pt, UUID uuid) {
    byte[] byteArray = new byte[pt.remaining()];
    pt.get(byteArray, 0, byteArray.length);
    byte[] b = restClient.cipherOperations(byteArray, CipherOperation.ENCIPHER.toString(), uuid);
    return ByteBuffer.wrap(b);
  }
}
