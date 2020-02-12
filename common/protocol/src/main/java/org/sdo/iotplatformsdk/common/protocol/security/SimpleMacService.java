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
