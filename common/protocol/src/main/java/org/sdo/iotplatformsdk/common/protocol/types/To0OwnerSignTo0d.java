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

import java.time.Duration;

public class To0OwnerSignTo0d {

  private Nonce n3;
  private OwnershipProxy op;
  private Duration ws;

  /**
   * Constructor.
   */
  public To0OwnerSignTo0d(OwnershipProxy op, Duration ws, Nonce n3) {
    this.op = op;
    this.ws = ws;
    this.n3 = n3;
  }

  public Nonce getN3() {
    return n3;
  }

  public void setN3(Nonce n3) {
    this.n3 = n3;
  }

  public OwnershipProxy getOp() {
    return op;
  }

  public void setOp(OwnershipProxy op) {
    this.op = op;
  }

  public Duration getWs() {
    return ws;
  }

  public void setWs(Duration ws) {
    this.ws = ws;
  }
}
