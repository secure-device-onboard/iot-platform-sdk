// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.util.concurrent.Future;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;

/**
 * Provides PKI signature services for SDO.
 */
@FunctionalInterface
public interface SignatureService {
  Future<SignatureBlock> sign(final String data);
}
