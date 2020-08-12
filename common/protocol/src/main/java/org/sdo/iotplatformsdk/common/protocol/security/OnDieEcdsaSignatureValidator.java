// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.cert.CRL;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnDieEcdsaSignatureValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnDieEcdsaSignatureValidator.class);

  private final OnDieEcdsaMaterialUtil onDieEcdsaMaterialUtil;
  private static final String ECDSA_ALGORITHM = "EC";
  private static final int taskInfoLength = 36;
  private static final int rLength = 48;
  private static final int sLength = 48;

  /**
   * Constructor.
   */
  public OnDieEcdsaSignatureValidator(OnDieEcdsaMaterialUtil onDieEcdsaMaterialUtil) {
    this.onDieEcdsaMaterialUtil = onDieEcdsaMaterialUtil;
  }

  /**
   * Verify the given message and the signature by using the public key present inside the first
   * certificate from {@link CertPath}. Prior ot verifying the signature, revocation
   * check is performed by fetching the appropriate CRL from {@link OnDieEcdsaMaterialUtil}.
   *
   * @param message   message whose signature needs to be verified as byte array
   * @param signature signature to be verified as byte array
   * @param certPath  CertPath instance containing the public key and revocation check info
   * @return          boolean value: true or false
   */
  public boolean verifySignature(final byte[] message, final byte[] signature,
      final CertPath certPath) {
    final List<Certificate> certificateList = (List<Certificate>) certPath.getCertificates();
    // check for revocations first, and then validate signature length.
    // signature data is of the form: taskinfo + R + S.
    if (!checkRevocations(certificateList)
        || signature.length != (taskInfoLength + rLength + sLength)) {
      return false;
    }
    final byte[] taskInfo = Arrays.copyOfRange(signature, 0, taskInfoLength);
    final byte[] signatureR =
        Arrays.copyOfRange(signature, taskInfoLength, taskInfoLength + rLength);
    final byte[] signatureS =
        Arrays.copyOfRange(signature, taskInfoLength + rLength, taskInfoLength + rLength + sLength);
    final ByteBuffer signedDataBuffer = ByteBuffer.allocate(message.length + taskInfo.length);
    signedDataBuffer.put(taskInfo);
    signedDataBuffer.put(message);
    final boolean signatureVerified = verifyEcdsaSha384Signature(signedDataBuffer.array(),
        signatureR, signatureS, certificateList.get(0).getPublicKey().getEncoded());
    return signatureVerified;
  }

  /**
   * Verify the given ECDSA-SHA384 signature (r and s values) for the given message and the public
   * key.
   *
   * @param message    the message to be verified
   * @param signatureR 'r' value of ECDSA signature
   * @param signatureS 's' value of ECDSA signature
   * @param publicKey  the public key that is used to verify the signature
   * @return           boolean value: true if verification succeeded
   */
  private boolean verifyEcdsaSha384Signature(final byte[] message, final byte[] signatureR,
      final byte[] signatureS, final byte[] publicKey) {
    try {
      final SHA384Digest digest = new SHA384Digest();
      digest.update(message, 0, message.length);
      final byte[] signedDigest = new byte[digest.getDigestSize()];
      digest.doFinal(signedDigest, 0);

      final BigInteger r = new BigInteger(1, signatureR);
      final BigInteger s = new BigInteger(1, signatureS);

      final ECDSASigner ecdsaSigner = new ECDSASigner();
      final KeyFactory keyFactory =
          KeyFactory.getInstance(ECDSA_ALGORITHM, BouncyCastleSupplier.load());
      final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
      ecdsaSigner.init(false,
          ECUtil.generatePublicKeyParameter(keyFactory.generatePublic(publicKeySpec)));
      boolean verified = ecdsaSigner.verifySignature(signedDigest, r, s);
      return verified;
    } catch (Exception e) {
      LOGGER.error("Signature verification failed: " + e.getMessage());
      LOGGER.debug(e.getMessage(), e);
      return false;
    }
  }

  /**
   * Perform revocation check by fetching the CRL information from the Distribution Points of
   * certificates. Appropriate CRL is loaded from {@link OnDieEcdsaMaterialUtil} using the
   * GeneralName.
   *
   * @param certificateList {@link ArrayList} of certificates.
   * @return                boolean value: true or false
   */
  private boolean checkRevocations(List<Certificate> certificateList) {
    // Check revocations first.
    try {
      final CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
      for (final Certificate cert : certificateList) {
        final X509Certificate x509cert = (X509Certificate) cert;
        final X509CertificateHolder certHolder = new X509CertificateHolder(x509cert.getEncoded());
        final CRLDistPoint cdp = CRLDistPoint.fromExtensions(certHolder.getExtensions());
        if (cdp != null) {
          final DistributionPoint[] distPoints = cdp.getDistributionPoints();
          for (DistributionPoint dp : distPoints) {
            final GeneralName[] generalNames =
                GeneralNames.getInstance(dp.getDistributionPoint().getName()).getNames();
            for (final GeneralName generalName : generalNames) {
              final byte[] crlBytes =
                  onDieEcdsaMaterialUtil.getCrl(generalName.getName().toString());
              if (crlBytes == null || crlBytes.length == 0) {
                return false;
              } else {
                final CRL crl = certificateFactory.generateCRL(new ByteArrayInputStream(crlBytes));
                if (crl.isRevoked(cert)) {
                  return false;
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
