// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Uuids {

  public static UUID buildRandomUuid() {
    return buildRandomUuid(ThreadLocalRandom.current());
  }

  /**
   * Rules for random UUIDs (from RFC4122):
   *
   * <p>1. Set the two most significant bits (bits 6 and 7) of the clock_seq_hi_and_reserved to zero
   * and one, respectively.
   *
   * <p>2. Set the four most significant bits (bits 12 through 15) of the time_hi_and_version
   * field to the 4-bit version number from Section 4.1.3.
   *
   * <p>3. Set all the other bits to randomly (or pseudo-randomly) chosen // values.
   *
   * @param random {@link Random}
   * @return
   */
  public static UUID buildRandomUuid(Random random) {

    byte[] bytes = new byte[Long.BYTES * 2];

    LongBuffer longs = ByteBuffer.wrap(bytes).asLongBuffer();
    while (longs.hasRemaining()) {
      longs.put(random.nextLong());
    }
    longs.flip();

    // clock_seq_hi_and_reserved is octet 8
    final int clock_seq_hi_res = 8;
    bytes[clock_seq_hi_res] = (byte) (bytes[clock_seq_hi_res] | 0x80); // set bit 7
    bytes[clock_seq_hi_res] = (byte) (bytes[clock_seq_hi_res] & 0xbf); // clear bit 6

    // time_hi_and_version is octets 6-7
    final int time_hi_and_version = 6;

    // clear the top nibble...
    bytes[time_hi_and_version] = (byte) (bytes[time_hi_and_version] & 0x0f);

    // ...then put 4 (UUID v4, 'random or pseudorandom' in it.
    bytes[time_hi_and_version] = (byte) (bytes[time_hi_and_version] | 0x40);

    return new UUID(longs.get(), longs.get());
  }
}
