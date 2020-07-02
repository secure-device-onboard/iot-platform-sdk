// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.config;

import java.security.cert.TrustAnchor;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class TrustAnchors extends HashSet<TrustAnchor> {

  public TrustAnchors(Set<TrustAnchor> s) {
    super(s);
  }

  public TrustAnchors() {
    super();
  }
}
