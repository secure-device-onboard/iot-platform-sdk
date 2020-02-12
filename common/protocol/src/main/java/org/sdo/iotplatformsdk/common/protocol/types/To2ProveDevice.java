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
import java.util.UUID;

public class To2ProveDevice implements Message {

  private final ByteBuffer ai;
  private final UUID g2;
  private final Nonce n6;
  private final Nonce n7;
  private final Integer nn;
  private final ByteBuffer xb;

  /**
   * Constructor.
   */
  public To2ProveDevice(final ByteBuffer ai, final Nonce n6, final Nonce n7, final UUID g2,
      final Integer nn, final ByteBuffer xb) {

    this.ai = ai;
    this.n6 = n6;
    this.n7 = n7;
    this.g2 = g2;
    this.nn = nn;
    this.xb = xb;
  }

  public ByteBuffer getAi() {
    return ai.asReadOnlyBuffer();
  }

  public UUID getG2() {
    return g2;
  }

  public Nonce getN6() {
    return n6;
  }

  public Nonce getN7() {
    return n7;
  }

  public Integer getNn() {
    return nn;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.TO2_PROVE_DEVICE;
  }

  public ByteBuffer getXb() {
    return xb.asReadOnlyBuffer();
  }
}
