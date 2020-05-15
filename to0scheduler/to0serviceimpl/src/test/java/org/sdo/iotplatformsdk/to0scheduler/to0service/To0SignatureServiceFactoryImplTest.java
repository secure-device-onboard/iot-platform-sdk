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
import org.sdo.iotplatformsdk.to0scheduler.rest.RestClient;

@RunWith(JUnit4.class)
public class To0SignatureServiceFactoryImplTest extends TestCase {

  @Mock
  RestClient restClient;

  To0SignatureServiceFactoryImpl demoSignatureServiceFactory;

  UUID[] hints;
  UUID uuid;

  SignatureResponse signatureResponse;
  SignatureBlock expectedSignatureBlock;

  String bo = "{\"sz\":1,\"oh\":{\"pv\":113,\"pe\":3,\"r\":[2,[4,{\"dn\":\"localhost\",\"only\""
      + ":\"owner\",\"pow\":8040,\"pr\":\"http\"}],[4,{\"dn\":\"localhost\",\"only\":\"dev\","
      + "\"po\":8040,\"pr\":\"http\"}]],\"g\":\"iavdTkTMT7uHZdjSoVhN8w==\",\"d\":\"cri device\""
      + ",\"pk\":[1,3,[257,\"AMy6OQz5CToc51e4ZvTOm3sWr6gKZ/3ED4q+lPiEHGKu8Cdsy2rfFOtDxZr+W22/GU"
      + "J2aK/5UkVZUdqsdLey77H0Hyyw9KcSOe92dej64rVLA+v6R4CIbyrGLpWY3nQ/nZX0pa1esckj9PO+Xu0ONcxi"
      + "NsOoVnLgShISBfviDhKGDG6OZMt4WYGOrVAFG63+cWoyXGHbzTIcr1FVjq038nhQoP32nda+Nr211rfxPFhMr5"
      + "++qD84rB5JQQLrkb7p65w1EEnLbs/m0Io3bRCSudlFPU33GfBUj2SmgaiTrMCGoHRS6hc1hWM4BvCDW1Z6euNe"
      + "GWsZPU9srbAYbpVdXi0=\",3,\"AQAB\"]],\"hdc\":[32,8,\"RA2j4JJ4KRFic9U1vQhfCmejQ6L8RrVemJ"
      + "qjn+dHzn0=\"]},\"hmac\":[32,108,\"NuoFP2IoULNsu5kJcRCG1nOY1YaLj3TilPS35/6WNwI=\"],"
      + "\"n5\":\"opjA5M7aBBMCVlp33wcqTA==\",\"n6\":\"lt8Vj+oXIUIr0SrEsUX5eA==\",\"eB\":[13,0,"
      + "\"\"],\"xA\":[150,\"ADAIqfvgKS6BgyqqmHEjd0erhFLpdqJfxZuoBY8731SDmkIE6s81Kh7rJlB4RXsX0"
      + "wEAMPbNN7exxXcUi1nqYdTnXjRW5RI9bDnpY2nSA1NhXjr0JZ8pNaYa163UWRd+wLQeswAwQ7MxPnVfnSCbFX"
      + "A+p1Gv39S3Z8zhaE6hYuimdFx37XQoRN87zsLDtinukENAs8g8\"]}";
  ByteBuffer sg;

