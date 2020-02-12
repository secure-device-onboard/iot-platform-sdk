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
