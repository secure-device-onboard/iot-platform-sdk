/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

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