  @Override
  @Before
  public void setUp() throws IOException {
    restClient = Mockito.mock(RestClient.class);
    signatureResponse = Mockito.mock(SignatureResponse.class);
    uuid = UUID.fromString("89abdd4e-44cc-4fbb-8765-d8d2a1584df3");
    hints = new UUID[1];
    hints[0] = uuid;
    demoSignatureServiceFactory = new To0SignatureServiceFactoryImpl(restClient);
    Mockito.when(restClient.signatureOperation(uuid, bo)).thenReturn(signatureResponse);
    // Return real values as the test will fail otherwise.
    Mockito.when(signatureResponse.getAlg()).thenReturn("RSA");
    Mockito.when(signatureResponse.getPk()).thenReturn(
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtE58Wx9S4BWTNdrTmj3+kJXNKuOAk3sgQwvF0Y8u"
            + "Xo3/ECeS/hj5SDmxG5fSnBlmGVKJwGV1bTVERDZ4uh4aW1fWMmoUd4xcxun4N4B9+WDSQlX/+Rd3wBLEkK"
            + "QfNr7lU9ZitfaGkBKxs23Y0GCYHfwh91TjXzNtGzAzv4F/SqQ45KrSafQIIEj72yuadBrQuN+XHkagpJwF"
            + "tLYr0rbtRZfSLcSvoGZtpwW9JfIDntC+eqoqcwOrMRWZAnyAY52GFZqK9+cjJlXuoAS4uH+q6KHgLC5u0r"
            + "cpLiDYJgiv56s4pwd4ILSuRGSohCYsIIIk9rD+tVWqFsGZGDcZXU0zCQIDAQAB");
    Mockito.when(signatureResponse.getSg()).thenReturn(
        "AEBIf8ZrEFDQoz79DRGtvK3TPrBcyJw1UK/XSO2wTFAUKivZVhvPFV98jxYePuJG+LWJerU4dqe1tJkTRvOU"
            + "IRzogI5K+LmTDhhuqhTNVUFCIOb5Aio34DCEipYQdyhNwKuhgClDPot8q8ny5j42RIrNGPAP5EOcW60M/p"
            + "H5Qo1b+9LIG/65sVehRz2kDnRCITLaKdbcg21uL9zcBtn8RYd0ogkCa+HldHalcgWDvrxZNZjudx7rcR/I"
            + "5InXvaN9MBBffq4B+FYhLRlIh570/JIXYxVX57JAz/Te2vg5WY3RkngiQnqHyOpa/qibWK4yrU3WVsJPhe"
            + "XjvZc8PX7J9A==");
    sg = ByteBuffer.wrap(Base64.getDecoder().decode(signatureResponse.getSg()));
  }

  @Test
  public void testBuild() throws InterruptedException, ExecutionException {

    SignatureBlock actualSignatureBlock = demoSignatureServiceFactory.build(hints).sign(bo).get();

    Assert.assertEquals(sg, actualSignatureBlock.getSg());

  }

  @Test(expected = Exception.class)
  public void testBuildEmptyHints() throws InterruptedException, ExecutionException, Exception {

    hints = new UUID[0];
    SignatureBlock actualSignatureBlock = demoSignatureServiceFactory.build(hints).sign(bo).get();
    SignatureBlock expectedSignatureBlock = new SignatureBlock(CharBuffer.wrap(bo), null, null);
    Assert.assertEquals(expectedSignatureBlock.getBo(), actualSignatureBlock.getBo());
    Assert.assertEquals(expectedSignatureBlock.getSg(), actualSignatureBlock.getSg());
    Assert.assertEquals(expectedSignatureBlock.getPk(), actualSignatureBlock.getPk());
  }

  @Test(expected = Exception.class)
  public void testBuildEmptySignature() throws InterruptedException, ExecutionException, Exception {

    Mockito.when(signatureResponse.getSg()).thenReturn(null);

    SignatureBlock actualSignatureBlock = demoSignatureServiceFactory.build(hints).sign(bo).get();
    SignatureBlock expectedSignatureBlock = new SignatureBlock(CharBuffer.wrap(bo), null, null);
    Assert.assertEquals(expectedSignatureBlock.getBo(), actualSignatureBlock.getBo());
    Assert.assertEquals(expectedSignatureBlock.getSg(), actualSignatureBlock.getSg());
    Assert.assertEquals(expectedSignatureBlock.getPk(), actualSignatureBlock.getPk());
  }

  @Test(expected = Exception.class)
  public void testBuildEmptyPk() throws InterruptedException, ExecutionException, Exception {

    Mockito.when(signatureResponse.getPk()).thenReturn(null);

    SignatureBlock actualSignatureBlock = demoSignatureServiceFactory.build(hints).sign(bo).get();
    SignatureBlock expectedSignatureBlock = new SignatureBlock(CharBuffer.wrap(bo), null, null);
    Assert.assertEquals(expectedSignatureBlock.getBo(), actualSignatureBlock.getBo());
    Assert.assertEquals(expectedSignatureBlock.getSg(), actualSignatureBlock.getSg());
    Assert.assertEquals(expectedSignatureBlock.getPk(), actualSignatureBlock.getPk());
  }
}
