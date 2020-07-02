// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.util.UUID;

/**
 * Provides factory services for building SignatureServices based on SDO UUID. (g2 or g3)
 */
@FunctionalInterface
public interface SignatureServiceFactory {
  SignatureService build(UUID... hints);
}
