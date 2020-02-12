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

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.codecs.Codec;
import org.sdo.iotplatformsdk.common.protocol.codecs.To2OpNextEntryCodec;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To2OpNextEntry;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.to2library.Message42Handler;
import org.sdo.iotplatformsdk.ops.to2library.SessionStorage;
import org.sdo.iotplatformsdk.ops.to2library.To2ProvingOwner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

class Message42HandlerTest {

  byte[] uuid;
  int enn;
  Codec<SignatureBlock> codecSignatureBlock;
  Codec<SignatureBlock>.Encoder codecSignatureBlockEncoder;
  Codec<Number> codecNumber;
  Codec<Number>.Encoder codecNumberEncoder;
  List<SignatureBlock> listSignatureBlock;
  SessionStorage sessions;
  Message42Handler message42Handler;
  // OwnershipProxy ownershipProxy;
  RequestEntity<String> requestEntity;
  SignatureBlock signatureBlock;
  String message42;
  To2OpNextEntry to2OpNextEntry;
  To2OpNextEntryCodec to2OpNextEntryCodec;
  To2ProvingOwner to2ProvingOwner;
  To2DeviceSessionInfo to2deviceSessionInfo;
  Writer writer;
  String ownershipProxy;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() throws IOException {

    codecNumber = Mockito.mock(Codec.class);
    codecNumberEncoder = Mockito.mock(Codec.Encoder.class);
    codecSignatureBlock = Mockito.mock(Codec.class);
    codecSignatureBlockEncoder = Mockito.mock(Codec.Encoder.class);
    enn = 0;
    listSignatureBlock = Mockito.mock(List.class);
    ownershipProxy = "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":"
        + "\"localhost\",\"only\":\"owner\",\"pow\":8040,\"pr\":\"http\"}],[4,"
        + "{\"dn\":\"localhost\",\"only\":\"dev\",\"po\":8040,\"pr\":\"http\"}]],"
        + "\"g\":\"ZlByr34/RBu5GUPG2VI+Hw==\",\"d\":\"cri device\",\"pk\":[1,3,"
        + "[257,\"AMy6OQz5CToc51e4ZvTOm3sWr6gKZ/3ED4q+lPiEHGKu8Cdsy2rfFOtDxZr+W22/GU"
        + "J2aK/5UkVZUdqsdLey77H0Hyyw9KcSOe92dej64rVLA+v6R4CIbyrGLpWY3nQ/nZX0pa1esckj"
        + "9PO+Xu0ONcxiNsOoVnLgShISBfviDhKGDG6OZMt4WYGOrVAFG63+cWoyXGHbzTIcr1FVjq038n"
        + "hQoP32nda+Nr211rfxPFhMr5++qD84rB5JQQLrkb7p65w1EEnLbs/m0Io3bRCSudlFPU33GfBUj"
        + "2SmgaiTrMCGoHRS6hc1hWM4BvCDW1Z6euNeGWsZPU9srbAYbpVdXi0=\",3,\"AQAB\"]],"
        + "\"hdc\":[32,8,\"RA2j4JJ4KRFic9U1vQhfCmejQ6L8RrVemJqjn+dHzn0=\"]},"
        + "\"hmac\":[32,108,\"sFFeZvQts7ctm5F8HrTKO/+N25L3b7QoYiZ3BTM5egY=\"],"
        + "\"dc\":[1,1,[[472,\"MIIB1DCCAXqgAwIBAgIJANUEJ6s4GWDGMAoGCCqGSM49BAMCMEUxC"
        + "zAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBXaWR"
        + "naXRzIFB0eSBMdGQwHhcNMTgxMTAzMTgyNTAzWhcNMjgxMDMxMTgyNTAzWjBFMQswCQYDVQQGE"
        + "wJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHk"
        + "gTHRkMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEdaOhUH0ghj4yWulJ552cK5NsLq2F/+6as"
        + "q846qJnXYx5CGSAeISnyj+AzTgbu2OzjyohV07JXZc/9MYRLxn+s6NTMFEwHQYDVR0OBBYEFF4"
        + "V8TAj1WRCQTODzupoT3aL1X3WMB8GA1UdIwQYMBaAFF4V8TAj1WRCQTODzupoT3aL1X3WMA8GA"
        + "1UdEwEB/wQFMAMBAf8wCgYIKoZIzj0EAwIDSAAwRQIhAK06vhlOjLLJw60otqeLD7OB5120pOy"
        + "dYlwc5KsHq7+yAiB4U1lL8DXKh8WD04KHXQe31PUxfCIiZW/HmEw9vVzugw==\"]]],\"en\":"
        + "[{\"bo\":{\"hp\":[32,8,\"NzVrJCcJvgsvGngeQZy0R/wnfXk2i0GWl5u6E5rY7nI=\"],"
        + "\"hc\":[32,8,\"SbYd4LqKAmqFGcffO6+2mWGkDnZ0XFQyopR4wKTj3KQ=\"],\"pk\":[1,3,"
        + "[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEMLxdGPLl6N/xAnkv4Y+Ug5sRuX0pwZZhlS"
        + "icBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//kXd8ASxJCkHza+5VPWYrX2hpASsbN"
        + "t2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9srmnQa0Ljflx5GoKScBbS2K9K27UWX0i"
        + "3Er6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWaivfnIyZV7qAEuLh/quih4CwubtK3KS4g2"
        + "CYIr+erOKcHeCC0rkRkqIQmLCCCJPaw/rVVqhbBmRg3GV1NMwk=\",3,\"AQAB\"]]},\"pk\":"
        + "[0,0,[0]],\"sg\":[256,\"sYgpHdH1kol4WotR5vCRUkZ8bhfnVF5bYtxUWzBXBS5GZQeoC94"
        + "ZLAH6MXnCRvVR5/k6nu2c8Ez9TzJndhOlE3s3pq5dz2igj7G5Oewowh3P7ZD0DjJnwALdrEq9ij"
        + "A8DfKmJxp0eXd/xLepQkiOdxxJNQMLSvcgSVsZXAFyybl9xKybMB4YvL4Kefz+UQ5R8ml03SY51"
        + "uZBCgd8h0+G/phMxwaYergj211+yFJmfuLY75imdfm1JO75Vb40q+U8meyHiDWa/fZuxQcK+Wgf"
        + "fG1SWoprQ5lDjdnwTtPMBkAnAzBDdIYnApKdFaRj0bcaG8sD7sQp3HlulmrUjaZRJw==\"]}]}";
    sessions = Mockito.mock(SessionStorage.class);
    signatureBlock = Mockito.mock(SignatureBlock.class);
    to2OpNextEntry = Mockito.mock(To2OpNextEntry.class);
    to2OpNextEntryCodec = new To2OpNextEntryCodec();
    // to2ProvingOwner = Mockito.mock(To2ProvingOwner.class);
    Message41Store message41Store = new Message41Store();
    message41Store.setOwnershipProxy(ownershipProxy);
    to2deviceSessionInfo = new To2DeviceSessionInfo();
    to2deviceSessionInfo.setMessage41Store(message41Store);
    uuid = new byte[16];
    writer = Mockito.mock(Writer.class);

    Mockito.when(sessions.load(Mockito.any(UUID.class))).thenReturn(to2deviceSessionInfo);
    Mockito.when(listSignatureBlock.get(Mockito.anyInt())).thenReturn(signatureBlock);
    Mockito.when(codecSignatureBlock.encoder()).thenReturn(codecSignatureBlockEncoder);
    Mockito.when(to2OpNextEntry.getEni()).thenReturn(signatureBlock);
    Mockito.when(signatureBlock.getSg()).thenReturn(ByteBuffer.allocate(256));
    Mockito.when(codecNumber.encoder()).thenReturn(codecNumberEncoder);

    message42Handler = new Message42Handler();
    message42Handler.setSessionStorage(sessions);

  }

