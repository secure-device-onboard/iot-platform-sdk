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
 * SDO cipher-text envelope.
 *
 * <p>@see "SDO Protocol Specification, 1.13b, 4.5: Encrypted Message Body"
 */
public class CipherText {

  private final byte[] iv;
  private final byte[] ct;

  public CipherText(ByteBuffer iv, ByteBuffer ct) {
    this.iv = Buffers.unwrap(iv);
    this.ct = Buffers.unwrap(ct);
  }

  public ByteBuffer getIv() {
    return ByteBuffer.wrap(this.iv).duplicate();
  }

  public ByteBuffer getCt() {
    return ByteBuffer.wrap(this.ct).duplicate();
  }
}
