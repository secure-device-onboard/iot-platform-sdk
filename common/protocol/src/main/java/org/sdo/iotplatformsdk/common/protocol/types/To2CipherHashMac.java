// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2CipherHashMacCodec.To2CipherHashMacDecoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2CipherHashMacCodec.To2CipherHashMacEncoder;

/**
 * SDO "Hash" for To2 Encrypted Message's MAC hashes.
 */
public class To2CipherHashMac implements Hash<MacType> {

  private final byte[] hash;

  To2CipherHashMac() {
    this(ByteBuffer.allocate(0));
  }

  /**
   * Constructor.
   */
  public To2CipherHashMac(final ByteBuffer hash) {

    byte[] h = new byte[hash.remaining()];
    hash.get(h);
    this.hash = h;
  }

  private To2CipherHashMac(final To2CipherHashMac that) {
    this(that.getHash());
  }

  public To2CipherHashMac(final CharBuffer cbuf) throws IOException {
    this(new To2CipherHashMacDecoder().decode(cbuf));
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
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(hash);
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

    To2CipherHashMac that = (To2CipherHashMac) o;
    return Arrays.equals(hash, that.hash);
  }

  @Override
  public String toString() {
    StringWriter w = new StringWriter();
    try {
      new To2CipherHashMacEncoder().encode(w, this);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return w.toString();
  }
}
