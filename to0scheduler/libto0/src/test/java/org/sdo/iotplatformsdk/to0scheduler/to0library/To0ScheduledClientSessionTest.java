// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.to0library;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInstr;

public class To0ScheduledClientSessionTest {

  To0ScheduledClientSession to0ScheduledClientSession;

  @Mock
  OwnershipProxy proxy;
  @Mock
  OwnershipProxyHeader proxyHeader;
  @Mock
  RendezvousInfo rendezvousInfo;
  @Mock
  RendezvousInstr rendezvousInstr;
  Iterator<RendezvousInstr> itr;

  @Mock
  To0ClientSession to0ClientSession;
  @Mock
  To0SchedulerEvents eventHandler;

  String expectedVoucher = "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,"
      + "{\"dn\":\"localhost\",\"only\":\"owner\",\"pow\":8040,\"pr\":\"http\""
      + "}],[4,{\"dn\":\"localhost\",\"only\":\"dev\",\"po\":8040,\"pr\":\"htt"
      + "p\"}]],\"g\":\"iavdTkTMT7uHZdjSoVhN8w==\",\"d\":\"cri device\",\"pk\""
      + ":[1,3,[257,\"AMy6OQz5CToc51e4ZvTOm3sWr6gKZ/3ED4q+lPiEHGKu8Cdsy2rfFOtD"
      + "xZr+W22/GUJ2aK/5UkVZUdqsdLey77H0Hyyw9KcSOe92dej64rVLA+v6R4CIbyrGLpWY3"
      + "nQ/nZX0pa1esckj9PO+Xu0ONcxiNsOoVnLgShISBfviDhKGDG6OZMt4WYGOrVAFG63+cW"
      + "oyXGHbzTIcr1FVjq038nhQoP32nda+Nr211rfxPFhMr5++qD84rB5JQQLrkb7p65w1EEn"
      + "Lbs/m0Io3bRCSudlFPU33GfBUj2SmgaiTrMCGoHRS6hc1hWM4BvCDW1Z6euNeGWsZPU9s"
      + "rbAYbpVdXi0=\",3,\"AQAB\"]],\"hdc\":[32,8,\"RA2j4JJ4KRFic9U1vQhfCmejQ"
      + "6L8RrVemJqjn+dHzn0=\"]},\"hmac\":[32,108,\"NuoFP2IoULNsu5kJcRCG1nOY1Y"
      + "aLj3TilPS35/6WNwI=\"],\"dc\":[1,1,[[472,\"MIIB1DCCAXqgAwIBAgIJANUEJ6s"
      + "4GWDGMAoGCCqGSM49BAMCMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRl"
      + "MSEwHwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTgxMTAzMTgyNTAzW"
      + "hcNMjgxMDMxMTgyNTAzWjBFMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZT"
      + "EhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMFkwEwYHKoZIzj0CAQYIKoZ"
      + "Izj0DAQcDQgAEdaOhUH0ghj4yWulJ552cK5NsLq2F/+6asq846qJnXYx5CGSAeISnyj+A"
      + "zTgbu2OzjyohV07JXZc/9MYRLxn+s6NTMFEwHQYDVR0OBBYEFF4V8TAj1WRCQTODzupoT"
      + "3aL1X3WMB8GA1UdIwQYMBaAFF4V8TAj1WRCQTODzupoT3aL1X3WMA8GA1UdEwEB/wQFMA"
      + "MBAf8wCgYIKoZIzj0EAwIDSAAwRQIhAK06vhlOjLLJw60otqeLD7OB5120pOydYlwc5Ks"
      + "Hq7+yAiB4U1lL8DXKh8WD04KHXQe31PUxfCIiZW/HmEw9vVzugw==\"]]],\"en\":[{"
      + "\"bo\":{\"hp\":[32,8,\"RfZ5kVQoEmNMUc1l2aCO0GI8YBRL+9m1+u1p/6pNvqg=\""
      + "],\"hc\":[32,8,\"k77cB+VPf5oFTqmfKePGOwzsAAh2j4r75q9t3EKGnLE=\"],"
      + "\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEMLxdGPLl6N/xAnkv"
      + "4Y+Ug5sRuX0pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//kXd8A"
      + "SxJCkHza+5VPWYrX2hpASsbNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9sr"
      + "mnQa0Ljflx5GoKScBbS2K9K27UWX0i3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdh"
      + "hWaivfnIyZV7qAEuLh/quih4CwubtK3KS4g2CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/r"
      + "VVqhbBmRg3GV1NMwk=\",3,\"AQAB\"]]},\"pk\":[0,0,[0]],\"sg\":[256,\"Da8"
      + "1MXRiSV6TfHqzrnZZ01SlEb2PmtygvAnq5sixwfsWAugQeIbd4qreE1nYRQbrHdf52jqs"
      + "qS0PAzFMrsgi3gHht1SBCORUFTbyUti/iAx8EGg+msW/n9+M5qjTT8vbh4VijIPBXN7EF"
      + "nzWPJepsddcjZa82jLJAsRlfR5eCayIfKJv1Gx8UCVv9kIA1DaM9aBpSmqjoUb99s7gEk"
      + "eLsEwkpUSDT0hcnsJtZRf2kqtlr/suIrdweQa7b/qDvS8+mnESeuWNHZXHhPWVwttCoAq"
      + "B9tQqVakfRHSuihaUc+gyYmVTrp+OJ4TVf/U7IU+7sL/k3n48zX+3dKHNEDZCpQ==\"]}]}";

