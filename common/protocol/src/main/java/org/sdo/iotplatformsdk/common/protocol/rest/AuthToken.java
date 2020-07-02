// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.rest;

import java.nio.ByteBuffer;
import java.util.UUID;

public class AuthToken {

  private static final String BEARER = "Bearer ";

  private final byte[] value;

  /**
   * Constructor.
   *
   * @param uuid guid
   */
  public AuthToken(UUID uuid) {

    value = new byte[16];
    ByteBuffer bb = ByteBuffer.wrap(value);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
  }

  /**
   * Constructor.
   *
   * @param s token
   */
  public AuthToken(String s) {

    if (null != s && s.startsWith(BEARER)) {
      value = parseHexBinary(s.replaceFirst(BEARER, ""));

    } else {
      throw new IllegalArgumentException(s);
    }
  }

  private static byte[] parseHexBinary(final String in) {

    if (0 != (in.length() % 2)) {
      throw new IllegalArgumentException();
    }

    final int length = in.length() / 2;
    final byte[] out = new byte[length];

    for (int off = 0; off < length; off++) {
      out[off] = (byte) Short.parseShort(in.substring(off * 2, off * 2 + 2), 16);
    }

    return out;
  }

  private static String printHexBinary(final byte[] in) {

    final StringBuilder builder = new StringBuilder();

    for (byte b : in) {
      builder.append(String.format("%02X", b));
    }

    return builder.toString();
  }

  public UUID getUuid() {
    ByteBuffer bb = ByteBuffer.wrap(getValue());
    return new UUID(bb.getLong(), bb.getLong());
  }

  public byte[] getValue() {
    return value;
  }

  @Override
  public String toString() {
    return BEARER + printHexBinary(getValue());
  }
}
