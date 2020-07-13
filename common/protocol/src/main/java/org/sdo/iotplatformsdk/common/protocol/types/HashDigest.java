// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Objects;
import org.sdo.iotplatformsdk.common.protocol.codecs.HashDigestCodec.HashDigestDecoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.HashDigestCodec.HashDigestEncoder;

/**
 * SDO "Hash" for digest hashes.
 */
public class HashDigest implements Hash<DigestType> {

  private final byte[] hash;
  private final DigestType type;

  public HashDigest() {
    this(DigestType.NONE, ByteBuffer.allocate(0));
  }

  /**
   * Constructor.
   */
  public HashDigest(final DigestType type, final ByteBuffer hash) {
    this.type = type;

    byte[] h = new byte[hash.remaining()];
    hash.get(h);
    this.hash = h;
  }

  public HashDigest(final CharBuffer cbuf) throws IOException {
    this(new HashDigestDecoder().decode(cbuf));
  }

  public HashDigest(final HashDigest that) {
    this(that.getType(), that.getHash());
  }

  @Override
  public ByteBuffer getHash() {
    ByteBuffer buf = ByteBuffer.allocate(hash.length);
    buf.put(hash);
    buf.flip();
    return buf;
  }

  @Override
  public DigestType getType() {
    return type;
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(type);
    result = 31 * result + Arrays.hashCode(hash);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HashDigest that = (HashDigest) o;
    return type == that.type && Arrays.equals(hash, that.hash);
  }

  @Override
  public String toString() {
    StringWriter w = new StringWriter();
    try {
      new HashDigestEncoder().encode(w, this);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return w.toString();
  }
}
