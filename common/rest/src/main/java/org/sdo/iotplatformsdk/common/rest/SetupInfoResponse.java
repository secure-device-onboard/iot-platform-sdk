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

package org.sdo.iotplatformsdk.common.rest;

import java.util.List;

/**
 * Class to manage SetupInfo. Represents the rendezvous information and the device identifier,
 */
public class SetupInfoResponse {

  private List<RendezvousInstruction> r3;
  private String g3;

  /**
   * Returns an array of {@link RendezvousInstruction}, representing the Rendezvous information.
   *
   * @return rendezvous information.
   */
  public List<RendezvousInstruction> getR3() {
    return r3;
  }

  /**
   * Stores the specified Rendezvous information, represented by an array of
   * {@link RendezvousInstruction}.
   *
   * @param r3 rendezvous information.
   */
  public void setR3(List<RendezvousInstruction> r3) {
    this.r3 = r3;
  }

  /**
   * Returns the device identifier/guid.
   *
   * @return the device identifier.
   */
  public String getG3() {
    return g3;
  }

  /**
   * Stores the specified device identifier.
   *
   * @param guid the device identifier.
   */
  public void setG3(String guid) {
    this.g3 = guid;
  }
}
