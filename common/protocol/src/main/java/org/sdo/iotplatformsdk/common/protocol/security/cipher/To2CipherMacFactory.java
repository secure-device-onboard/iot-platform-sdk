// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security.cipher;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.Mac;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoConstants;
import org.sdo.iotplatformsdk.common.protocol.types.MacType;

public class To2CipherMacFactory {

  private MacType macType;

  public To2CipherMacFactory(MacType macType) {
    this.setMacType(macType);
  }

  public Mac build() throws NoSuchAlgorithmException, NoSuchProviderException {
    return Mac.getInstance(getMacType().getJceName(), SdoConstants.SECURITY_PROVIDER);
  }

  public MacType getMacType() {
    return macType;
  }

  public void setMacType(MacType macType) {
    this.macType = macType;
  }
}
