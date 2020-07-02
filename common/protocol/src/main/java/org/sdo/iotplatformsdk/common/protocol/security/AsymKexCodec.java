// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * An interface for cipher operations when we don't have access to the keys.
 */
public interface AsymKexCodec {
  ByteBuffer buildEncipher(final ByteBuffer plainText, final UUID uuid);

  ByteBuffer buildDecipher(final ByteBuffer cipherText, final UUID uuid);
}
