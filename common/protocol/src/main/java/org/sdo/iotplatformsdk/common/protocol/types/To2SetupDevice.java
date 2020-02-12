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

public class To2SetupDevice implements Message {

  private final SignatureBlock noh;
  private final Integer osinn;

  public To2SetupDevice(final Integer osinn, final SignatureBlock noh) {
    this.osinn = osinn;
    this.noh = noh;
  }

  public SignatureBlock getNoh() {
    return noh;
  }

  public Integer getOsinn() {
    return osinn;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.TO2_SETUP_DEVICE;
  }
}
