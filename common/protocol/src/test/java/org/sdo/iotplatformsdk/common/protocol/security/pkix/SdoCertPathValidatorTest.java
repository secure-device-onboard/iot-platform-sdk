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

package org.sdo.iotplatformsdk.common.protocol.security.pkix;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.PKIXRevocationChecker.Option;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.security.pkix.SdoCertPathValidator;

class SdoCertPathValidatorTest {

  CertPath certPath;
  CertStore certStore;
  Set<Option> revocationCheckerOptions;
  Set<TrustAnchor> trustAnchors;
  SdoCertPathValidator sdoCertPathValidator;
  TrustAnchor trustAnchor;
  X509Certificate trustedCert;

  @BeforeEach
  void beforeEach() {

    certPath = Mockito.mock(CertPath.class);
    certStore = Mockito.mock(CertStore.class);
    revocationCheckerOptions = new HashSet<Option>();
    trustAnchors = new HashSet<TrustAnchor>();
    trustedCert = Mockito.mock(X509Certificate.class);
    trustAnchor = new TrustAnchor(trustedCert, null);
    trustAnchors.add(trustAnchor);

    sdoCertPathValidator =
        new SdoCertPathValidator(certStore, revocationCheckerOptions, trustAnchors);
  }

  @Test
  void test_SdoCertPathValidator_BadRequest() throws CertPathValidatorException,
      InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

    org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
      sdoCertPathValidator.validate(certPath);
    });
  }

}
