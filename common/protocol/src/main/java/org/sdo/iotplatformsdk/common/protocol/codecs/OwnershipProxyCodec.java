// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.security.cert.CertPath;
import java.util.LinkedList;
import java.util.List;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyHeaderCodec.OwnershipProxyHeaderDecoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyHeaderCodec.OwnershipProxyHeaderEncoder;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;

/**
 * Codec for {@link OwnershipProxy}.
 */
public abstract class OwnershipProxyCodec {

  public static final String HMAC = "hmac";
  public static final String OH = "oh";
  private static final String DC = "dc";
  private static final String EN = "en";
  private static final String SZ = "sz";

  public static class OwnershipProxyDecoder implements SdoDecoder<OwnershipProxy> {

    private final Codec<CertPath>.Decoder dcDec = new CertPathCodec().decoder();
    private final OwnershipProxyHeaderDecoder ohDec = new OwnershipProxyHeaderDecoder();
    private final Codec<Number>.Decoder szDec = new Uint32Codec().decoder();

    private char[] lastDc = new char[0];
    private char[] lastHmac = new char[0];
    private char[] lastOh = new char[0];

    @Override
    public OwnershipProxy decode(final CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(SZ));
      final int sz = szDec.apply(in).intValue();

      expect(in, COMMA);
      expect(in, Json.asKey(OH));
      CharBuffer cbuf = in.asReadOnlyBuffer();
      final OwnershipProxyHeader oh = ohDec.decode(in);
      cbuf.limit(in.position());
      lastOh = new char[cbuf.remaining()];
      cbuf.get(lastOh);

      expect(in, COMMA);
      expect(in, Json.asKey(HMAC));
      cbuf = in.asReadOnlyBuffer();
      final HashMac hmac = new HashMac(in);
      cbuf.limit(in.position());
      lastHmac = new char[cbuf.remaining()];
      cbuf.get(lastHmac);

      CertPath dc;
      in.mark();

      try {
        expect(in, COMMA);
        expect(in, Json.asKey(DC));
        cbuf = in.duplicate();
        dc = dcDec.apply(in);
        cbuf.limit(in.position());
        lastDc = new char[cbuf.remaining()];
        cbuf.get(lastDc);

      } catch (BufferUnderflowException | IOException e) {
        in.reset(); // dc not present in this proxy
        dc = null;
      }

      expect(in, COMMA);
      expect(in, Json.asKey(EN));
      expect(in, BEGIN_ARRAY);

      Character separator = null;
      List<SignatureBlock> en = new LinkedList<>();
      SignatureBlockCodec.Decoder enDec = new SignatureBlockCodec.Decoder(null);

      for (int n = 0; n < sz; n++) {

        if (null != separator) {
          expect(in, separator);

        } else {
          separator = COMMA;
        }

        en.add(enDec.decode(in));
      }

      expect(in, END_ARRAY);
      expect(in, END_OBJECT);

      return new OwnershipProxy(oh, hmac, dc, en);
    }

    public CharBuffer getLastD() {
      return ohDec.getLastD();
    }

    /**
     * Returns the 'dc' tag as {@link CharBuffer}.
     *
     * @return {@link CharBuffer}
     */
    public CharBuffer getLastDc() {
      CharBuffer cbuf = CharBuffer.allocate(lastDc.length);
      cbuf.put(lastDc);
      cbuf.flip();
      return cbuf;
    }

    public CharBuffer getLastG() {
      return ohDec.getLastG();
    }

    /**
     * Returns the 'hmac' tag as {@link CharBuffer}.
     *
     * @return {@link CharBuffer}
     */
    public CharBuffer getLastHmac() {
      CharBuffer cbuf = CharBuffer.allocate(lastHmac.length);
      cbuf.put(lastHmac);
      cbuf.flip();
      return cbuf;
    }

    /**
     * Returns the 'oh' tag as {@link CharBuffer}.
     *
     * @return {@link CharBuffer}
     */
    public CharBuffer getLastOh() {
      CharBuffer cbuf = CharBuffer.allocate(lastOh.length);
      cbuf.put(lastOh);
      cbuf.flip();
      return cbuf;
    }
  }

  public static class OwnershipProxyEncoder implements SdoEncoder<OwnershipProxy> {

    private final Codec<CertPath>.Encoder dcEnc = new CertPathCodec().encoder();
    private final Codec<Number>.Encoder szEnc = new Uint32Codec().encoder();

    @Override
    public void encode(final Writer out, final OwnershipProxy val) throws IOException {

      out.write(BEGIN_OBJECT);

      out.write(Json.asKey(SZ));
      szEnc.apply(out, val.getEn().size());

      out.write(COMMA);
      out.write(Json.asKey(OH));
      new OwnershipProxyHeaderEncoder().encode(out, val.getOh());

      out.write(COMMA);
      out.write(Json.asKey(HMAC));
      out.write(val.getHmac().toString());

      CertPath dc = val.getDc();

      if (null != dc) {
        out.write(COMMA);
        out.write(Json.asKey(DC));
        dcEnc.apply(out, dc);
      }

      out.write(COMMA);
      out.write(Json.asKey(EN));
      out.write(BEGIN_ARRAY);

      Character separator = null;
      SignatureBlockCodec.Encoder enEnc =
          new SignatureBlockCodec.Encoder(new PublicKeyCodec.Encoder(val.getOh().getPe()));

      for (SignatureBlock en : val.getEn()) {

        if (null != separator) {
          out.write(separator);

        } else {
          separator = COMMA;
        }

        enEnc.encode(out, en);
      }

      out.write(END_ARRAY);
      out.write(END_OBJECT);
    }
  }
}
