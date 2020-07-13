// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Objects;
import org.sdo.iotplatformsdk.common.protocol.codecs.HashMacCodec.HashMacDecoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.HashMacCodec.HashMacEncoder;

/**
 * SDO "Hash" for MAC hashes.
 */
public class HashMac implements Hash<MacType> {

  private final byte[] hash;
  private final MacType type;

  HashMac() {
    this(MacType.NONE, ByteBuffer.allocate(0));
  }

  /**
   * Constructor.
   */
  public HashMac(final MacType type, final ByteBuffer hash) {
    this.type = type;

    byte[] h = new byte[hash.remaining()];
    hash.get(h);
    this.hash = h;
  }

  private HashMac(final HashMac that) {
    this(that.getType(), that.getHash());
  }

  public HashMac(final CharBuffer cbuf) throws IOException {
    this(new HashMacDecoder().decode(cbuf));
  }

  @Override
  public ByteBuffer getHash() {
    ByteBuffer buf = ByteBuffer.allocate(hash.length);
    buf.put(hash);
    buf.flip();
    return buf;
  }

  @Override
  public MacType getType() {
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

    HashMac that = (HashMac) o;
    return type == that.type && Arrays.equals(hash, that.hash);
  }

  @Override
  public String toString() {
    StringWriter w = new StringWriter();
    try {
      new HashMacEncoder().encode(w, this);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return w.toString();
  }
}
