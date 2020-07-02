// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COLON;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.QUOTE;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.sdo.iotplatformsdk.common.protocol.types.UInt.UInt16;
import org.sdo.iotplatformsdk.common.protocol.types.UInt.UInt32;
import org.sdo.iotplatformsdk.common.protocol.types.UInt.UInt8;

/**
 * SDO "RendezvousInstr" type.
 *
 * <p>A rendezvous instruction is an ordered sequence of key-value pairs. Elements are ordered
 * alphabetically by key. Keys are called 'tag's in the spec. Some keys may be absent, see the SDO
 * Protocol Specification for which ones. Values vary by key, all are SDO Base or Composite Types.
 *
 * <p>Because of the strict ordering and typing, a Map-based implementation is troublesome
 * - you can't use generics because even though types differ, keys (tags) are not
 * arbitrary and any key has only one type of value, but value types are not homogeneous.
 * We must be explicit.
 */
public class RendezvousInstr {

  private static final String KEY_DELAY = "delaysec";
  private static final String KEY_DN = "dn";
  private static final String KEY_IP = "ip";
  private static final String KEY_ONLY = "only";
  private static final String KEY_PO = "po";
  private static final String KEY_POW = "pow";
  private static final String KEY_PR = "pr";

  private Duration delay = null;
  private String dn = null;
  private InetAddress ip = null;
  private Only only = null;
  private UInt16 po = null;
  private UInt16 pow = null;
  private Protocol pr = null;

  public RendezvousInstr() {}

