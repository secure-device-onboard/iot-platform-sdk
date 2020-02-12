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

package org.sdo.iotplatformsdk.ops.to2library;

import java.nio.ByteBuffer;

import org.sdo.iotplatformsdk.common.protocol.security.keyexchange.KeyExchange;
import org.sdo.iotplatformsdk.common.protocol.types.CipherType;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;

/**
 * TO2 state after message 40 (TO2.HelloDevice)
 */
class To2ProvingOwner implements To2Session {

  private final CipherType cipherType;
  private final ByteBuffer iv;
  private final KeyExchange keyExchange;
  private final Nonce n6;
  private final OwnershipProxy ownershipProxy;

  To2ProvingOwner(CipherType cipherType, ByteBuffer iv, KeyExchange keyExchange, Nonce n6,
      OwnershipProxy ownershipProxy) {
    this.cipherType = cipherType;
    this.iv = iv;
    this.keyExchange = keyExchange;
    this.n6 = n6;
    this.ownershipProxy = ownershipProxy;
  }

  @Override
  public OwnershipProxy getOwnershipProxy() {
    return ownershipProxy;
  }

  CipherType getCipherType() {
    return cipherType;
  }

  ByteBuffer getIv() {
    return iv;
  }

  KeyExchange getKeyExchange() {
    return keyExchange;
  }

  Nonce getN6() {
    return n6;
  }
}