  @Test
  void test_Message42() throws Exception {
    message42 = "{\"enn\":" + enn + "}";
    requestEntity =
        RequestEntity.post(URI.create("http://localhost")).header("accept", "text/plain, */*")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + DatatypeConverter.printHexBinary(uuid))
            .header("user-agent", "Java/11.0.3").header("host", "localhost:8042")
            .header("connection", "keep-alive").header("content-length", "9")
            .contentType(MediaType.APPLICATION_JSON_UTF8).body(message42);
    Callable<ResponseEntity<?>> message43 = message42Handler.onPostAsync(requestEntity);
    message43.call();
  }

  @Test
  void test_Message42_BadRequest() {
    message42 = "{\"enn\":" + "notAnInt" + "}";
    requestEntity =
        RequestEntity.post(URI.create("http://localhost")).header("accept", "text/plain, */*")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + DatatypeConverter.printHexBinary(uuid))
            .header("user-agent", "Java/11.0.3").header("host", "localhost:8042")
            .header("connection", "keep-alive").header("content-length", "9")
            .contentType(MediaType.APPLICATION_JSON_UTF8).body(message42);
    Callable<ResponseEntity<?>> message43 = message42Handler.onPostAsync(requestEntity);
    Assertions.assertThrows(IOException.class, () -> {
      message43.call();
    });

  }

}