  /**
   * Constructor.
   */
  public RendezvousInstr(URI uri) {

    this.setPr(Protocol.valueOfName(uri.getScheme()));

    String host = uri.getHost();
    if (isIpAddress(host)) {

      try {
        this.setIp(InetAddress.getByName(host));

      } catch (UnknownHostException e) {
        this.setDn(host);
      }

    } else {
      this.setDn(host);
    }

    int port = uri.getPort();
    if (port < 0) {
      try {
        port = uri.toURL().getDefaultPort();

      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }

    UInt16 u16 = new UInt16(port);
    this.setPo(u16);
    this.setPow(u16);

    String queries = uri.getQuery();

    if (null != queries) {
      for (String query : uri.getQuery().split("&")) {

        final int ix = query.indexOf("=");
        String key;
        String value = null;

        if (ix > 0) {
          key = query.substring(0, ix);

          if (ix + 1 < query.length()) {
            value = query.substring(ix + 1);
          }

        } else {
          key = query;
        }

        switch (key) {
          case KEY_ONLY:
            this.setOnly(Only.valueOf(value));
            break;

          case KEY_DELAY:
            if (null != value) {
              this.setDelay(Duration.ofSeconds(new UInt32(CharBuffer.wrap(value)).getValue()));
            } else {
              throw new IllegalArgumentException(KEY_DELAY + " requires a value");
            }
            break;

          default:
            throw new IllegalArgumentException("unknown key: " + key);
        }
      }
    }
  }

  /**
   * Constructor.
   */
  public RendezvousInstr(final CharBuffer cbuf) throws IOException {

    expect(cbuf, BEGIN_ARRAY);
    final long expectedLength = new UInt8(cbuf).getValue();

    expect(cbuf, COMMA);
    expect(cbuf, BEGIN_OBJECT);
    int actualLength = 0;

    if (matchKey(cbuf, separator(actualLength), KEY_DELAY)) {
      setDelay(Duration.ofSeconds(new UInt32(cbuf).getValue()));
      actualLength++;
    }

    if (matchKey(cbuf, separator(actualLength), KEY_DN)) {
      setDn(Strings.decode(cbuf));
      actualLength++;
    }

    if (matchKey(cbuf, separator(actualLength), KEY_IP)) {
      setIp(new IpAddress(cbuf).get());
      actualLength++;
    }

    if (matchKey(cbuf, separator(actualLength), KEY_ONLY)) {
      setOnly(Only.valueOf(Strings.decode(cbuf)));
      actualLength++;

    }

    if (matchKey(cbuf, separator(actualLength), KEY_PO)) {
      setPo(new UInt16(cbuf));
      actualLength++;
    }

    if (matchKey(cbuf, separator(actualLength), KEY_POW)) {
      setPow(new UInt16(cbuf));
      actualLength++;
    }

    if (matchKey(cbuf, separator(actualLength), KEY_PR)) {
      setPr(Protocol.valueOfName(Strings.decode(cbuf)));
      actualLength++;
    }

    expect(cbuf, END_OBJECT);
    expect(cbuf, END_ARRAY);

    if (expectedLength != actualLength) {
      throw new IOException("expected " + expectedLength + " values but found " + actualLength);
    }
  }

  /**
   * Multi-arg constructor clones another {@link RendezvousInstr} instance.
   */
  public RendezvousInstr(RendezvousInstr that) {
    this.setDelay(that.getDelay());
    this.setDn(that.getDn());
    this.setIp(that.getIp());
    this.setOnly(that.getOnly());
    this.setPo(that.getPo());
    this.setPow(that.getPow());
    this.setPr(that.getPr());
  }

  private static String asJsonKey(String s) {
    return QUOTE + s + QUOTE + COLON;
  }

  private static boolean isIpAddress(String host) {
    return isIpV4Address(host) || isIpV6Address(host);
  }

  private static boolean isIpV4Address(String host) {

    for (char c : host.toCharArray()) {
      if (c != '.' && Character.digit(c, 10) < 0) {
        return false;
      }
    }

    return true;
  }

  private static boolean isIpV6Address(String host) {
    for (char c : host.toCharArray()) {
      if (c != ':' && Character.digit(c, 16) < 0) {
        return false;
      }
    }

    return true;
  }

  private static boolean matchKey(CharBuffer cbuf, String separator, String key) {

    cbuf.mark();

    try {
      expect(cbuf, separator);
      expect(cbuf, asJsonKey(key));
      return true;

    } catch (IOException e) {
      cbuf.reset();
      return false;
    }
  }

  private static String separator(int index) {
    return 0 == index ? "" : COMMA.toString();
  }

  /**
   * TLS CA certificate hash.
   *
   * <p>This has never been needed, and is not implemented.
   */
  @SuppressWarnings("unused")
  public HashDigest getCch() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public void setCch(final HashDigest cch) {
    throw new UnsupportedOperationException();
  }

  /**
   * Post-instruction delay.
   */
  public Duration getDelay() {
    return delay;
  }

  /**
   * Set the delay.
   *
   * @param delay the input delay represented by {@link Duration}
   */
  public void setDelay(final Duration delay) {
    if (null != delay) {
      this.delay = Duration.ofSeconds(new UInt32(delay.toSeconds()).getValue());
    } else {
      this.delay = null;
    }
  }

  /**
   * Server DNS name.
   */
  public String getDn() {
    return dn;
  }

  public void setDn(final String dn) {
    this.dn = dn;
  }

  /**
   * Server IP address.
   *
   * <p>This is a fallback, used if DN is empty, fails DN lookup, or DN lookup produces
   * an unreachable address.
   */
  public InetAddress getIp() {
    return ip;
  }

  public void setIp(final InetAddress ip) {
    this.ip = ip;
  }

  /**
   * Device medium.
   *
   * <p>The SDO Protocol Specification provides a list of valid values. All appear to be ethernet
   * device specifiers.
   *
   * <p>This has never been needed, and is not implemented.
   */
  @SuppressWarnings("unused")
  public String getMe() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public void setMe(final String me) {
    throw new UnsupportedOperationException();
  }

  /**
   * A flag identifying device-only or owner-only instructions.
   */
  public Only getOnly() {
    return only;
  }

  public void setOnly(final Only only) {
    this.only = only;
  }

  /**
   * Rendezvous port for device-only instructions.
   *
   * @see #getPow
   */
  public Integer getPo() {

    if (null != po) {
      return Long.valueOf(po.getValue()).intValue();
    } else {
      return null;
    }
  }

  /**
   * Set the 'po' tag.
   *
   * @param po the integer po value
   */
  public void setPo(final Integer po) {

    if (null != po) {
      setPo(new UInt16(po));

    } else {
      this.po = null;
    }
  }

  void setPo(final UInt16 po) {
    this.po = new UInt16(po);
  }

  /**
   * Rendezvous port for device-only instructions.
   *
   * <p>Must only be used if only == owner.
   *
   * @see #getPo
   */
  public Integer getPow() {

    if (null != pow) {
      return Long.valueOf(pow.getValue()).intValue();
    } else {
      return null;
    }
  }

  /**
   * Set the 'pow' tag.
   *
   * @param pow the integer pow value
   */
  public void setPow(final Integer pow) {

    if (null != pow) {
      setPow(new UInt16(pow));
    } else {
      this.pow = null;
    }
  }

  void setPow(UInt16 pow) {
    this.pow = pow;
  }

  /**
   * Rendezvous network protocol.
   *
   * <p>The SDO Protocol Specification provides a list of valid values.
   */
  public Protocol getPr() {
    return pr;
  }

  public void setPr(final Protocol pr) {
    this.pr = pr;
  }

  /**
   * Wireless password.
   *
   * <p>This has never been needed, and is not implemented.
   */
  @SuppressWarnings("unused")
  public String getPw() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public void setPw(final String pw) {
    throw new UnsupportedOperationException();
  }

  /**
   * TLS Server certificate hash.
   *
   * <p>This has never been needed, and is not implemented.
   */
  @SuppressWarnings("unused")
  public HashDigest getSch() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public void setSch(final HashDigest sch) {
    throw new UnsupportedOperationException();
  }

  /**
   * Wireless SSID.
   *
   * <p>This has never been needed, and is not implemented.
   */
  @SuppressWarnings("unused")
  public String getSs() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public void setSs(final String ss) {
    throw new UnsupportedOperationException();
  }

  /**
   * User-input flag.
   *
   * <p>The SDO Protocol Specification provides details of this flag.
   *
   * <p>This has never been needed, and is not implemented.
   */
  @SuppressWarnings("unused")
  public boolean getUi() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public void setUi(final boolean ui) {
    throw new UnsupportedOperationException();
  }

  /**
   * Wireless security password.
   *
   * <p>This has never been needed, and is not implemented. The difference between 'wp' and 'wsp' is
   * not clear.
   */
  @SuppressWarnings("unused")
  public String getWsp() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  public void setWsp(final String wsp) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    return Objects.hash(delay, dn, ip, only, po, pow, pr);
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RendezvousInstr that = (RendezvousInstr) o;
    return Objects.equals(delay, that.delay) && Objects.equals(dn, that.dn)
        && Objects.equals(ip, that.ip) && Objects.equals(only, that.only)
        && (Objects.equals(only, Only.dev) && Objects.equals(po, that.po)
            || Objects.equals(only, Only.owner) && Objects.equals(pow, that.pow))
        && Objects.equals(pr, that.pr);
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();

    // Build the string from the inside out, because the list must be prefixed
    // by its own length.
    int length = 0;

    builder.append(BEGIN_OBJECT);

    if (null != getDelay()) {

      builder.append(separator(length)).append(asJsonKey(KEY_DELAY))
          .append(new UInt32(getDelay().getSeconds()));
      ++length;
    }

    if (null != getDn()) {

      builder.append(separator(length)).append(asJsonKey(KEY_DN)).append(Strings.encode(getDn()));
      ++length;
    }

    if (null != getIp()) {

      builder.append(separator(length)).append(asJsonKey(KEY_IP)).append(new IpAddress(getIp()));
      ++length;
    }

    if (null != getOnly()) {

      builder.append(separator(length)).append(asJsonKey(KEY_ONLY))
          .append(Strings.encode(getOnly().toString()));
      ++length;

    }

    if (Only.owner != getOnly() && null != getPo()) {

      builder.append(separator(length)).append(asJsonKey(KEY_PO)).append(getPo());
      ++length;

    }

    if (Only.dev != getOnly() && null != getPow()) {

      builder.append(separator(length)).append(asJsonKey(KEY_POW)).append(getPow());
      ++length;

    }

    if (null != getPr()) {

      builder.append(separator(length)).append(asJsonKey(KEY_PR))
          .append(Strings.encode(getPr().getName()));
      ++length;
    }

    builder.append(END_OBJECT);

    return BEGIN_ARRAY.toString() + new UInt8(length) + COMMA + builder.toString() + END_ARRAY;
  }

