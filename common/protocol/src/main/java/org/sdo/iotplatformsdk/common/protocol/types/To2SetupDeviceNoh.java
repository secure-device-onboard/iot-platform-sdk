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

public class To2SetupDeviceNoh {

  private UUID g3;
  private Nonce n7;
  private RendezvousInfo r3;

  /**
   * Constructor.
   */
  public To2SetupDeviceNoh(RendezvousInfo r3, UUID g3, Nonce n7) {
    this.r3 = r3;
    this.g3 = g3;
    this.n7 = n7;
  }

  public UUID getG3() {
    return g3;
  }

  public void setG3(UUID g3) {
    this.g3 = g3;
  }

  public Nonce getN7() {
    return n7;
  }

  public void setN7(Nonce n7) {
    this.n7 = n7;
  }

  public RendezvousInfo getR3() {
    return r3;
  }

  public void setR3(RendezvousInfo r3) {
    this.r3 = r3;
  }
}
