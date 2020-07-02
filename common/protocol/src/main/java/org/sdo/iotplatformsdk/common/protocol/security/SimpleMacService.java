// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

/**
 * A simple implementation of {@link MacService}.
 */
class SimpleMacService implements MacService {

  // NIO reads and writes use this buffer. Bigger for fewer ops at higher memory cost.
  private static final int IO_BUFSIZ = 256;

  private final MacType macType;

  SimpleMacService(final MacType macType) {
    this.macType = Objects.requireNonNull(macType);
  }

  @Override
  public HashMac macOf(final byte[] key, final ReadableByteChannel... ins) {

    final ByteBuffer buf = ByteBuffer.allocate(IO_BUFSIZ);
    final Mac mac;

    try {
      final String jceAlgo = macType.getJceName();
      mac = Mac.getInstance(jceAlgo);
      mac.init(new SecretKeySpec(key, jceAlgo));

    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    try {
      for (final ReadableByteChannel in : ins) {
        while (-1 < in.read(buf)) {
          buf.flip();
          mac.update(buf);
          buf.flip();
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new HashMac(macType, ByteBuffer.wrap(mac.doFinal()));
  }

  @Override
  public HashMac macOf(final byte[] key, final ByteBuffer... ins) {
    final Mac mac;
    try {
      final String jceAlgo = macType.getJceName();
      mac = Mac.getInstance(jceAlgo);
      mac.init(new SecretKeySpec(key, jceAlgo));

    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    for (final ByteBuffer in : ins) {
      mac.update(in);
    }

    return new HashMac(macType, ByteBuffer.wrap(mac.doFinal()));
  }
}
