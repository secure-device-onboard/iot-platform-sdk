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

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.io.IOException;
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
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.sdo.iotplatformsdk.common.rest.SviMessage;
import org.springframework.web.client.RestTemplate;

/**
 * This class only tests the url building loggeric, and doesn't make any real
 * calls. In case of any exception, the tests will fail.
 */
@RunWith(JUnit4.class)
public class RestClientTest extends TestCase {

  @Mock
  RestTemplate restTemplate;
  @Mock
  RestUri restUri;
  @Mock
  SignatureResponse signatureResponse;

  RestClient restClient;

  UUID uuid;

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
  String bo = "{\"hp\":[32,8,\"JX/b4lBGbSfvyzus36TZKNCyBLvl2cmIi6Ip7pjAJRk=\"],\"hc\":[32,8,\"iZTb"
      + "0IfWSSf66BmEn2/pMd/kg82O5eHmmWj+CGjn3Uw=\"],\"pk\":[13,1,[91,\"MFkwEwYHKoZIzj0CAQ"
      + "YIKoZIzj0DAQcDQgAEiJB5hPxpIied36DgwgjtaBTlfFUhQNxEupm7U1QGdaAYERwTnvOuk2Vi9Gllrri"
      + "36aZQ58VhxwtNxy8WZZwWrA==\"]]}";

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
    restUri = Mockito.mock(RestUri.class);
    signatureResponse = Mockito.mock(SignatureResponse.class);
    uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");

    restClient = new RestClient();
    restClient.setRestTemplate(restTemplate);
    restClient.setRestUri(restUri);
    restClient.setApiServer("http://localhost:9009/");

    Mockito.when(restTemplate.getForObject(
        "http://localhost:9009/v1/devices/89abdd4e-44cc-4fbb-8765-d8d2a1584df3/voucher",
        String.class)).thenReturn(expectedVoucher);
    Mockito.when(restTemplate.postForObject(
        "http://localhost:9009/v1/signatures/89abdd4e-44cc-4fbb-8765-d8d2a1584df3", bo,
        SignatureResponse.class)).thenReturn(signatureResponse);
    Mockito.when(restUri.getVoucherUrl_()).thenReturn("/v1/devices/{deviceId}/voucher");
    Mockito.when(restUri.getDevStateUrl_()).thenReturn("/v1/devices/{deviceId}/state");
    Mockito.when(restUri.getSignatureUrl_()).thenReturn("/v1/signatures/{deviceId}");

  }

  @Test
  public void testGetDeviceVoucher() {
    String actualVoucher = restClient.getDeviceVoucher(uuid.toString());
    Assert.assertEquals(expectedVoucher, actualVoucher);

    Mockito.when(restTemplate.getForObject(
        "http://localhost:9009/v1/devices/89abdd4e-44cc-4fbb-8765-d8d2a1584df3/voucher",
        String.class)).thenThrow(new RuntimeException());
    actualVoucher = restClient.getDeviceVoucher(uuid.toString());
    Assert.assertNull(actualVoucher);
  }

  @Test
  public void testPostDeviceState() {
    // Void return method.
    restClient.postDeviceState(uuid.toString(), new DeviceState());
  }

  @Test
  public void testPostError() {
    // Void return method.
    restClient.postError(uuid.toString(), new DeviceState());
  }

  @Test
  public void testSignatureOperation() {
    SignatureResponse actualResponse = restClient.signatureOperation(uuid, bo);
    Assert.assertEquals(signatureResponse, actualResponse);

    Mockito.when(restTemplate.postForObject(
        "http://localhost:9009/v1/signatures/89abdd4e-44cc-4fbb-8765-d8d2a1584df3", bo,
        SignatureResponse.class)).thenThrow(new RuntimeException());
    actualResponse = restClient.signatureOperation(uuid, bo);
    Assert.assertNull(actualResponse);
  }
}