  /**
   * Returns the URIs to which this instruction expands.
   *
   * <p>All the callers of this service don't want the query. They'd have to strip
   * it off if they got it. Leave the query out of the URI.
   *
   * @throws URISyntaxException thrown when such an Exception occurs
   */
  @SuppressWarnings("unchecked")
  public Collection<URI> toUris(Only onlyFor) throws URISyntaxException {

    final String host;
    if (null != getDn() && !getDn().isEmpty()) {
      host = getDn();

    } else if (null != getIp()) {
      host = getIp().getHostAddress();

    } else {
      // No host and no IP: delay (or other metadata) instruction.
      // This will not produce any valid URIs.
      return Collections.EMPTY_LIST;
    }

    final List<URI> uris = new ArrayList<>();
    for (String scheme : getPr().getSchemes()) {

      if ((Only.owner != onlyFor) && (Only.owner != getOnly())) {
        uris.add(new URI(scheme, null, host, toPort(getPo()), null, null, null));
      }
      if ((Only.dev != onlyFor) && (Only.dev != getOnly())) {
        uris.add(new URI(scheme, null, host, toPort(getPow()), null, null, null));
      }

    }
    return Collections.unmodifiableList(uris);
  }

  private int toPort(Integer i) {
    if (null != i && -1 < i) {
      return i;
    } else {
      return -1;
    }
  }

  // This enum has lowercase values so we can use native toString().
  public enum Only {
    dev,
    owner
  }

  public enum Protocol {
    REST("rest", List.of("https", "http")),
    HTTP("http", List.of("http")),
    HTTPS("https", List.of("https"));

    private final String name;
    private final List<String> schemes;

    private Protocol(String name, List<String> schemes) {
      this.name = name;
      this.schemes = Collections.unmodifiableList(schemes);
    }

    /**
     * Utility method to return the {@link Protocol} corresponding to the input name.
     * @param name the input name
     * @return {@link Protocol}
     */
    public static Protocol valueOfName(String name) {
      for (Protocol candidate : values()) {
        if (candidate.getName().contentEquals(name)) {
          return candidate;
        }
      }

      throw new IllegalArgumentException(); // no match
    }

    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return getName();
    }

    List<String> getSchemes() {
      return schemes;
    }
  }
}
