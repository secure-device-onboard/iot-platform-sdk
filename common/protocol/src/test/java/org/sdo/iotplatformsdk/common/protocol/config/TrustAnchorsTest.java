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

package org.sdo.iotplatformsdk.common.protocol.config;

import java.io.IOException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sdo.iotplatformsdk.common.protocol.config.TrustAnchors;

class TrustAnchorsTest {

  Set<TrustAnchor> trustanchors;
  TrustAnchor trustAnchor;
  TrustAnchors trustAnchors1;
  TrustAnchors trustAnchors2;
  X509Certificate trustedCert;

  @BeforeEach
  void beforeEach() {

    trustedCert = Mockito.mock(X509Certificate.class);
    trustAnchor = new TrustAnchor(trustedCert, null);
    trustanchors = new HashSet<TrustAnchor>();
    trustanchors.add(trustAnchor);
  }

  @Test
  void test_Bean() throws IOException {

    trustAnchors1 = new TrustAnchors();
    trustAnchors2 = new TrustAnchors(trustanchors);
  }
}
