// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
