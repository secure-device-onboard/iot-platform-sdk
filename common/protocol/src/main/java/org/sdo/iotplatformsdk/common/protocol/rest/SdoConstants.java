// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.rest;

public class SdoConstants {

  // The MTU limit for SDO protocol messages.
  public static final int MTU_LIMIT = 1300;

  // AES block size. Always 16.
  public static final int AES_BLOCK_SIZE = 128 / 8;

  // Size of the nonce in the 16-byte initialization vector for AES CTR mode of operation.
  public static final int CTR_NONCE_SIZE = 12;

  // Cipher transformation for AES-CTR mode.
  public static final String CIPHER_TRANSFORM_CTR = "AES/CTR/NoPadding";

  // Cipher transformation for AES-CBC mode.
  public static final String CIPHER_TRANSFORM_CBC = "AES/CBC/PKCS5Padding";

  // Security provider to be used for all crypto operations.
  public static final String SECURITY_PROVIDER = "SunJCE";
}
