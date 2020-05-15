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

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.cipher.To2CipherContext;
import org.sdo.iotplatformsdk.common.protocol.types.CipherText;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.To2CipherHashMac;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;

class Message48HandlerTest {

  AsymKexCodec asymKexCodec;
  byte[] uuid;
  CipherText cipherText;
  EncryptedMessage encryptedMessage;
  DeviceCryptoInfo deviceCryptoInfo;
  To2CipherHashMac hashMac;
  KeyExchangeDecoder keyExchangeDecoder;
  Message41Store message41Store;
  Message45Store message45Store;
  Message47Store message47Store;
  Message48Handler message48Handler;
  String requestEntity;
  SecureRandom secureRandom;
  Set<ServiceInfoModule> serviceInfoModules;
  SessionStorage sessionStorage;
  String message48;
  To2CipherContext deviceCipherContext;
  To2CipherContext ownerCipherContext;
  To2DeviceSessionInfo to2DeviceSessionInfo;

  @BeforeEach
  void beforeEach() {

    secureRandom = new SecureRandom();
    asymKexCodec = Mockito.mock(AsymKexCodec.class);
    cipherText = Mockito.mock(CipherText.class);
    deviceCryptoInfo = new DeviceCryptoInfo();
    deviceCipherContext = Mockito.mock(To2CipherContext.class);
    hashMac = Mockito.mock(To2CipherHashMac.class);
    encryptedMessage = Mockito.mock(EncryptedMessage.class);
    keyExchangeDecoder = new KeyExchangeDecoder(asymKexCodec, secureRandom);
    message47Store = new Message47Store();
    ownerCipherContext = Mockito.mock(To2CipherContext.class);
    serviceInfoModules = new HashSet<ServiceInfoModule>();
    sessionStorage = Mockito.mock(SessionStorage.class);
    to2DeviceSessionInfo = new To2DeviceSessionInfo();
    uuid = new byte[16];

    message41Store = new Message41Store("\"UOpomJjQ2KeXQJO44aGLYg==\"", "\"ECDH\"",
        "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":\"localhost\",\"only\""
            + ":\"owner\",\"pow\":8040,\"pr\":\"http\"}],[4,{\"dn\":\"localhost\",\"only\":"
            + "\"dev\",\"po\":8040,\"pr\":\"http\"}]],\"g\":\"iavdTkTMT7uHZdjSoVhN8w==\","
            + "\"d\":\"cri device\",\"pk\":[1,3,[257,\"AMy6OQz5CToc51e4ZvTOm3sWr6gKZ/3ED4"
            + "q+lPiEHGKu8Cdsy2rfFOtDxZr+W22/GUJ2aK/5UkVZUdqsdLey77H0Hyyw9KcSOe92dej64rVL"
            + "A+v6R4CIbyrGLpWY3nQ/nZX0pa1esckj9PO+Xu0ONcxiNsOoVnLgShISBfviDhKGDG6OZMt4WY"
            + "GOrVAFG63+cWoyXGHbzTIcr1FVjq038nhQoP32nda+Nr211rfxPFhMr5++qD84rB5JQQLrkb7p"
            + "65w1EEnLbs/m0Io3bRCSudlFPU33GfBUj2SmgaiTrMCGoHRS6hc1hWM4BvCDW1Z6euNeGWsZPU"
            + "9srbAYbpVdXi0=\",3,\"AQAB\"]],\"hdc\":[32,8,\"RA2j4JJ4KRFic9U1vQhfCmejQ6L8"
            + "RrVemJqjn+dHzn0=\"]},\"hmac\":[32,108,\"NuoFP2IoULNsu5kJcRCG1nOY1YaLj3TilP"
            + "S35/6WNwI=\"],\"dc\":[1,1,[[472,\"MIIB1DCCAXqgAwIBAgIJANUEJ6s4GWDGMAoGCCqG"
            + "SM49BAMCMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbn"
            + "Rlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTgxMTAzMTgyNTAzWhcNMjgxMDMxMTgyNTAzWjBF"
            + "MQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2"
            + "lkZ2l0cyBQdHkgTHRkMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEdaOhUH0ghj4yWulJ552c"
            + "K5NsLq2F/+6asq846qJnXYx5CGSAeISnyj+AzTgbu2OzjyohV07JXZc/9MYRLxn+s6NTMFEwHQ"
            + "YDVR0OBBYEFF4V8TAj1WRCQTODzupoT3aL1X3WMB8GA1UdIwQYMBaAFF4V8TAj1WRCQTODzupo"
            + "T3aL1X3WMA8GA1UdEwEB/wQFMAMBAf8wCgYIKoZIzj0EAwIDSAAwRQIhAK06vhlOjLLJw60otq"
            + "eLD7OB5120pOydYlwc5KsHq7+yAiB4U1lL8DXKh8WD04KHXQe31PUxfCIiZW/HmEw9vVzugw=="
            + "\"]]],\"en\":[{\"bo\":{\"hp\":[32,8,\"RfZ5kVQoEmNMUc1l2aCO0GI8YBRL+9m1+u1p"
            + "/6pNvqg=\"]," + "\"hc\":[32,8,\"k77cB+VPf5oFTqmfKePGOwzsAAh2j4r75q9t3EKGnLE=\"],"
            + "\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEMLxdGPLl6N/xAnkv4"
            + "Y+Ug5sRuX0pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//kXd8ASx"
            + "JCkHza+5VPWYrX2hpASsbNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9srmnQ"
            + "a0Ljflx5GoKScBbS2K9K27UWX0i3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWai"
            + "vfnIyZV7qAEuLh/quih4CwubtK3KS4g2CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/rVVqhb"
            + "BmRg3GV1NMwk=\",3,\"AQAB\"]]},\"pk\":[0,0,[0]],\"sg\":[256,\"Da81MXRiS"
            + "V6TfHqzrnZZ01SlEb2PmtygvAnq5sixwfsWAugQeIbd4qreE1nYRQbrHdf52jqsqS0PAzF"
            + "Mrsgi3gHht1SBCORUFTbyUti/iAx8EGg+msW/n9+M5qjTT8vbh4VijIPBXN7EFnzWPJeps"
            + "ddcjZa82jLJAsRlfR5eCayIfKJv1Gx8UCVv9kIA1DaM9aBpSmqjoUb99s7gEkeLsEwkpUS"
            + "DT0hcnsJtZRf2kqtlr/suIrdweQa7b/qDvS8+mnESeuWNHZXHhPWVwttCoAqB9tQqVakfR"
            + "HSuihaUc+gyYmVTrp+OJ4TVf/U7IU+7sL/k3n48zX+3dKHNEDZCpQ==\"]}]}",
        "\"AES128/CTR/HMAC-SHA256\"",
        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEiWqCheRU9WLiB3WbWuGZrUUGD74d2bCg7M1QocII"
            + "VS2wPoCdrJPiFe1xFnM1YHuQgmRVdfk+/PH67ZPeSAo2/A==",
        "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgsbKgIdYSgH9JEpyiIBc6MRqFokao"
            + "6xymm0q2bep8wNigCgYIKoZIzj0DAQehRANCAASJaoKF5FT1YuIHdZta4ZmtRQYPvh3ZsKDszV"
            + "ChwghVLbA+gJ2sk+IV7XEWczVge5CCZFV1+T788frtk95ICjb8",
        "9zmY7i6i0Mv5isrJYiqaRw==", null, null, null);

    message45Store = new Message45Store("\"GYaV0lGJpZxbQ0DMlYOqKA==\"", 1,
        "[86,\"ACAdArDxkfqqb5lkS9O1A1RsABXNgMRMxPXB9C0dzOP3UQAgFA8l+H+MPGNnVQ5ZSOV94F"
            + "ckW4VX6AO5o1sN+7suu2YAEBfl7G7cTRlDYQV5qLMjsRA=\"]");

    message48Handler =
        new Message48Handler(sessionStorage, secureRandom, keyExchangeDecoder, serviceInfoModules);
  }

