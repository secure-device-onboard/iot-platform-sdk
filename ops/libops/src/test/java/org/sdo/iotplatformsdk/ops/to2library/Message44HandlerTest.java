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

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.config.EpidOptionBean;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfoEntry;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.serviceinfo.PreServiceInfoMultiSource;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMultiSource;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoSource;
import org.sdo.iotplatformsdk.ops.to2library.KeyExchangeDecoder;
import org.sdo.iotplatformsdk.ops.to2library.Message44Handler;
import org.sdo.iotplatformsdk.ops.to2library.SessionStorage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

class Message44HandlerTest {

  AsymKexCodec asymKexCodec;
  byte[] uuid;
  EpidOptionBean epidOptions;
  DeviceCryptoInfo deviceCryptoInfo;
  KeyExchangeDecoder keyExchangeDecoder;
  Message44Handler message44Handler;
  Message41Store message41Store;
  Message45Store message45Store;
  Message47Store message47Store;
  RequestEntity<String> requestEntity;
  RequestEntity<String> badequestEntity;
  SecureRandom secureRandom;
  SessionStorage sessionStorage;
  Set<ServiceInfoModule> serviceInfoModules;
  String message44;
  To2DeviceSessionInfo to2DeviceSessionInfo;

  @BeforeEach
  void beforeEach() {

    secureRandom = new SecureRandom();
    asymKexCodec = Mockito.mock(AsymKexCodec.class);
    deviceCryptoInfo = new DeviceCryptoInfo();
    epidOptions = new EpidOptionBean();
    keyExchangeDecoder = new KeyExchangeDecoder(asymKexCodec, secureRandom);
    message44Handler = new Message44Handler();
    message41Store = new Message41Store("\"UOpomJjQ2KeXQJO44aGLYg==\"", "\"ECDH\"",
        "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":\"localhost\","
            + "\"only\":\"owner\",\"pow\":8040,\"pr\":\"http\"}],[4,{\"dn\":\"localhost\""
            + ",\"only\":\"dev\",\"po\":8040,\"pr\":\"http\"}]],\"g\":\"iavdTkTMT7uHZdjSoVhN8w==\""
            + ",\"d\":\"cri device\",\"pk\":[1,3,[257,\"AMy6OQz5CToc51e4ZvTOm3sWr6gKZ/3ED4q+lPiEH"
            + "GKu8Cdsy2rfFOtDxZr+W22/GUJ2aK/5UkVZUdqsdLey77H0Hyyw9KcSOe92dej64rVLA+v6R4CIbyrGLpW"
            + "Y3nQ/nZX0pa1esckj9PO+Xu0ONcxiNsOoVnLgShISBfviDhKGDG6OZMt4WYGOrVAFG63+cWoyXGHbzTIcr"
            + "1FVjq038nhQoP32nda+Nr211rfxPFhMr5++qD84rB5JQQLrkb7p65w1EEnLbs/m0Io3bRCSudlFPU33GfB"
            + "Uj2SmgaiTrMCGoHRS6hc1hWM4BvCDW1Z6euNeGWsZPU9srbAYbpVdXi0=\",3,\"AQAB\"]],\"hdc\":["
            + "32,8,\"RA2j4JJ4KRFic9U1vQhfCmejQ6L8RrVemJqjn+dHzn0=\"]},\"hmac\":[32,108,\"NuoFP2I"
            + "oULNsu5kJcRCG1nOY1YaLj3TilPS35/6WNwI=\"],\"dc\":[1,1,[[472,\"MIIB1DCCAXqgAwIBAgIJA"
            + "NUEJ6s4GWDGMAoGCCqGSM49BAMCMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwY"
            + "DVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTgxMTAzMTgyNTAzWhcNMjgxMDMxMTgyNTAzW"
            + "jBFMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l"
            + "0cyBQdHkgTHRkMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEdaOhUH0ghj4yWulJ552cK5NsLq2F/+6as"
            + "q846qJnXYx5CGSAeISnyj+AzTgbu2OzjyohV07JXZc/9MYRLxn+s6NTMFEwHQYDVR0OBBYEFF4V8TAj1WR"
            + "CQTODzupoT3aL1X3WMB8GA1UdIwQYMBaAFF4V8TAj1WRCQTODzupoT3aL1X3WMA8GA1UdEwEB/wQFMAMBA"
            + "f8wCgYIKoZIzj0EAwIDSAAwRQIhAK06vhlOjLLJw60otqeLD7OB5120pOydYlwc5KsHq7+yAiB4U1lL8DX"
            + "Kh8WD04KHXQe31PUxfCIiZW/HmEw9vVzugw==\"]]],\"en\":[{\"bo\":{\"hp\":[32,8,\"RfZ5kVQ"
            + "oEmNMUc1l2aCO0GI8YBRL+9m1+u1p/6pNvqg=\"],"
            + "\"hc\":[32,8,\"k77cB+VPf5oFTqmfKePGOwzsAAh2j4r75q9t3EKGnLE=\"],"
            + "\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEMLxdGPLl6N/xAnkv4Y+Ug5sRu"
            + "X0pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//kXd8ASxJCkHza+5VPWYrX2h"
            + "pASsbNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9srmnQa0Ljflx5GoKScBbS2K9K27UW"
            + "X0i3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWaivfnIyZV7qAEuLh/quih4CwubtK3KS4g2"
            + "CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/rVVqhbBmRg3GV1NMwk=\",3,\"AQAB\"]]},\"pk\":[0,"
            + "0,[0]],\"sg\":[256,\"Da81MXRiSV6TfHqzrnZZ01SlEb2PmtygvAnq5sixwfsWAugQeIbd4qreE"
            + "1nYRQbrHdf52jqsqS0PAzFMrsgi3gHht1SBCORUFTbyUti/iAx8EGg+msW/n9+M5qjTT8vbh4VijIP"
            + "BXN7EFnzWPJepsddcjZa82jLJAsRlfR5eCayIfKJv1Gx8UCVv9kIA1DaM9aBpSmqjoUb99s7gEkeLs"
            + "EwkpUSDT0hcnsJtZRf2kqtlr/suIrdweQa7b/qDvS8+mnESeuWNHZXHhPWVwttCoAqB9tQqVakfRHS"
            + "uihaUc+gyYmVTrp+OJ4TVf/U7IU+7sL/k3n48zX+3dKHNEDZCpQ==\"]}]}",
        "\"AES128/CTR/HMAC-SHA256\"",
        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEiWqCheRU9WLiB3WbWuGZrUUGD74d2bCg7M1QocIIVS2wPoCdr"
            + "JPiFe1xFnM1YHuQgmRVdfk+/PH67ZPeSAo2/A==",
        "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgsbKgIdYSgH9JEpyiIBc6MRqFokao6xymm0q2b"
            + "ep8wNigCgYIKoZIzj0DAQehRANCAASJaoKF5FT1YuIHdZta4ZmtRQYPvh3ZsKDszVChwghVLbA+gJ2sk+IV"
            + "7XEWczVge5CCZFV1+T788frtk95ICjb8",
        "9zmY7i6i0Mv5isrJYiqaRw==", null, null, null);
    message45Store = new Message45Store();
    message47Store = new Message47Store();
    serviceInfoModules = new HashSet<ServiceInfoModule>();
    sessionStorage = Mockito.mock(SessionStorage.class);
    to2DeviceSessionInfo = new To2DeviceSessionInfo();
    uuid = new byte[16];

    keyExchangeDecoder.getKeyExchangeType(KeyExchangeType.ECDH, null);
    to2DeviceSessionInfo.setMessage41Store(message41Store);
    to2DeviceSessionInfo.setMessage45Store(message45Store);
    to2DeviceSessionInfo.setMessage47Store(message47Store);
    to2DeviceSessionInfo.setDeviceCryptoInfo(deviceCryptoInfo);

    message44Handler.setEpidOptions(epidOptions);
    message44Handler.setSecureRandom(secureRandom);
    message44Handler.setServiceInfoModules(serviceInfoModules);
    message44Handler.setSessionStorage(sessionStorage);
    message44Handler.setKeyExchangeDecoder(keyExchangeDecoder);
  }

