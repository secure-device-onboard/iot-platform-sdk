// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Objects;
import org.sdo.iotplatformsdk.common.protocol.codecs.SdoErrorCodec;

/**
 * SDO Error.
 *
 * @see "SDO Protocol Specification, 1.13b, 5.1.1: Error"
 */
public class SdoError implements Message {

  private SdoErrorCode ec;
  private String em;
  private Integer emsg;

  /**
   * Constructor.
   */
  public SdoError(SdoErrorCode ec, int emsg, String em) {
    setEc(ec);
    setEmsg(emsg);
    setEm(em);
  }

  public SdoError(SdoErrorCode ec, MessageType emsg, String em) {
    this(ec, emsg.intValue(), em);
  }

  public SdoErrorCode getEc() {
    return ec;
  }

  public void setEc(SdoErrorCode ec) {
    this.ec = ec;
  }

  public String getEm() {
    return em;
  }

  public void setEm(String em) {
    this.em = em;
  }

  public Integer getEmsg() {
    return emsg;
  }

  public void setEmsg(Integer emsg) {
    this.emsg = emsg;
  }

  @Override
  public Version getVersion() {
    return Version.VERSION_1_13;
  }

  @Override
  public MessageType getType() {
    return MessageType.ERROR;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ec, em, emsg);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SdoError sdoError = (SdoError) o;
    return ec == sdoError.ec && Objects.equals(em, sdoError.em)
        && Objects.equals(emsg, sdoError.emsg);
  }

  @Override
  public String toString() {
    final StringWriter writer = new StringWriter();
    try {
      new SdoErrorCodec().encoder().apply(writer, this);
      return writer.toString();

    } catch (IOException e) {
      return Object.class.toString();
    }
  }
}
