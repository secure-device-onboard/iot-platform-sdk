// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

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
