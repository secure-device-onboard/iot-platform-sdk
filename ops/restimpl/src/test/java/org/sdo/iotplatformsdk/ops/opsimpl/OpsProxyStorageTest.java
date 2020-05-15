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

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.ops.rest.RestClient;

@RunWith(JUnit4.class)
public class OpsProxyStorageTest extends TestCase {

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
  OpsProxyStorage proxyStorage;

  @Mock
  RestClient restClient;

  @Override
  @Before
  public void setUp() {
    restClient = Mockito.mock(RestClient.class);
  }

  @Test
  public void testLoad() throws IOException {

    proxyStorage = new OpsProxyStorage(restClient);
    UUID uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");
    Mockito.when(restClient.getDeviceVoucher(uuid)).thenReturn(expectedVoucher);
    OwnershipProxy actualProxy = proxyStorage.load(uuid);
    StringWriter writer = new StringWriter();
    new OwnershipProxyCodec.OwnershipProxyEncoder().encode(writer, actualProxy);
    String actualVoucher = writer.toString();
    Assert.assertEquals(expectedVoucher, actualVoucher);

    Mockito.when(restClient.getDeviceVoucher(uuid)).thenReturn(null);
    actualProxy = proxyStorage.load(uuid);
    Assert.assertNull(actualProxy);

  }

  @Test
  public void testLoadNoProxy() throws IOException {

    proxyStorage = new OpsProxyStorage(restClient);
    UUID uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");

    Mockito.when(restClient.getDeviceVoucher(uuid)).thenReturn(null);
    OwnershipProxy actualProxy = proxyStorage.load(uuid);
    Assert.assertNull(actualProxy);

  }

  @Test(expected = IOException.class)
  public void testLoadBadProxyField() throws IOException {

    proxyStorage = new OpsProxyStorage(restClient);
    UUID uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");
    expectedVoucher = expectedVoucher.replaceFirst("oh", "o");

    Mockito.when(restClient.getDeviceVoucher(uuid)).thenReturn(expectedVoucher);
    proxyStorage.load(uuid);
  }
}
