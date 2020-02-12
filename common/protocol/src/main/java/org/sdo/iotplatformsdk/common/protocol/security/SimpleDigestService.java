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
