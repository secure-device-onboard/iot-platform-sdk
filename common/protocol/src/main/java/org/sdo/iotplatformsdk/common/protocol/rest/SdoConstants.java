/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

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
