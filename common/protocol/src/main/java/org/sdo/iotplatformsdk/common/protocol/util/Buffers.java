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

package org.sdo.iotplatformsdk.common.protocol.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class Buffers {

  /**
   * Creates a deep copy (clone) of a ByteBuffer.
   *
   * @param original The original ByteBuffer.
   * @return The new clone.
   */
  public static ByteBuffer clone(final ByteBuffer original) {

    ByteBuffer copy = original.asReadOnlyBuffer(); // don't change the original buffer
    final ByteBuffer clone;

    if (original.isDirect()) {
      clone = ByteBuffer.allocateDirect(original.capacity());

    } else {
      clone = ByteBuffer.allocate(original.capacity());
    }

    clone.position(original.position());
    clone.put(copy);
    clone.position(original.position());
    clone.limit(original.limit());
    clone.order(original.order());
    return clone;
  }

  /**
   * Creates a deep copy (clone) of a CharBuffer.
   *
   * @param original The original CharBuffer.
   * @return The new clone.
   */
  public static CharBuffer clone(final CharBuffer original) {

    CharBuffer copy = original.asReadOnlyBuffer(); // don't change the original buffer
    final CharBuffer clone = CharBuffer.allocate(original.capacity());

    clone.position(original.position());
    clone.put(copy);
    clone.position(original.position());
    clone.limit(original.limit());
    return clone;
  }

  /**
   * Copies the contents of a ByteBuffer to a raw byte array.
   *
   * @param buffer The input buffer.
   * @return The unwrapped array.
   */
  public static byte[] unwrap(ByteBuffer buffer) {

    byte[] unwrapped = new byte[buffer.remaining()];
    buffer.get(unwrapped);
    return unwrapped;
  }

  public static class Eraser implements AutoCloseable {

    private final ByteBuffer buf;

    public Eraser(ByteBuffer buf) {
      this.buf = buf;
    }

    @Override
    public void close() {

      buf.clear();

      while (buf.hasRemaining()) {
        buf.put((byte) 0);
      }
      buf.clear();
    }

    public ByteBuffer getBuf() {
      return buf.duplicate();
    }
  }
}