  @Test
  void buildPreServiceInfo_multipleSources_filtersServiceInfo() {

    UUID id = UUID.fromString("0e1fd06e-447c-44cd-be54-572d2ee4657c");
    List<ServiceInfoModule> modules = new LinkedList<>();

    String key = "k";
    String valS = "a";
    String valM = "b";
    String valP = "c";

    class TestModule
        implements ServiceInfoSource, ServiceInfoMultiSource, PreServiceInfoMultiSource {

      @Override
      public List<PreServiceInfoEntry> getPreServiceInfo(UUID id) {
        return Arrays.asList(new PreServiceInfoEntry(key, valP));
      }

      @Override
      public List<ServiceInfoEntry> getServiceInfo(UUID id) {
        return Arrays.asList(new ServiceInfoEntry(key, valM));
      }

      @Override
      public List<ServiceInfoEntry> getServiceInfo() {
        return Arrays.asList(new ServiceInfoEntry(key, valS));
      }
    }

    modules.add(new TestModule());

    PreServiceInfo preServiceInfo = Message44Handler.buildPreServiceInfo(id, modules);
    assertEquals(1, preServiceInfo.size());
    assertEquals(valP, preServiceInfo.get(0).getValue());
  }

  @Test
  void test_Message44() throws Exception {

    message44 = "{\"bo\":{\"ai\":[0,0,\"\"],\"n6\":\"UOpomJjQ2KeXQJO44aGLYg==\","
        + "\"n7\":\"GYaV0lGJpZxbQ0DMlYOqKA==\",\"g2\":\"iavdTkTMT7uHZdjS"
        + "oVhN8w==\",\"nn\":1,\"xB\":[86,\"ACAdArDxkfqqb5lkS9O1A1RsABXN"
        + "gMRMxPXB9C0dzOP3UQAgFA8l+H+MPGNnVQ5ZSOV94FckW4VX6AO5o1sN+7suu2"
        + "YAEBfl7G7cTRlDYQV5qLMjsRA=\"]},\"pk\":[0,0,[0]],\"sg\":[72,\"M"
        + "EYCIQDgPU/UAQho8pe1C14OC5fljzpAiqC9sIHOsgSzGqF8awIhAJX4GgAU3rO"
        + "ZAH+y6ZraggY3KSYfRVtmAyJiujrHJx5W\"]}";

    requestEntity =
        RequestEntity.post(URI.create("http://localhost")).header("accept", "text/plain, */*")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + DatatypeConverter.printHexBinary(uuid))
            .header("user-agent", "Java/11.0.3").header("host", "localhost:8042")
            .header("connection", "keep-alive").header("content-length", "9")
            .contentType(MediaType.APPLICATION_JSON_UTF8).body(message44);
    Mockito.when(sessionStorage.load(Mockito.any(UUID.class))).thenReturn(to2DeviceSessionInfo);

