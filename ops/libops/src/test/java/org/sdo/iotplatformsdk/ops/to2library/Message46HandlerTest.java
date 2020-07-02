// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.to2library;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureService;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
import org.sdo.iotplatformsdk.ops.to2library.SetupDeviceService.Setup;

class Message46HandlerTest {

  AsymKexCodec asymKexCodec;
  ByteBuffer sg;
  byte[] uuid;
  String badresponseEntity;
  String responseEntity;
  DeviceCryptoInfo deviceCryptoInfo;
  Future<SignatureBlock> futureSignatureBlock;
  KeyExchangeDecoder keyExchangeDecoder;
  Message46Handler message46Handler;
  Message41Store message41Store;
  Message45Store message45Store;
  Message47Store message47Store;
  RendezvousInfo r3;
  SignatureBlock signatureBlock;
  SignatureService signatureService;
  Setup setup;
  Set<ServiceInfoModule> serviceInfoModules;
  SecureRandom secureRandom;
  SessionStorage sessionStorage;
  SetupDeviceService setupDeviceservice;
  SignatureServiceFactory signatureServiceFactory;
  String bo;
  String message46;
  To2DeviceSessionInfo to2DeviceSessionInfo;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void beforeEach() {

    secureRandom = new SecureRandom();
    asymKexCodec = Mockito.mock(AsymKexCodec.class);
    bo = "{\"r3\":[2,[4,{\"dn\":\"localhost\",\"only\":\"owner\",\"pow\":8040,\"pr\":\"http\"}]"
        + ",[4,{\"dn\":\"localhost\",\"only\":\"dev\",\"po\":8040,\"pr\":\"http\"}]],\"g3\":\"i"
        + "avdTkTMT7uHZdjSoVhN8w==\",\"n7\":\"GYaV0lGJpZxbQ0DMlYOqKA==\"}";
    deviceCryptoInfo = new DeviceCryptoInfo();
    futureSignatureBlock = Mockito.mock(Future.class);
    keyExchangeDecoder = new KeyExchangeDecoder(asymKexCodec, secureRandom);
    message47Store = new Message47Store();
    r3 = new RendezvousInfo();
    setupDeviceservice = Mockito.mock(SetupDeviceService.class);
    sg = ByteBuffer.wrap(new String("[256,\"SL66mpc7q+F64VSPPOZWU2RZJAD0LjCKZ7QRNv7CnHP7QHevCG0"
        + "fcdcLMhgDxGja/U5Ee8ww85sy/nGInXrotrJ5uBMMCVCyLm2fFBeu5mOsxwozO8y"
        + "JeC6pvNKsAcSbjig+dKF6A+7L6KqSYbSYrMTXTtd4pX4AiFfxzAkSRse8nvGGR5h"
        + "AX/IcGR1ArkV0N+rsystrZLQNHDcIuM7mZfg6nNOHcQ8wCkDgpeWPiWyzSf67H/E"
        + "R5vQPaxX80tDhrGbbB5O3PMCMIc0beOKqBnBathyuyZYXxBGWshwu943Kb/j3Fif"
        + "PLuv4sD86gzE9OKt34inTTPqYgREKPPSw8g==\"]").getBytes());
    signatureBlock = new SignatureBlock(CharBuffer.wrap(bo), null, sg);
    signatureService = Mockito.mock(SignatureService.class);
    setup = Mockito.mock(Setup.class);
    serviceInfoModules = new HashSet<ServiceInfoModule>();
    sessionStorage = Mockito.mock(SessionStorage.class);
    signatureServiceFactory = Mockito.mock(SignatureServiceFactory.class);
    to2DeviceSessionInfo = new To2DeviceSessionInfo();
    uuid = new byte[16];

    message41Store = new Message41Store("\"UOpomJjQ2KeXQJO44aGLYg==\"", "\"ECDH\"",
        "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":\"localhost\",\"only\":\"owner\""
            + ",\"pow\":8040,\"pr\":\"http\"}],[4,{\"dn\":\"localhost\",\"only\":\"dev\",\"po\":"
            + "8040,\"pr\":\"http\"}]],\"g\":\"iavdTkTMT7uHZdjSoVhN8w==\",\"d\":\"cri device\","
            + "\"pk\":[1,3,[257,\"AMy6OQz5CToc51e4ZvTOm3sWr6gKZ/3ED4q+lPiEHGKu8Cdsy2rfFOtDxZr+W"
            + "22/GUJ2aK/5UkVZUdqsdLey77H0Hyyw9KcSOe92dej64rVLA+v6R4CIbyrGLpWY3nQ/nZX0pa1esckj9"
            + "PO+Xu0ONcxiNsOoVnLgShISBfviDhKGDG6OZMt4WYGOrVAFG63+cWoyXGHbzTIcr1FVjq038nhQoP32n"
            + "da+Nr211rfxPFhMr5++qD84rB5JQQLrkb7p65w1EEnLbs/m0Io3bRCSudlFPU33GfBUj2SmgaiTrMCGo"
            + "HRS6hc1hWM4BvCDW1Z6euNeGWsZPU9srbAYbpVdXi0=\",3,\"AQAB\"]],\"hdc\":[32,8,\"RA2j4"
            + "JJ4KRFic9U1vQhfCmejQ6L8RrVemJqjn+dHzn0=\"]},\"hmac\":[32,108,\"NuoFP2IoULNsu5kJc"
            + "RCG1nOY1YaLj3TilPS35/6WNwI=\"],\"dc\":[1,1,[[472,\"MIIB1DCCAXqgAwIBAgIJANUEJ6s4G"
            + "WDGMAoGCCqGSM49BAMCMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKD"
            + "BhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwHhcNMTgxMTAzMTgyNTAzWhcNMjgxMDMxMTgyNTAzWjBFM"
            + "QswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0c"
            + "yBQdHkgTHRkMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEdaOhUH0ghj4yWulJ552cK5NsLq2F/+6as"
            + "q846qJnXYx5CGSAeISnyj+AzTgbu2OzjyohV07JXZc/9MYRLxn+s6NTMFEwHQYDVR0OBBYEFF4V8TAj1"
            + "WRCQTODzupoT3aL1X3WMB8GA1UdIwQYMBaAFF4V8TAj1WRCQTODzupoT3aL1X3WMA8GA1UdEwEB/wQFM"
            + "AMBAf8wCgYIKoZIzj0EAwIDSAAwRQIhAK06vhlOjLLJw60otqeLD7OB5120pOydYlwc5KsHq7+yAiB4U"
            + "1lL8DXKh8WD04KHXQe31PUxfCIiZW/HmEw9vVzugw==\"]]],\"en\":[{\"bo\":{\"hp\":[32,8,"
            + "\"RfZ5kVQoEmNMUc1l2aCO0GI8YBRL+9m1+u1p/6pNvqg=\"],"
            + "\"hc\":[32,8,\"k77cB+VPf5oFTqmfKePGOwzsAAh2j4r75q9t3EKGnLE=\"],"
            + "\"pk\":[1,3,[257,\"ALROfFsfUuAVkzXa05o9/pCVzSrjgJN7IEMLxdGPLl6N/xAnkv4Y+Ug5sRuX0"
            + "pwZZhlSicBldW01REQ2eLoeGltX1jJqFHeMXMbp+DeAfflg0kJV//kXd8ASxJCkHza+5VPWYrX2hpASs"
            + "bNt2NBgmB38IfdU418zbRswM7+Bf0qkOOSq0mn0CCBI+9srmnQa0Ljflx5GoKScBbS2K9K27UWX0i3Er"
            + "6BmbacFvSXyA57QvnqqKnMDqzEVmQJ8gGOdhhWaivfnIyZV7qAEuLh/quih4CwubtK3KS4g2CYIr+erO"
            + "KcHeCC0rkRkqIQmLCCCJPaw/rVVqhbBmRg3GV1NMwk=\",3,\"AQAB\"]]},\"pk\":[0,0,[0]],"
            + "\"sg\":[256,\"Da81MXRiSV6TfHqzrnZZ01SlEb2PmtygvAnq5sixwfsWAugQeIbd4qreE1nYRQbrHd"
            + "f52jqsqS0PAzFMrsgi3gHht1SBCORUFTbyUti/iAx8EGg+msW/n9+M5qjTT8vbh4VijIPBXN7EFnzWPJ"
            + "epsddcjZa82jLJAsRlfR5eCayIfKJv1Gx8UCVv9kIA1DaM9aBpSmqjoUb99s7gEkeLsEwkpUSDT0hcns"
            + "JtZRf2kqtlr/suIrdweQa7b/qDvS8+mnESeuWNHZXHhPWVwttCoAqB9tQqVakfRHSuihaUc+gyYmVTrp"
            + "+OJ4TVf/U7IU+7sL/k3n48zX+3dKHNEDZCpQ==\"]}]}",
        "\"AES128/CTR/HMAC-SHA256\"",
        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEiWqCheRU9WLiB3WbWuGZrUUGD74d2bCg7M1QocIIVS2wPoCdrJ"
            + "PiFe1xFnM1YHuQgmRVdfk+/PH67ZPeSAo2/A==",
        "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgsbKgIdYSgH9JEpyiIBc6MRqFokao6xymm0q2be"
            + "p8wNigCgYIKoZIzj0DAQehRANCAASJaoKF5FT1YuIHdZta4ZmtRQYPvh3ZsKDszVChwghVLbA+gJ2sk+IV7X"
            + "EWczVge5CCZFV1+T788frtk95ICjb8",
        "9zmY7i6i0Mv5isrJYiqaRw==", null, null, null);

    message45Store = new Message45Store("\"GYaV0lGJpZxbQ0DMlYOqKA==\"", 1,
        "[86,\"ACAdArDxkfqqb5lkS9O1A1RsABXNgMRMxPXB9C0dzOP3UQAgFA8l+H+MPGNnVQ5ZSOV94FckW4VX6AO5o1s"
            + "N+7suu2YAEBfl7G7cTRlDYQV5qLMjsRA=\"]");

    message46 = "{\"ct\":[485,0,\"0O87ocvnWYsCMbRfSkxnuKLugQJ7hOLnwUdQNJYZv2jCotWpSu2nuSK1nV9AMi5gv"
        + "Vv5TATcilBQRz+cKXZlvISqhiRdc21uh+iL7/udSWkeL6wWOKKv5Gh1rKBpbTLifTGZJc0BvNgrpuLI8"
        + "wt0Y8+EEFMdDZjQv7MCYEYFSLdzeDVfIMN5RT6F1fFyNBb7h2kcZxKObdnaOQJaO1ozju0cV4cJQ3YIO"
        + "BJsskBJI3ZZzLDUW/fsr/Bk1xKC/k1OpCHyCbErzOACd2DGQpssZVDjv8+SqgOG/pymzlP8Jr6Lw4fQX"
        + "gIL8o3OG/UQLeMtHjAwSIDajqiOXCFVehaKKcx53j8jZCk1rcSI6WtTE7uEruOGx0EYqLuQ9N+1WAtPE"
        + "+5k0Bo2WPheDhHPwzV2GzoXfPwwGNj28q0zXXd1QnNKLi/p2WhJRkKqoo2TReekO4sUj/eGH7O1CQaJW"
        + "W4PkW0unAuKkFPcQnbm+oC6WnWBgmU6EgvNPyn1vfoBAIJA9yaIrilBCgLTnBv9xbQf/0Jh+dTpmG4Ya"
        + "UUd2yHfxGA0ayu86WPrwHVSSPw7o+FgSpp6WVYrpCWuJtmfH4sBhrxtqpR30jXrWzyXnOjKvxaW9MVVX"
        + "6kFqETDgZozF45AUnShDPI=\"],\"hmac\":[32,108,\"VqgXk50h5QWfkPM93PPRHi7MoZCY8okhnr"
        + "LE+xmpcA0=\"]}";

    message46Handler = new Message46Handler(signatureServiceFactory, sessionStorage,
        setupDeviceservice, secureRandom, keyExchangeDecoder, serviceInfoModules);

  }

