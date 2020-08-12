// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.util.NoSuchElementException;

/**
 * SDO Public Key types.
 *
 * <p>These enumerations are the key type names from the Protocol Specification with all
 * non-alphanumerics replaced with underscores.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2.2: Public Key Types"
 */
public enum KeyType {
  /**
   * No public key present: key is PKNull.
   */
  NONE(0),
  /**
   * RSA2048RESTR means RSA2048 with exponent 65537. This is a restriction of the SDO Client-Intel.
   * The SDO Manufacturer/Reseller Toolkit and Owner must ensure that all keys in the Ownership
   * Voucher meet this restriction for SDO Client-Intel. Note that the Owner allocates the “Owner2”
   * key, which must also meet this restriction.
   *
   * @see KeyType#RSA_UR
   */
  RSA2048RESTR(1),
  /**
   * Diffie-Hellman.
   */
  DH(2),
  /**
   * Digital Signature Algorithm.
   */
  DSA(3),
  /**
   * An RSA type that is not restricted.
   *
   * @see KeyType#RSA2048RESTR
   */
  RSA_UR(4),
  /**
   * Elliptic Curve Digital Signature Algorithm P-256.
   *
   * @see <a href="http://dx.doi.org/10.6028/NIST.FIPS.186-4">FIPS 186-4</a>
   */
  ECDSA_P_256(13),
  /**
   * Elliptic Curve Digital Signature Algorithm P-384.
   *
   * @see <a href="http://dx.doi.org/10.6028/NIST.FIPS.186-4">FIPS 186-4</a>
   */
  ECDSA_P_384(14),
  /**
   * EPID v1.0
   *
   * @see <a href=https://01.org/epid-sdk>The Intel EPID SDK</a>
   */
  EPIDV1_0(90),
  /**
   * EPID v1.1
   *
   * @see <a href=https://01.org/epid-sdk>The Intel EPID SDK</a>
   */
  EPIDV1_1(91),
  /**
   * EPID v2.0
   *
   * @see <a href=https://01.org/epid-sdk>The Intel EPID SDK</a>
   */
  EPIDV2_0(92),
  /**
   * On-Die ECDSA.
   */
  ONDIE_ECDSA_384(93);

  private int value;

  private KeyType(int value) {
    this.value = value;
  }

  /**
   * Utility method to return the {@link KeyType} corresponding to the input number.
   *
   * @param n the input number
   * @return {@link KeyType}
   */
  public static KeyType fromNumber(Number n) {

    int i = n.intValue();

    for (KeyType e : values()) {

      if (e.toInteger() == i) {
        return e;
      }
    }

    throw new NoSuchElementException(KeyType.class.getName() + ":" + i);
  }

  int getValue() {
    return value;
  }

  public int toInteger() {
    return getValue();
  }
}
