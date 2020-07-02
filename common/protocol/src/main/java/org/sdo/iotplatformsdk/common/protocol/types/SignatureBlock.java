// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.PublicKey;
import java.util.Objects;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

/**
 * SDO Composite type 'SignatureBlock', from protocol specification 1.12j 3.2.
 */
public class SignatureBlock {

  // The body text, signed (in its ASCII form, usually) by 'sg'.
  private String bo;

  // The public key advertised in this block. This is not necessarily the key which was
  // used to generate 'sg.' Nulls are interpreted as the 'NONE' value.
  private PublicKey pk;

  // The signature text.
  private ByteBuffer sg;

  /**
   * Constructor.
   */
  public SignatureBlock(CharSequence bo, PublicKey pk, ByteBuffer sg) {
    this.bo = bo.toString();
    this.pk = pk;
    this.sg = Buffers.clone(sg);
  }

  public CharBuffer getBo() {
    return CharBuffer.wrap(bo).asReadOnlyBuffer();
  }

  public void setBo(CharSequence bo) {
    this.bo = bo.toString();
  }

  public PublicKey getPk() {
    return pk;
  }

  public void setPk(PublicKey pk) {
    this.pk = pk;
  }

  public ByteBuffer getSg() {
    return sg.asReadOnlyBuffer();
  }

  public void setSg(ByteBuffer sg) {
    this.sg = Buffers.clone(sg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getBo(), getPk(), getSg());
  }

  @Override
  public boolean equals(Object thatObject) {

    if (!(thatObject instanceof SignatureBlock)) {
      return false;
    }

    SignatureBlock that = (SignatureBlock) thatObject;
    return getBo().equals(that.getBo()) && getPk().equals(that.getPk())
        && getSg().equals(that.getSg());
  }
}
