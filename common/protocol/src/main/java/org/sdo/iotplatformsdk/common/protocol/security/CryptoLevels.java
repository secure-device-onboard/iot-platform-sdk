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

import java.util.Optional;
import java.util.Set;

import org.sdo.iotplatformsdk.common.protocol.types.MacType;

/**
 * General utility functions for working with CryptoLevel objects.
 */
public abstract class CryptoLevels {

  /**
   * Return a set of {@link CryptoLevel}.
   *
   * @return The set of all known crypto levels.
   */
  public static Set<CryptoLevel> all() {
    return Set.of(new CryptoLevel112(), new CryptoLevel11());
  }

  /**
   * Find the CryptoLevel which supports the given MacType.
   *
   * @param type the MacType to search for.
   * @return the CryptoLevel which can support it, if any.
   */
  public static Optional<CryptoLevel> find(final MacType type) {
    for (final CryptoLevel cl : all()) {
      if (cl.hasType(type)) {
        return Optional.of(cl);
      }
    }

    return Optional.empty();
  }
}