  @Test
  void test_Message46() throws IOException, InterruptedException, ExecutionException {

    keyExchangeDecoder.getKeyExchangeType(KeyExchangeType.ECDH, null);
    to2DeviceSessionInfo.setMessage41Store(message41Store);
    to2DeviceSessionInfo.setMessage45Store(message45Store);
    to2DeviceSessionInfo.setMessage47Store(message47Store);
    to2DeviceSessionInfo.setDeviceCryptoInfo(deviceCryptoInfo);

    Mockito.when(sessionStorage.load(Mockito.any(UUID.class))).thenReturn(to2DeviceSessionInfo);
    Mockito
        .when(setupDeviceservice.setup(Mockito.any(UUID.class), Mockito.any(RendezvousInfo.class)))
        .thenReturn(setup);
    Mockito.when(setup.r3()).thenReturn(r3);
    Mockito.when(setup.g3()).thenReturn(UUID.randomUUID());
    Mockito.when(signatureServiceFactory.build(Mockito.any(UUID.class)))
        .thenReturn(signatureService);
    Mockito.when(signatureService.sign(Mockito.anyString())).thenReturn(futureSignatureBlock);
    Mockito.when(futureSignatureBlock.get()).thenReturn(signatureBlock);

    try {
      message46Handler.onPost(message46, Mockito.anyString());
    } catch (Exception e) {
      // nothing to do.
    }

  }

