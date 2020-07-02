// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.sdo.iotplatformsdk.ops.rest.RestClient;

@RunWith(JUnit4.class)
public class OpsSignatureServiceFactoryTest extends TestCase {

  @Mock
  RestClient restClient;

  OpsSignatureServiceFactory opsSignatureServiceFactory;

  UUID[] hints;
  UUID uuid;

  SignatureResponse signatureResponse;
  SignatureBlock expectedSignatureBlock;

  String bo = "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":"
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
  ByteBuffer sg;

  @Override
  @Before
  public void setUp() throws IOException {
    restClient = Mockito.mock(RestClient.class);
    signatureResponse = Mockito.mock(SignatureResponse.class);
    uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");
    hints = new UUID[1];
    hints[0] = uuid;
    opsSignatureServiceFactory = new OpsSignatureServiceFactory(restClient);
    Mockito.when(restClient.signatureOperation(uuid, bo)).thenReturn(signatureResponse);
    // Return real values as the test will fail otherwise.
    Mockito.when(signatureResponse.getAlg()).thenReturn("RSA");
    Mockito.when(signatureResponse.getPk()).thenReturn(
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtE58Wx9S4BWTNdrTmj3+kJXNKuOAk3sgQwvF0Y8"
            + "uXo3/ECeS/hj5SDmxG5fSnBlmGVKJwGV1bTVERDZ4uh4aW1fWMmoUd4xcxun4N4B9+WDSQlX/+Rd3wBLE"
            + "kKQfNr7lU9ZitfaGkBKxs23Y0GCYHfwh91TjXzNtGzAzv4F/SqQ45KrSafQIIEj72yuadBrQuN+XHkagp"
            + "JwFtLYr0rbtRZfSLcSvoGZtpwW9JfIDntC+eqoqcwOrMRWZAnyAY52GFZqK9+cjJlXuoAS4uH+q6KHgLC"
            + "5u0rcpLiDYJgiv56s4pwd4ILSuRGSohCYsIIIk9rD+tVWqFsGZGDcZXU0zCQIDAQAB");
    Mockito.when(signatureResponse.getSg()).thenReturn(
        "AEBIf8ZrEFDQoz79DRGtvK3TPrBcyJw1UK/XSO2wTFAUKivZVhvPFV98jxYePuJG+LWJerU4dqe1tJkTRvO"
            + "UIRzogI5K+LmTDhhuqhTNVUFCIOb5Aio34DCEipYQdyhNwKuhgClDPot8q8ny5j42RIrNGPAP5EOcW60M"
            + "/pH5Qo1b+9LIG/65sVehRz2kDnRCITLaKdbcg21uL9zcBtn8RYd0ogkCa+HldHalcgWDvrxZNZjudx7rc"
            + "R/I5InXvaN9MBBffq4B+FYhLRlIh570/JIXYxVX57JAz/Te2vg5WY3RkngiQnqHyOpa/qibWK4yrU3WVs"
            + "JPheXjvZc8PX7J9A==");
    sg = ByteBuffer.wrap(Base64.getDecoder().decode(signatureResponse.getSg()));
  }

  @Test
  public void testBuild() throws InterruptedException, ExecutionException {

    SignatureBlock actualSignatureBlock = opsSignatureServiceFactory.build(hints).sign(bo).get();

    Assert.assertEquals(sg, actualSignatureBlock.getSg());

  }

  @Test(expected = Exception.class)
  public void testBuildEmptyHints() throws InterruptedException, ExecutionException, Exception {

    hints = new UUID[0];
    SignatureBlock actualSignatureBlock = opsSignatureServiceFactory.build(hints).sign(bo).get();
    SignatureBlock expectedSignatureBlock = new SignatureBlock(CharBuffer.wrap(bo), null, null);
    Assert.assertEquals(expectedSignatureBlock.getBo(), actualSignatureBlock.getBo());
    Assert.assertEquals(expectedSignatureBlock.getSg(), actualSignatureBlock.getSg());
    Assert.assertEquals(expectedSignatureBlock.getPk(), actualSignatureBlock.getPk());
  }

  @Test(expected = Exception.class)
  public void testBuildEmptySignature() throws InterruptedException, ExecutionException, Exception {

    Mockito.when(signatureResponse.getSg()).thenReturn(null);

    SignatureBlock actualSignatureBlock = opsSignatureServiceFactory.build(hints).sign(bo).get();
    SignatureBlock expectedSignatureBlock = new SignatureBlock(CharBuffer.wrap(bo), null, null);
    Assert.assertEquals(expectedSignatureBlock.getBo(), actualSignatureBlock.getBo());
    Assert.assertEquals(expectedSignatureBlock.getSg(), actualSignatureBlock.getSg());
    Assert.assertEquals(expectedSignatureBlock.getPk(), actualSignatureBlock.getPk());
  }

  @Test(expected = Exception.class)
  public void testBuildEmptyPk() throws InterruptedException, ExecutionException, Exception {

    Mockito.when(signatureResponse.getPk()).thenReturn(null);

    SignatureBlock actualSignatureBlock = opsSignatureServiceFactory.build(hints).sign(bo).get();
    SignatureBlock expectedSignatureBlock = new SignatureBlock(CharBuffer.wrap(bo), null, null);
    Assert.assertEquals(expectedSignatureBlock.getBo(), actualSignatureBlock.getBo());
    Assert.assertEquals(expectedSignatureBlock.getSg(), actualSignatureBlock.getSg());
    Assert.assertEquals(expectedSignatureBlock.getPk(), actualSignatureBlock.getPk());
  }
}