  // @Test
  void test_Message48() throws Exception {

    to2DeviceSessionInfo.setDeviceCryptoInfo(deviceCryptoInfo);
    to2DeviceSessionInfo.setMessage41Store(message41Store);
    to2DeviceSessionInfo.setMessage45Store(message45Store);
    to2DeviceSessionInfo.setMessage47Store(message47Store);

    Mockito.when(sessionStorage.load(Mockito.any(UUID.class))).thenReturn(to2DeviceSessionInfo);
    Mockito.when(deviceCipherContext.read(Mockito.any(EncryptedMessage.class)))
        .thenReturn(ByteBuffer.wrap("{\"nn\":0}".getBytes()));
    Mockito.when(encryptedMessage.getCt()).thenReturn(cipherText);
    Mockito.when(cipherText.getCt()).thenReturn(ByteBuffer.allocate(8));
    Mockito.when(encryptedMessage.getHmac()).thenReturn(hashMac);
    Mockito.when(ownerCipherContext.write(Mockito.any())).thenReturn(encryptedMessage);

    String message49 = message48Handler.onPost(message48, Mockito.anyString());
  }

  @Test
  void test_Message48_BadRequest() throws Exception {

    Mockito.when(sessionStorage.load(Mockito.any(UUID.class))).thenReturn(to2DeviceSessionInfo);

    Assertions.assertThrows(SdoProtocolException.class, () -> {
      String message49 = message48Handler.onPost(message48, UUID.randomUUID().toString());
    });
  }
}
