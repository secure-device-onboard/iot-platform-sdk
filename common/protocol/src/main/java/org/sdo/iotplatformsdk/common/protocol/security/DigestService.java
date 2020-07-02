// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;

/**
 * Provides message-digest services for SDO.
 */
public interface DigestService {

  /**
   * Returns the digest of the input.
   *
   * @param in an array of {@link ReadableByteChannel} inputs.
   * @return the completed digest.
   */
  HashDigest digestOf(final ReadableByteChannel... in);

  /**
   * Returns the digest of the input.
   *
   * @param in an array of {@link ByteBuffer} inputs.
   * @return the completed digest.
   */
  HashDigest digestOf(final ByteBuffer... in);
}
