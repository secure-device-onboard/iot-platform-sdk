// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;

class SdoUriComponentBuilderTest {

  SdoUriComponentsBuilder sdoUriComponentsBuilder;
  URI uri;

  @Test
  void test_UriComponentBuilder() throws IOException {
    String path = SdoUriComponentsBuilder.path(MessageType.TO2_DONE.intValue());
    assertEquals("/mp/113/msg/50", path);
  }
}
