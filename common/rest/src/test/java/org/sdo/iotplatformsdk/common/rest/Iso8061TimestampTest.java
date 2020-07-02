// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.rest;

import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.rest.Iso8061Timestamp;

class Iso8061TimestampTest {

  @Test
  void test_Bean() {

    Iso8061Timestamp.now();
    Iso8061Timestamp.nowPlusSeconds(100);
    Iso8061Timestamp.fromString("2017-01-13T17:09:42.411");
    Iso8061Timestamp.instantPlusSeconds("2017-01-13T17:09:42.411", 100);
  }

}

