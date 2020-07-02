// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.util;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

/**
 * An AutoCloseable wrapper for Destroyable objects.
 */
public class Destroyer<T extends Destroyable> implements AutoCloseable {

  private final T destroyable;

  public Destroyer(T destroyable) {
    this.destroyable = destroyable;
  }

  @Override
  public void close() {

    if (!(null == destroyable || destroyable.isDestroyed())) {
      try {
        destroyable.destroy();

      } catch (DestroyFailedException e) {
        // many destroyables (PrivateKey, I'm looking at you) don't implement this interface
        // so this exception is expected more often than not.
        //
        // There's nothing to be done, and even if we log it at debug level, it causes FUD.
        // Squelch it.
        ;
      }
    }
  }

  public T get() {
    return destroyable;
  }
}
