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

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel112;
import org.sdo.iotplatformsdk.common.protocol.types.DigestType;
import org.sdo.iotplatformsdk.common.protocol.types.KeyExchangeType;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

class CryptoLevel112Test {

  CryptoLevel112 cryptoLevel;
  PublicKey pk;

  @BeforeEach
  void beforeEach() {

    cryptoLevel = new CryptoLevel112();
    pk = Mockito.mock(PublicKey.class);
  }

  @Test
  void test_Encoder() throws IOException, InvalidKeyException, NoSuchAlgorithmException {

    cryptoLevel.getDigestService();
    cryptoLevel.getKeyExchangeType(pk);
    cryptoLevel.getMacService();
    cryptoLevel.getSekDerivationFunction();
    cryptoLevel.getSvkDerivationFunction();
    cryptoLevel.version();
    cryptoLevel.hasType(DigestType.SHA256);
    cryptoLevel.hasType(KeyExchangeType.ASYMKEX);
    cryptoLevel.hasType(MacType.HMAC_SHA256);


  }

}
