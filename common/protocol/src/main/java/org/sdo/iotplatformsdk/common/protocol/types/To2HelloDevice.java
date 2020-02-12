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

import java.util.UUID;

public class To2HelloDevice implements Message {

  private final CipherType cs;
  private final SigInfo ea;
  private final UUID g2;
  private final KeyExchangeType kx;
  private final Nonce n5;
  private final KeyEncoding pe;

  /**
   * Constructor.
   */
  public To2HelloDevice(final UUID g2, final Nonce n5, final KeyEncoding pe,
      final KeyExchangeType kx, final CipherType cs, final SigInfo ea) {

    this.g2 = g2;
    this.n5 = n5;
    this.pe = pe;
    this.kx = kx;
    this.cs = cs;
    this.ea = ea;
  }

  public CipherType getCs() {
    return cs;
  }

  public SigInfo getEa() {
    return ea;
  }

  public UUID getG2() {
    return g2;
  }

  public KeyExchangeType getKx() {
    return kx;
  }

  public Nonce getN5() {
    return n5;
  }

  public KeyEncoding getPe() {
    return pe;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.TO2_HELLO_DEVICE;
  }
}
