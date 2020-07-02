// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.io.IOException;
import java.nio.CharBuffer;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.types.MessageType;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoError;
import org.sdo.iotplatformsdk.common.protocol.types.SdoErrorCode;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsOwnerEventHandler;
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.sdo.iotplatformsdk.ops.to2library.To2BeginEvent;
import org.sdo.iotplatformsdk.ops.to2library.To2EndEvent;
import org.sdo.iotplatformsdk.ops.to2library.To2ErrorEvent;

@RunWith(JUnit4.class)
public class OpsOwnerEventHandlerTest extends TestCase {

  @Mock
  RestClient restClient;
  @Mock
  SdoError sdoError;

  OwnershipProxy ownershipProxy;

  OpsOwnerEventHandler opsOwnerEventHandler;

  String expectedVoucher = "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":"
      + "\"localhost\",\"only\":\"owner\",\"pow\":8040,\"pr\":\"http\"}],[4,{\"dn\":"
      + "\"localhost\",\"only\":\"dev\",\"po\":8040,\"pr\":\"http\"}]],\"g\":\"iavdT"
      + "kTMT7uHZdjSoVhN8w==\",\"d\":\"cri device\",\"pk\":[1,3,[257,\"AMy6OQz5CToc5"
      + "1e4ZvTOm3sWr6gKZ/3ED4q+lPiEHGKu8Cdsy2rfFOtDxZr+W22/GUJ2aK/5UkVZUdqsdLey77H0"
      + "Hyyw9KcSOe92dej64rVLA+v6R4CIbyrGLpWY3nQ/nZX0pa1esckj9PO+Xu0ONcxiNsOoVnLgShI"
      + "SBfviDhKGDG6OZMt4WYGOrVAFG63+cWoyXGHbzTIcr1FVjq038nhQoP32nda+Nr211rfxPFhMr5"
      + "++qD84rB5JQQLrkb7p65w1EEnLbs/m0Io3bRCSudlFPU33GfBUj2SmgaiTrMCGoHRS6hc1hWM4B"
      + "vCDW1Z6euNeGWsZPU9srbAYbpVdXi0=\",3,\"AQAB\"]],\"hdc\":[32,8,\"RA2j4JJ4KRFi"
      + "c9U1vQhfCmejQ6L8RrVemJqjn+dHzn0=\"]},\"hmac\":[32,108,\"NuoFP2IoULNsu5kJcRC"
      + "G1nOY1YaLj3TilPS35/6WNwI=\"],\"dc\":[1,1,[[472,\"MIIB1DCCAXqgAwIBAgIJANUEJ6"
      + "s4GWDGMAoGCCqGSM49BAMCMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwH"
      + "wYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTgxMTAzMTgyNTAzWhcNMjgxMDMx"
      + "MTgyNTAzWjBFMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW5"
      + "0ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEdaOhUH0ghj"
      + "4yWulJ552cK5NsLq2F/+6asq846qJnXYx5CGSAeISnyj+AzTgbu2OzjyohV07JXZc/9MYRLxn+s"
      + "6NTMFEwHQYDVR0OBBYEFF4V8TAj1WRCQTODzupoT3aL1X3WMB8GA1UdIwQYMBaAFF4V8TAj1WRC"
      + "QTODzupoT3aL1X3WMA8GA1UdEwEB/wQFMAMBAf8wCgYIKoZIzj0EAwIDSAAwRQIhAK06vhlOjLL"
      + "Jw60otqeLD7OB5120pOydYlwc5KsHq7+yAiB4U1lL8DXKh8WD04KHXQe31PUxfCIiZW/HmEw9vV"
      + "zugw==\"]]],\"en\":[{\"bo\":{\"hp\":[32,8,\"RfZ5kVQoEmNMUc1l2aCO0GI8YBRL+9m"
      + "1+u1p/6pNvqg=\"],\"hc\":[32,8,\"k77cB+VPf5oFTqmfKePGOwzsAAh2j4r75q9t3EKGnLE"
      + "=\"],\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEMLxdGPLl6N/xAnkv4"
      + "Y+Ug5sRuX0pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//kXd8ASxJCkHz"
      + "a+5VPWYrX2hpASsbNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9srmnQa0Ljflx5Go"
      + "KScBbS2K9K27UWX0i3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWaivfnIyZV7qAEuLh/"
      + "quih4CwubtK3KS4g2CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/rVVqhbBmRg3GV1NMwk=\",3,"
      + "\"AQAB\"]]},\"pk\":[0,0,[0]],\"sg\":[256,\"Da81MXRiSV6TfHqzrnZZ01SlEb2Pmtyg"
      + "vAnq5sixwfsWAugQeIbd4qreE1nYRQbrHdf52jqsqS0PAzFMrsgi3gHht1SBCORUFTbyUti/iAx"
      + "8EGg+msW/n9+M5qjTT8vbh4VijIPBXN7EFnzWPJepsddcjZa82jLJAsRlfR5eCayIfKJv1Gx8UC"
      + "Vv9kIA1DaM9aBpSmqjoUb99s7gEkeLsEwkpUSDT0hcnsJtZRf2kqtlr/suIrdweQa7b/qDvS8+m"
      + "nESeuWNHZXHhPWVwttCoAqB9tQqVakfRHSuihaUc+gyYmVTrp+OJ4TVf/U7IU+7sL/k3n48zX+3"
      + "dKHNEDZCpQ==\"]}]}";

  @Override
  @Before
  public void setUp() throws IOException {
    restClient = Mockito.mock(RestClient.class);
    sdoError = Mockito.mock(SdoError.class);
    ownershipProxy =
        new OwnershipProxyCodec.OwnershipProxyDecoder().decode(CharBuffer.wrap(expectedVoucher));

    opsOwnerEventHandler = new OpsOwnerEventHandler(restClient);

    Mockito.when(sdoError.getEm()).thenReturn("Error message");
    Mockito.when(sdoError.getEmsg()).thenReturn(255);
    Mockito.when(sdoError.getType()).thenReturn(MessageType.ERROR);
    Mockito.when(sdoError.getEc()).thenReturn(SdoErrorCode.InternalError);
    Mockito.doNothing().when(restClient).postDeviceState(Mockito.anyString(),
        Mockito.any(DeviceState.class));
  }

  @Test
  public void testTo2BeginEvent() {
    To2BeginEvent event = new To2BeginEvent(ownershipProxy);
    opsOwnerEventHandler.call(event);
  }

  @Test
  public void testTo2EndEvent() {
    To2EndEvent event = new To2EndEvent(ownershipProxy, Mockito.mock(OwnershipProxy.class));
    opsOwnerEventHandler.call(event);
  }

  @Test
  public void testTo2ErrorEvent() {
    To2ErrorEvent event = new To2ErrorEvent(sdoError, ownershipProxy);
    opsOwnerEventHandler.call(event);
  }
}
