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

package org.sdo.iotplatformsdk.ops.to2library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;

class Message255HandlerTest {

  SessionStorage sessions = Mockito.mock(SessionStorage.class);
  byte[] uuid = new byte[16];
  String expectedVoucher = "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":"
      + "\"localhost\",\"only\":\"owner\",\"pow\":8040,\"pr\":\"http\"}],[4,{\"dn\":"
      + "\"localhost\",\"only\":\"dev\",\"po\":8040,\"pr\":\"http\"}]],\"g\":"
      + "\"iavdTkTMT7uHZdjSoVhN8w==\",\"d\":\"cri device\",\"pk\":[1,3,[257,"
      + "\"AMy6OQz5CToc51e4ZvTOm3sWr6gKZ/3ED4q+lPiEHGKu8Cdsy2rfFOtDxZr+W22/GU"
      + "J2aK/5UkVZUdqsdLey77H0Hyyw9KcSOe92dej64rVLA+v6R4CIbyrGLpWY3nQ/nZX0pa"
      + "1esckj9PO+Xu0ONcxiNsOoVnLgShISBfviDhKGDG6OZMt4WYGOrVAFG63+cWoyXGHbzTI"
      + "cr1FVjq038nhQoP32nda+Nr211rfxPFhMr5++qD84rB5JQQLrkb7p65w1EEnLbs/m0Io3"
      + "bRCSudlFPU33GfBUj2SmgaiTrMCGoHRS6hc1hWM4BvCDW1Z6euNeGWsZPU9srbAYbpVdXi"
      + "0=\",3,\"AQAB\"]],\"hdc\":[32,8,\"RA2j4JJ4KRFic9U1vQhfCmejQ6L8RrVemJqj"
      + "n+dHzn0=\"]},\"hmac\":[32,108,\"NuoFP2IoULNsu5kJcRCG1nOY1YaLj3TilPS35/"
      + "6WNwI=\"],\"dc\":[1,1,[[472,\"MIIB1DCCAXqgAwIBAgIJANUEJ6s4GWDGMAoGCCqG"
      + "SM49BAMCMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBh"
      + "JbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTgxMTAzMTgyNTAzWhcNMjgxMDMxMTgyNT"
      + "AzWjBFMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZX"
      + "JuZXQgV2lkZ2l0cyBQdHkgTHRkMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEdaOhUH0ghj"
      + "4yWulJ552cK5NsLq2F/+6asq846qJnXYx5CGSAeISnyj+AzTgbu2OzjyohV07JXZc/9MYRLx"
      + "n+s6NTMFEwHQYDVR0OBBYEFF4V8TAj1WRCQTODzupoT3aL1X3WMB8GA1UdIwQYMBaAFF4V8T"
      + "Aj1WRCQTODzupoT3aL1X3WMA8GA1UdEwEB/wQFMAMBAf8wCgYIKoZIzj0EAwIDSAAwRQIhAK0"
      + "6vhlOjLLJw60otqeLD7OB5120pOydYlwc5KsHq7+yAiB4U1lL8DXKh8WD04KHXQe31PUxfCIi"
      + "ZW/HmEw9vVzugw==\"]]],\"en\":[{\"bo\":{\"hp\":[32,8,\"RfZ5kVQoEmNMUc1l2a"
      + "CO0GI8YBRL+9m1+u1p/6pNvqg=\"],\"hc\":[32,8,\"k77cB+VPf5oFTqmfKePGOwzsAAh2j"
      + "4r75q9t3EKGnLE=\"],\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEML"
      + "xdGPLl6N/xAnkv4Y+Ug5sRuX0pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJ"
      + "V//kXd8ASxJCkHza+5VPWYrX2hpASsbNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+"
      + "9srmnQa0Ljflx5GoKScBbS2K9K27UWX0i3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhW"
      + "aivfnIyZV7qAEuLh/quih4CwubtK3KS4g2CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/rVVqhbBmR"
      + "g3GV1NMwk=\",3,\"AQAB\"]]},\"pk\":[0,0,[0]],\"sg\":[256,\"Da81MXRiSV6TfHqzr"
      + "nZZ01SlEb2PmtygvAnq5sixwfsWAugQeIbd4qreE1nYRQbrHdf52jqsqS0PAzFMrsgi3gHht1SB"
      + "CORUFTbyUti/iAx8EGg+msW/n9+M5qjTT8vbh4VijIPBXN7EFnzWPJepsddcjZa82jLJAsRlfR5"
      + "eCayIfKJv1Gx8UCVv9kIA1DaM9aBpSmqjoUb99s7gEkeLsEwkpUSDT0hcnsJtZRf2kqtlr/suIr"
      + "dweQa7b/qDvS8+mnESeuWNHZXHhPWVwttCoAqB9tQqVakfRHSuihaUc+gyYmVTrp+OJ4TVf/U7I"
      + "U+7sL/k3n48zX+3dKHNEDZCpQ==\"]}]}";

  @BeforeEach
  void beforeEach() {
    final LongBuffer lbuf = ByteBuffer.wrap(uuid).asLongBuffer();
    lbuf.put(0xD10A59AC713347FCL);
    lbuf.put(0x9E457B33DB949F89L);
  }

  @Test
  void onPost_callsHandler() throws Exception {

    final LongBuffer lbuf = ByteBuffer.wrap(uuid).asLongBuffer();
    To2DeviceSessionInfo sessionInfo = new To2DeviceSessionInfo(
        new Message41Store("", "", expectedVoucher, null, null, null, null, null, null, null),
        new Message45Store(), new Message47Store(), new DeviceCryptoInfo());
    Mockito.when(sessions.load(Mockito.any())).thenReturn(sessionInfo);

    AtomicBoolean flag = new AtomicBoolean(false);
    Message255Handler h = new Message255Handler(sessions, event -> {
      assertTrue(event instanceof To2ErrorEvent);
      assertTrue(null != ((To2ErrorEvent) event).getError());
      flag.set(true);
    });
    h.onPost("Error from cient", DatatypeConverter.printHexBinary(uuid));
    assertTrue(flag.get()); // make sure the callback fired
  }

  @Test
  void onPost_parsesError() throws Exception {

    final LongBuffer lbuf = ByteBuffer.wrap(uuid).asLongBuffer();
    To2DeviceSessionInfo sessionInfo = new To2DeviceSessionInfo(
        new Message41Store("", "", expectedVoucher, null, null, null, null, null, null, null),
        new Message45Store(), new Message47Store(), new DeviceCryptoInfo());
    Mockito.when(sessions.load(Mockito.any())).thenReturn(sessionInfo);
    final SdoError expected = new SdoError(SdoErrorCode.InternalError, 255, "test");

    AtomicBoolean flag = new AtomicBoolean(false);
    Message255Handler h = new Message255Handler(sessions, event -> {
      assertTrue(event instanceof To2ErrorEvent);
      assertEquals(expected, ((To2ErrorEvent) event).getError());
      flag.set(true);
    });
    h.onPost(expected.toString(), DatatypeConverter.printHexBinary(uuid));
    assertTrue(flag.get()); // make sure the callback fired
  }
}
