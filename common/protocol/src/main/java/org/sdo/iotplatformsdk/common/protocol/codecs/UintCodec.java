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

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.Writer;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;

/**
 * Provides serialization for SDO 'Uint' types.
 *
 * <p>SDO places signing, format, and width constraint on integer types.
 */
public class UintCodec extends Codec<Number> {

  private static final int RADIX = 10;

  private int width;

  public UintCodec(int width) {
    this.width = width;
  }

  @Override
  public Codec<Number>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<Number>.Encoder encoder() {
    return new Encoder();
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  class Decoder extends Codec<Number>.Decoder {

    @Override
    public Number apply(CharBuffer in) throws IOException {
      long value = 0L;

      final long max = (1L << getWidth()) - 1;

      // unsigned decimal only, no '-', no valid prefixes
      for (int i = getDigit(in); 0 <= i; i = getDigit(in)) {
        value = (value * RADIX) + i;

        if (max < value) {
          throw new NumberFormatException("Uint out of bounds");
        }
      }

      return value;
    }

    private int getDigit(CharBuffer in) {

      int digit;

      try {
        in.mark();
        char c = in.get();
        digit = Character.digit(c, RADIX);

        if (digit < 0) {
          // not a digit, unread the character (usual case is normal end-of-number)
          in.reset();
        }

      } catch (BufferUnderflowException e) {
        // This is OK, it's legal to have a number be the last thing in the buffer
        digit = -1;
      }

      return digit;
    }
  }

  class Encoder extends Codec<Number>.Encoder {

    @Override
    public void apply(Writer writer, Number value) throws IOException {

      long val = value.longValue();

      if (val < 0 || (1L << getWidth()) <= val) {
        throw new NumberFormatException("Uint out of bounds");
      }
      writer.write(Long.toString(val, RADIX));
    }
  }
}
