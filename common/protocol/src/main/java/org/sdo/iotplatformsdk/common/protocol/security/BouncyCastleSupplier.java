// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.security.Provider;
import java.util.function.Supplier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class BouncyCastleSupplier implements Supplier<Provider> {

  private static Provider provider;

  /**
   * Loads the Bouncy Castle JCE provider.
   *
   * <p>This method is idempotent, and may be called repeatedly without side effect.
   *
   * @return The Bouncy Castle JCE provider.
   */
  public static Provider load() {
    if (provider == null) {
      provider = new BouncyCastleProvider(); // dynamic registration requried
    }
    return provider;
  }

  @Override
  public Provider get() {
    return load();
  }
}
