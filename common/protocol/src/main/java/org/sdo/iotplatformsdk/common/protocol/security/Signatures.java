// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.security.spec.AlgorithmParameterSpec;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey;

public abstract class Signatures {

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

    } else if (key instanceof EpidKey) {
      return getInstance((EpidKey) key);

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

  private static Signature getInstance(EpidKey key) throws NoSuchAlgorithmException {
    return Signature.getInstance(key.getAlgorithm());
  }

  /**
   * Verifies a signed block of data.
   *
   * @param data The data 'message' which has been signed.
   * @param signature The signature.
   * @param verificationKey The public key to use for verification.
   * @return True if the signature verifies successfully.
   */
  public static boolean verifySignature(final byte[] data, final byte[] signature,
      final PublicKey verificationKey) {
    return verifySignature(data, signature, verificationKey, null);
  }

  /**
   * Verifies a signed block of data.
   *
   * @param data The data 'message' which has been signed.
   * @param signature The signature.
   * @param verificationKey The public key to use for verification.
   * @param params The algorithm parameters, or null if not needed.
   * @return True if the signature verifies successfully.
   */
  public static boolean verifySignature(final byte[] data, final byte[] signature,
      final PublicKey verificationKey, final AlgorithmParameterSpec params) {

    try {
      Signature verifier = getInstance(verificationKey);
      verifier.initVerify(verificationKey);
      if (null != params) {
        verifier.setParameter(params);
      }
      verifier.update(data);
      return verifier.verify(signature);

    } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException
        | SignatureException e) {

      throw new RuntimeException(e);
    }
  }
}
