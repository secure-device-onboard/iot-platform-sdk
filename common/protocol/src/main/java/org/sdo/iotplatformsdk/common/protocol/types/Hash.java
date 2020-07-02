// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.nio.ByteBuffer;

/**
 * SDO type "Hash".
 *
 * <p>@see "SDO Protocol Specification, 1.13b, 3.2: Composite Types"
 */
public interface Hash<T> {

  ByteBuffer getHash();

  T getType();
}
