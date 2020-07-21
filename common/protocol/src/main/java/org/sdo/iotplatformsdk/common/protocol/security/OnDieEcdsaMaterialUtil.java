// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

public interface OnDieEcdsaMaterialUtil {

  /**
   * Return the CRL content as byte array for the given crlName.
   *
   * @param crlName name of the crl
   * @return        contents of CRL as byte array.
   */
  public byte[] getCrl(final String crlName);
}
