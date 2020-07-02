// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

@FunctionalInterface
public interface OwnerEventHandler {

  void call(OwnerEvent event);
}
