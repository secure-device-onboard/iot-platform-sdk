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
import java.nio.CharBuffer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.function.Predicate;

/**
 * Common text-matching utilities.
 */
public class Matchers {

  /**
   * Assert that the next character inputs match the expected string.
   *
   * @param in the input text
   * @param match the string which we expect the input text to match
   * @throws IOException if the text doesn't match the target
   */
  public static void expect(CharBuffer in, final String match) throws IOException {

    StringCharacterIterator iter = new StringCharacterIterator(match);
    for (char c = iter.first(); CharacterIterator.DONE != c; c = iter.next()) {
      expect(in, c);
    }
  }

  /**
   * Assert that the next character input matches the expected value.
   *
   * @param in the input text
   * @param match the character which we expect the input text to match
   * @throws IOException if the text doesn't match the target
   */
  public static void expect(CharBuffer in, final Character match) throws IOException {

    expect(in, match::equals);
  }

  /**
   * Assert that the next character input satisfies the supplied predicate.
   *
   * @param in the input text
   * @param predicate the character test which will determine if we have a match
   * @throws IOException if the text doesn't match the target
   */
  public static void expect(CharBuffer in, Predicate<Character> predicate) throws IOException {

    char c = in.get();

    if (!predicate.test(c)) {
      throw new IOException("unexpected input: " + c);
    }
  }
}
