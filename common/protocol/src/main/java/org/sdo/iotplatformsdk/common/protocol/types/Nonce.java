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

package org.sdo.iotplatformsdk.common.protocol.types;

import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Nonce {

  private static final short BYTES = 16;
  private static final short CHARS = 24; // ceil(16 * 4 / 3), rounded up to block
  private static final String QUOTE = "\"";

  private final byte[] bytes;

  /**
   * Constructor with {@link SecureRandom} as argument.
   *
   * @param random random
   */
  public Nonce(final SecureRandom random) {
    byte[] b = new byte[BYTES];
    random.nextBytes(b);
    this.bytes = b;
  }

  /**
   * Constructor with {@link CharBuffer} as argument.
   *
   * @param cbuf buffer
   */
  public Nonce(final CharBuffer cbuf) {

    // String will be CHARS b64 chars plus two quotes
    final char[] c = new char[CHARS + 2];
    cbuf.get(c);
    final String s = new String(c);

    // String must begin and end with quotes
    if (!(s.startsWith(QUOTE) || s.endsWith(QUOTE))) {
      throw new IllegalArgumentException("not a quoted-string: " + s);
    }

    this.bytes = Base64.getDecoder().decode(s.substring(1, s.length() - 1));
  }

  public byte[] getBytes() {
    return Arrays.copyOf(bytes, bytes.length);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bytes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Nonce nonce = (Nonce) o;
    return Arrays.equals(bytes, nonce.bytes);
  }

  @Override
  public String toString() {
    return QUOTE + Base64.getEncoder().encodeToString(bytes) + QUOTE;
  }
}
