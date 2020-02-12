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

package org.sdo.iotplatformsdk.ocs.fsimpl.credsmanager;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;

public class Signatures {

  private static final String ECDSA = "ECDSA";
  private static final String RSA = "RSA";
  private static final String SHA = "SHA";
  private static final String WITH = "with";

  /**
   * Convert from a {@link java.security.Key}'s algorithm string to the
   * {@link java.security.Signature} algorithm string which should be matched to that key.
   */
  public static Signature getInstance(Key key) throws NoSuchAlgorithmException {

    if (key instanceof RSAKey) {
      return getInstance((RSAKey) key);
    } else if (key instanceof ECKey) {
      return getInstance((ECKey) key);
    } else {
      throw new UnsupportedOperationException(key.getAlgorithm() + "is not supported");
    }
  }

  private static Signature getInstance(RSAKey key) throws NoSuchAlgorithmException {
    int bytes = key.getModulus().bitLength() / Byte.SIZE;
    String name = SHA + bytes + WITH + RSA;
    return Signature.getInstance(name);
  }

  private static Signature getInstance(ECKey key) throws NoSuchAlgorithmException {
    int bytes = key.getParams().getCurve().getField().getFieldSize();
    String name = SHA + bytes + WITH + ECDSA;
    return Signature.getInstance(name);
  }
}
