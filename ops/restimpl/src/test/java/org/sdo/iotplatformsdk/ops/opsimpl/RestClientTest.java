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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.MessageEncoding;
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;
import org.sdo.iotplatformsdk.common.rest.SviMessage;
import org.sdo.iotplatformsdk.common.rest.SviMessageType;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsRestUri;
import org.sdo.iotplatformsdk.ops.opsimpl.RestClient;
import org.springframework.web.client.RestTemplate;

/**
 * This class only tests the url building loggeric, and doesn't make any real calls. In case of any
 * exception, the tests will fail.
 */
@RunWith(JUnit4.class)
public class RestClientTest extends TestCase {

  @Mock
  RestTemplate restTemplate;
  @Mock
  OpsRestUri opsRestUri;

  RestClient restClient;

  UUID uuid;

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
  SviMessage[] sviMessages;
  ModuleMessage[] expectedModuleMessages;
  String message = "#!/bin/bash\r\n" + "filename=payload.bin\r\n" + "cksum_tx=1612472339\r\n"
      + "cksum_rx=$(cksum $filename | cut -d ' ' -f 1)\r\n"
      + "if [ $cksum_tx -eq $cksum_rx  ]; then\r\n"
      + "  echo \"Device onboarded successfully.\"\r\n"
      + "  echo \"Device onboarded successfully.\" > result.txt\r\n" + "else\r\n"
      + "  echo \"ServiceInfo file transmission failed.\"\r\n"
      + "  echo \"ServiceInfo file transmission failed.\" > result.txt\r\n" + "fi";
  byte[] expectedBytes = message.getBytes();

  @Override
  @Before
  public void setUp() throws IOException {
    restTemplate = Mockito.mock(RestTemplate.class);
    opsRestUri = Mockito.mock(OpsRestUri.class);
    uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");

    restClient = new RestClient();
    restClient.setRestTemplate(restTemplate);
    restClient.setOpsRestUri(opsRestUri);
    restClient.setApiServer("http://localhost:9009/");

    sviMessages = new SviMessage[1];
    SviMessage message = new SviMessage();
    message.setEnc(MessageEncoding.ASCII.toString());
    message.setModule(SviMessageType.FILEDESC.toString());
    message.setMsg("fileDesc");
    message.setValueId("fileDescId");
    message.setValueLen(0);
    sviMessages[0] = message;

    expectedModuleMessages = new ModuleMessage[1];
    ModuleMessage moduleMessage = new ModuleMessage();
    moduleMessage.setModule(SviMessageType.MODULE.toString());
    moduleMessage.setMsg("active");
    moduleMessage.setValue("1");
    expectedModuleMessages[0] = moduleMessage;

    Mockito.when(restTemplate.getForObject(
        "http://localhost:9009/v1/devices/89abdd4e-44cc-4fbb-8765-d8d2a1584df3/voucher",
        String.class)).thenReturn(expectedVoucher);
    Mockito.when(opsRestUri.getVoucherUrl()).thenReturn("/v1/devices/{deviceId}/voucher");
    Mockito.when(opsRestUri.getDevStateUrl()).thenReturn("/v1/devices/{deviceId}/state");
    Mockito.when(opsRestUri.getServiceInfoUrl()).thenReturn("/v1/devices/{deviceId}/msgs");
    Mockito.when(opsRestUri.getPsiUrl()).thenReturn("v1/devices/{deviceId}/psi");
    Mockito.when(opsRestUri.getServiceInfoValueUrl())
        .thenReturn("v1/devices/{deviceId}/values/{valueId}");

    Mockito.when(restTemplate.getForObject(
        "http://localhost:9009/v1/devices/89abdd4e-44cc-4fbb-8765-d8d2a1584df3/msgs",
        SviMessage[].class)).thenReturn(sviMessages);
    Mockito.when(restTemplate.getForObject(
        "http://localhost:9009/v1/devices/89abdd4e-44cc-4fbb-8765-d8d2a1584df3/psi",
        ModuleMessage[].class)).thenReturn(expectedModuleMessages);
    Mockito.when(restTemplate.getForObject(
        "http://localhost:9009/v1/devices/89abdd4e-44cc-4fbb-8765-d8d2a1584df3/values/opaqueId?start=0&end=230",
        byte[].class)).thenReturn(expectedBytes);
  }

  @Test
  public void testGetDeviceVoucher() {
    String actualVoucher = restClient.getDeviceVoucher(uuid);
    Assert.assertEquals(expectedVoucher, actualVoucher);
  }

  @Test
  public void testPostDeviceState() {
    // Void return method.
    restClient.postDeviceState(uuid.toString(), new DeviceState());
  }

  @Test
  public void testGetMessage() {
    SviMessage[] messages = restClient.getMessage(uuid.toString());
    Assert.assertArrayEquals(sviMessages, messages);
  }

  @Test
  public void testPostMessage() {
    // Void return method.
    List<ModuleMessage> moduleMessages = new ArrayList<ModuleMessage>();
    restClient.postMessage(uuid.toString(), moduleMessages);
  }

  @Test
  public void testGetPsi() {
    ModuleMessage[] actualModuleMessages = restClient.getPsi(uuid.toString());
    Assert.assertArrayEquals(expectedModuleMessages, actualModuleMessages);
  }

  @Test
  public void testGetValue() {
    byte[] actualBytes = restClient.getValue(uuid, "opaqueId", 0, 230);
    Assert.assertEquals(expectedBytes, actualBytes);
  }

  @Test
  public void testPostError() {
    // Void return method.
    restClient.postError(uuid.toString(), new DeviceState());
  }
}
