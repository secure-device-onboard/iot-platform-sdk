// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.to0service;

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
import org.sdo.iotplatformsdk.to0scheduler.rest.RestClient;

@RunWith(JUnit4.class)
public class To0ProxyStoreImplTest extends TestCase {

  String expectedVoucher =
      "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":\"localhost\",\"only\":"
          + "\"owner\",\"pow\":8040,\"pr\":\"http\"}],[4,{\"dn\":\"localhost\",\"only\":\"dev\""
          + ",\"po\":8040,\"pr\":\"http\"}]],\"g\":\"iavdTkTMT7uHZdjSoVhN8w==\",\"d\":"
          + "\"cri device\",\"pk\":[1,3,[257,\"AMy6OQz5CToc51e4ZvTOm3sWr6gKZ/3ED4q+lPi"
          + "EHGKu8Cdsy2rfFOtDxZr+W22/GUJ2aK/5UkVZUdqsdLey77H0Hyyw9KcSOe92dej64rVLA+v6"
          + "R4CIbyrGLpWY3nQ/nZX0pa1esckj9PO+Xu0ONcxiNsOoVnLgShISBfviDhKGDG6OZMt4WYGOr"
          + "VAFG63+cWoyXGHbzTIcr1FVjq038nhQoP32nda+Nr211rfxPFhMr5++qD84rB5JQQLrkb7p65"
          + "w1EEnLbs/m0Io3bRCSudlFPU33GfBUj2SmgaiTrMCGoHRS6hc1hWM4BvCDW1Z6euNeGWsZPU9"
          + "srbAYbpVdXi0=\",3,\"AQAB\"]],\"hdc\":[32,8,\"RA2j4JJ4KRFic9U1vQhfCmejQ6L8"
          + "RrVemJqjn+dHzn0=\"]},\"hmac\":[32,108,\"NuoFP2IoULNsu5kJcRCG1nOY1YaLj3Til"
          + "PS35/6WNwI=\"],\"dc\":[1,1,[[472,\"MIIB1DCCAXqgAwIBAgIJANUEJ6s4GWDGMAoGCC"
          + "qGSM49BAMCMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBh"
          + "JbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTgxMTAzMTgyNTAzWhcNMjgxMDMxMTgyNTAz"
          + "WjBFMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZ"
          + "XQgV2lkZ2l0cyBQdHkgTHRkMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEdaOhUH0ghj4yWu"
          + "lJ552cK5NsLq2F/+6asq846qJnXYx5CGSAeISnyj+AzTgbu2OzjyohV07JXZc/9MYRLxn+s6N"
          + "TMFEwHQYDVR0OBBYEFF4V8TAj1WRCQTODzupoT3aL1X3WMB8GA1UdIwQYMBaAFF4V8TAj1WRC"
          + "QTODzupoT3aL1X3WMA8GA1UdEwEB/wQFMAMBAf8wCgYIKoZIzj0EAwIDSAAwRQIhAK06vhlOj"
          + "LLJw60otqeLD7OB5120pOydYlwc5KsHq7+yAiB4U1lL8DXKh8WD04KHXQe31PUxfCIiZW/HmE"
          + "w9vVzugw==\"]]],\"en\":[{\"bo\":{\"hp\":[32,8,\"RfZ5kVQoEmNMUc1l2aCO0GI8Y"
          + "BRL+9m1+u1p/6pNvqg=\"],\"hc\":[32,8,\"k77cB+VPf5oFTqmfKePGOwzsAAh2j4r75q9"
          + "t3EKGnLE=\"],\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEMLxdGPL"
          + "l6N/xAnkv4Y+Ug5sRuX0pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//"
          + "kXd8ASxJCkHza+5VPWYrX2hpASsbNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9s"
          + "rmnQa0Ljflx5GoKScBbS2K9K27UWX0i3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWa"
          + "ivfnIyZV7qAEuLh/quih4CwubtK3KS4g2CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/rVVqhbBm"
          + "Rg3GV1NMwk=\",3,\"AQAB\"]]},\"pk\":[0,0,[0]],\"sg\":[256,\"Da81MXRiSV6TfH"
          + "qzrnZZ01SlEb2PmtygvAnq5sixwfsWAugQeIbd4qreE1nYRQbrHdf52jqsqS0PAzFMrsgi3gH"
          + "ht1SBCORUFTbyUti/iAx8EGg+msW/n9+M5qjTT8vbh4VijIPBXN7EFnzWPJepsddcjZa82jLJ"
          + "AsRlfR5eCayIfKJv1Gx8UCVv9kIA1DaM9aBpSmqjoUb99s7gEkeLsEwkpUSDT0hcnsJtZRf2k"
          + "qtlr/suIrdweQa7b/qDvS8+mnESeuWNHZXHhPWVwttCoAqB9tQqVakfRHSuihaUc+gyYmVTrp"
          + "+OJ4TVf/U7IU+7sL/k3n48zX+3dKHNEDZCpQ==\"]}]}";
  To0ProxystoreImpl demoProxystore;

  @Mock
  RestClient restClient;

  @Override
  @Before
  public void setUp() {
    restClient = Mockito.mock(RestClient.class);
  }

  @Test
  public void testGetProxy() throws IOException {

    demoProxystore = new To0ProxystoreImpl(restClient);
    UUID uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");
    Mockito.when(restClient.getDeviceVoucher(uuid.toString())).thenReturn(expectedVoucher);
    OwnershipProxy actualProxy = demoProxystore.getProxy(uuid.toString());
    StringWriter writer = new StringWriter();
    new OwnershipProxyCodec.OwnershipProxyEncoder().encode(writer, actualProxy);
    String actualVoucher = writer.toString();
    Assert.assertEquals(expectedVoucher, actualVoucher);

    Mockito.when(restClient.getDeviceVoucher(uuid.toString())).thenReturn(null);
    actualProxy = demoProxystore.getProxy(uuid.toString());
    Assert.assertNull(actualProxy);

  }

  @Test
  public void testGetNoProxy() throws IOException {

    demoProxystore = new To0ProxystoreImpl(restClient);
    UUID uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");

    Mockito.when(restClient.getDeviceVoucher(uuid.toString())).thenReturn(null);
    OwnershipProxy actualProxy = demoProxystore.getProxy(uuid.toString());
    Assert.assertNull(actualProxy);

  }

  @Test(expected = IOException.class)
  public void testGetBadProxyField() throws IOException {

    demoProxystore = new To0ProxystoreImpl(restClient);
    UUID uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");
    expectedVoucher = expectedVoucher.replaceFirst("oh", "o");

    Mockito.when(restClient.getDeviceVoucher(uuid.toString())).thenReturn(expectedVoucher);
    demoProxystore.getProxy(uuid.toString());
  }
}