  @Test
  void test_Message46_BadRequest() throws Exception {

    Mockito.when(sessionStorage.load(Mockito.any(UUID.class))).thenReturn(to2DeviceSessionInfo);
    Assertions.assertThrows(SdoProtocolException.class, () -> {
      message46Handler.onPost(message46, Mockito.anyString());
    });

    message45Store = new Message45Store("\"GYaV0lGJpZxbQ0DMlYOqKA==\"", 2,
        "[86,\"ACAdArDxkfqqb5lkS9O1A1RsABXNgMRMxPXB9C0dzOP3UQAgFA8l+H+MPGNnVQ5ZSOV94FckW4VX"
            + "6AO5o1sN+7suu2YAEBfl7G7cTRlDYQV5qLMjsRA=\"]");
    keyExchangeDecoder.getKeyExchangeType(KeyExchangeType.ECDH, null);
    to2DeviceSessionInfo.setMessage41Store(message41Store);
    to2DeviceSessionInfo.setMessage45Store(message45Store);
    to2DeviceSessionInfo.setMessage47Store(message47Store);
    to2DeviceSessionInfo.setDeviceCryptoInfo(deviceCryptoInfo);
    Mockito.when(sessionStorage.load(Mockito.any(UUID.class))).thenReturn(to2DeviceSessionInfo);
    Mockito
        .when(setupDeviceservice.setup(Mockito.any(UUID.class), Mockito.any(RendezvousInfo.class)))
        .thenReturn(setup);
    Mockito.when(setup.r3()).thenReturn(r3);
    Mockito.when(setup.g3()).thenReturn(UUID.randomUUID());
    Mockito.when(signatureServiceFactory.build(Mockito.any(UUID.class)))
        .thenReturn(signatureService);
    Mockito.when(signatureService.sign(Mockito.anyString())).thenReturn(futureSignatureBlock);
    Mockito.when(futureSignatureBlock.get()).thenReturn(signatureBlock);
    Assertions.assertThrows(SdoProtocolException.class, () -> {
      message46Handler.onPost(message46, Mockito.anyString());
    });
  }
}
