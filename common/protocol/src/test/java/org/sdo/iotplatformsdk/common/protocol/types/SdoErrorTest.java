// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;

class SdoErrorTest {

  SdoErrorCode ec;
  MessageType emsg;
  SdoError sdoError;
  String em;

  @BeforeEach
  void beforeEach() {

    ec = SdoErrorCode.InvalidGuid;
    emsg = MessageType.DI_SET_CREDENTIALS;
    em = "Test";
    sdoError = new SdoError(ec, emsg, em);
  }

  @Test
  void test_Bean() {

    assertEquals(ec, sdoError.getEc());
    assertEquals(em, sdoError.getEm());
    assertEquals(Integer.valueOf(11), sdoError.getEmsg());
    sdoError.getType();
    sdoError.getVersion();
    sdoError.toString();
    sdoError.equals(sdoError);
    sdoError.equals(null);
    sdoError.equals(new SdoError(ec, emsg, em));

  }

}
