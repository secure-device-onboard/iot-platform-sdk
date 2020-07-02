// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import org.sdo.iotplatformsdk.common.protocol.types.DigestType;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;

/**
 * A simple implementation of {@link DigestService}.
 */
class SimpleDigestService implements DigestService {

  // NIO reads and writes use this buffer. Bigger for fewer ops at higher memory cost.
  private static final int IO_BUFSIZ = 256;

  private final DigestType digestType;

  SimpleDigestService(final DigestType digestType) {
    this.digestType = Objects.requireNonNull(digestType);
  }

  @Override
  public HashDigest digestOf(final ReadableByteChannel... ins) {

    final ByteBuffer buf = ByteBuffer.allocate(IO_BUFSIZ);
    final MessageDigest digest;

    try {
      digest = MessageDigest.getInstance(getDigestType().toJceAlgorithm());

      for (final ReadableByteChannel in : ins) {
        while (-1 < in.read(buf)) {
          buf.flip();
          digest.update(buf);
          buf.flip();
        }
      }

    } catch (IOException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    return new HashDigest(getDigestType(), ByteBuffer.wrap(digest.digest()));
  }

  @Override
  public HashDigest digestOf(final ByteBuffer... ins) {

    final MessageDigest digest;
    try {
      digest = MessageDigest.getInstance(getDigestType().toJceAlgorithm());

      for (final ByteBuffer in : ins) {
        digest.update(in);
      }

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    return new HashDigest(getDigestType(), ByteBuffer.wrap(digest.digest()));
  }

  private DigestType getDigestType() {
    return digestType;
  }
}
