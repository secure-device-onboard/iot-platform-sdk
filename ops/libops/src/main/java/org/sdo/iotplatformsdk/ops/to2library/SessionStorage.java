// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.time.Duration;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;

public interface SessionStorage {

  To2DeviceSessionInfo load(Object key) throws IOException;

  void store(Object key, To2DeviceSessionInfo value) throws IOException;

  void store(Object key, To2DeviceSessionInfo value, Duration timeout) throws IOException;

  void remove(Object key);
}