  /**
   * Setup method.
   */
  @BeforeEach
  public void setUp() {
    proxy = Mockito.mock(OwnershipProxy.class);
    proxyHeader = Mockito.mock(OwnershipProxyHeader.class);
    rendezvousInfo = Mockito.mock(RendezvousInfo.class);
    rendezvousInstr = Mockito.mock(RendezvousInstr.class);
    itr = Mockito.mock(Iterator.class);
    to0ClientSession = Mockito.mock(To0ClientSession.class);
    eventHandler = Mockito.mock(To0SchedulerEvents.class);
    to0ScheduledClientSession =
        new To0ScheduledClientSession(proxy, to0ClientSession, eventHandler);

    Mockito.when(proxy.getOh()).thenReturn(proxyHeader);
    Mockito.when(proxyHeader.getR()).thenReturn(rendezvousInfo);
    Mockito.when(rendezvousInfo.iterator()).thenReturn(itr);
    Mockito.when(itr.hasNext()).thenReturn(true, false);
    Mockito.when(itr.next()).thenReturn(rendezvousInstr);
    Mockito.doCallRealMethod().when(rendezvousInfo).forEach(Mockito.any(Consumer.class));
  }

  @Test
  public void testCallNoRendezvousInfo() {
    To0ClientSession actualTo0ClientSession = to0ScheduledClientSession.call();
    Assertions.assertEquals(to0ClientSession, actualTo0ClientSession);
  }

  @Test()
  public void testCallNoProxy() {
    to0ScheduledClientSession = new To0ScheduledClientSession(null, to0ClientSession, eventHandler);
    To0ClientSession actualTo0ClientSession = to0ScheduledClientSession.call();
    Assertions.assertEquals(to0ClientSession, actualTo0ClientSession);
  }

  @Test
  public void testCallWithRendezvousInfo()
      throws IOException, ExecutionException, InterruptedException {
    OwnershipProxy ownershipProxy =
        new OwnershipProxyCodec.OwnershipProxyDecoder().decode(CharBuffer.wrap(expectedVoucher));
    to0ScheduledClientSession =
        new To0ScheduledClientSession(ownershipProxy, to0ClientSession, eventHandler);
    To0ClientSession actualTo0ClientSession = to0ScheduledClientSession.call();
    Assertions.assertEquals(to0ClientSession, actualTo0ClientSession);

    actualTo0ClientSession = to0ScheduledClientSession.call();
    Assertions.assertEquals(to0ClientSession, actualTo0ClientSession);
  }
}
