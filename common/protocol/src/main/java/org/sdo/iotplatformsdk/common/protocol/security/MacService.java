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

package org.sdo.iotplatformsdk.common.protocol.security;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import org.sdo.iotplatformsdk.common.protocol.types.HashMac;

/**
 * Provides message-digest services for SDO.
 */
public interface MacService {

  /**
   * Returns the MAC of the input.
   *
   * @param key the MAC key.
   * @param in an array of {@link ReadableByteChannel} inputs.
   * @return the completed MAC.
   */
  HashMac macOf(final byte[] key, final ReadableByteChannel... in);

  /**
   * Returns the MAC of the input.
   *
   * @param key the MAC key.
   * @param in an array of {@link ByteBuffer} inputs.
   * @return the completed MAC.
   */
  HashMac macOf(final byte[] key, final ByteBuffer... in);
}
