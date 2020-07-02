// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
