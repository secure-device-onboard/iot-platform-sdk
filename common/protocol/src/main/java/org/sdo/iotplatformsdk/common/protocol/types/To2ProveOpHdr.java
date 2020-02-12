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

/**
 * TO2.ProveOPHdr.
 */
public final class To2ProveOpHdr implements Message {

  private final SigInfo eb;
  private final HashMac hmac;
  private final Nonce n5;
  private final Nonce n6;
  private final OwnershipProxyHeader oh;
  private final Integer sz;
  private final ByteBuffer xa;

  /**
   * Constructor.
   */
  public To2ProveOpHdr(final Integer sz, final OwnershipProxyHeader oh, final HashMac hmac,
      final Nonce n5, final Nonce n6, final SigInfo eb, final ByteBuffer xa) {

    this.sz = sz;
    this.oh = oh;
    this.hmac = hmac;
    this.n5 = n5;
    this.n6 = n6;
    this.eb = eb;
    this.xa = xa;
  }

  public SigInfo getEb() {
    return eb;
  }

  public HashMac getHmac() {
    return hmac;
  }

  public Nonce getN5() {
    return n5;
  }

  public Nonce getN6() {
    return n6;
  }

  public OwnershipProxyHeader getOh() {
    return oh;
  }

  public Integer getSz() {
    return sz;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.TO2_PROVE_OP_HDR;
  }

  public ByteBuffer getXa() {
    return xa.asReadOnlyBuffer();
  }
}
