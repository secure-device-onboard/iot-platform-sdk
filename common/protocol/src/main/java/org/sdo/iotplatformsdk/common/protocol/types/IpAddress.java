// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.CharBuffer;
import java.util.Base64;
import java.util.Objects;
import org.sdo.iotplatformsdk.common.protocol.types.UInt.UInt8;

/**
 * SDO composite type 'IPAddress'.
 */
public class IpAddress {

  private final InetAddress address;

  /**
   * Constructor.
   */
  public IpAddress(CharBuffer cbuf) throws IOException {

    expect(cbuf, BEGIN_ARRAY);

    long len = new UInt8(cbuf).getValue();

    expect(cbuf, COMMA);
    byte[] address = Base64.getDecoder().decode(Strings.decode(cbuf));

    expect(cbuf, END_ARRAY);

    if (len != address.length) {
      throw new IOException("length mismatch");
    }

    this.address = InetAddress.getByAddress(address);
  }

  public IpAddress(InetAddress a) {
    this.address = a;
  }

  public InetAddress get() {
    return address;
  }

  @Override
  public int hashCode() {
    return Objects.hash(address);
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    IpAddress ipAddress = (IpAddress) o;
    return Objects.equals(this.address, ipAddress.address);
  }

  @Override
  public String toString() {
    byte[] address = get().getAddress();

    return BEGIN_ARRAY.toString() + new UInt8(address.length) + COMMA
        + Strings.encode(Base64.getEncoder().encodeToString(address)) + END_ARRAY;
  }
}
