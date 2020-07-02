// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.epid;

/**
 * Constants class for EPID extension related constants.
 */
public class EpidConstants {

  public static final String onlineEpidUrlDefault = "https://verify.epid.trustedservices.intel.com";
  public static final String sandboxEpidUrlDefault =
      "https://verify.epid-sbx.trustedservices.intel.com";

  public static final int EPID1X_GID_SIZE = 4;
  public static final int EPID2X_GID_SIZE = 16;
}