    Callable<ResponseEntity<?>> message45 = message44Handler.onPostAsync(requestEntity);
    message45.call();
    message44Handler.getSecureRandom();
  }

  @Test
  void test_Message44_BadRequests() throws GeneralSecurityException, IOException {

    message44 = "{\"bo\":{\"a\":[0,0,\"\"],\"n6\":\"UOpomJjQ2KeXQJO44aGLYg==\",\"n7\":"
        + "\"GYaV0lGJpZxbQ0DMlYOqKA==\",\"g2\":\"iavdTkTMT7uHZdjSoVhN8w==\","
        + "\"nn\":1,\"xB\":[86,\"ACAdArDxkfqqb5lkS9O1A1RsABXNgMRMxPXB9C0dzOP"
        + "3UQAgFA8l+H+MPGNnVQ5ZSOV94FckW4VX6AO5o1sN+7suu2YAEBfl7G7cTRlDYQV5"
        + "qLMjsRA=\"]},\"pk\":[0,0,[0]],\"sg\":[72,\"MEYCIQDgPU/UAQho8pe1C1"
        + "4OC5fljzpAiqC9sIHOsgSzGqF8awIhAJX4GgAU3rOZAH+y6ZraggY3KSYfRVtmAyJ" + "iujrHJx5W\"]}";

    requestEntity =
        RequestEntity.post(URI.create("http://localhost")).header("accept", "text/plain, */*")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + DatatypeConverter.printHexBinary(uuid))
            .header("user-agent", "Java/11.0.3").header("host", "localhost:8042")
            .header("connection", "keep-alive").header("content-length", "9")
            .contentType(MediaType.APPLICATION_JSON_UTF8).body(message44);
    Callable<ResponseEntity<?>> message45 = message44Handler.onPostAsync(requestEntity);
    Assertions.assertThrows(IOException.class, () -> {
      message45.call();
    });
  }

}
