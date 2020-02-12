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

import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

/**
 * SDO SigInfo.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2: Composite Types"
 */
public class SigInfo {

  private ByteBuffer info;
  private SignatureType sgType;

  public SigInfo(SignatureType sgType, ByteBuffer info) {
    this.sgType = sgType;
    this.info = Buffers.clone(info);
  }

  public ByteBuffer getInfo() {
    return info.asReadOnlyBuffer();
  }

  public void setInfo(ByteBuffer info) {
    this.info = Buffers.clone(info);
  }

  public SignatureType getSgType() {
    return sgType;
  }

  public void setSgType(SignatureType sgType) {
    this.sgType = sgType;
  }
}
