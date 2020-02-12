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

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.security.PublicKey;
import java.util.UUID;

import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;
import org.sdo.iotplatformsdk.common.protocol.types.KeyEncoding;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;
import org.sdo.iotplatformsdk.common.protocol.types.Version;

/**
 * Codec for {@link OwnershipProxyHeader}.
 */
public abstract class OwnershipProxyHeaderCodec {

  private static final String D = "d";
  private static final String G = "g";
  private static final String HDC = "hdc";
  private static final String PE = "pe";
  private static final String PK = "pk";
  private static final String PV = "pv";
  private static final String R = "r";

  public static class OwnershipProxyHeaderDecoder implements SdoDecoder<OwnershipProxyHeader> {

    private char[] lastD = new char[0];
    private char[] lastG = new char[0];
    private char[] lastPk = new char[0];

    @Override
    public OwnershipProxyHeader decode(final CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);
      expect(in, Json.asKey(PV));
      final Version pv = Version.valueOfInt(new Uint32Codec().decoder().apply(in).intValue());
      if (OwnershipProxyHeader.THIS_VERSION.intValue() != pv.intValue()) {
        // version mismatch, we can't parse this.
        throw new IOException("version mismatch, pv = " + pv);
      }

      expect(in, COMMA);
      expect(in, Json.asKey(PE));
      final KeyEncoding pe = new KeyEncodingCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(R));
      final RendezvousInfo r = new RendezvousInfoCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(G));
      CharBuffer gbuf = in.asReadOnlyBuffer();
      final UUID g = new UuidCodec().decoder().apply(in);
      gbuf.limit(in.position());
      setLastG(gbuf);

      expect(in, COMMA);
      expect(in, Json.asKey(D));
      CharBuffer dbuf = in.asReadOnlyBuffer();
      final String d = new StringCodec().decoder().apply(in);
      dbuf.limit(in.position());
      setLastD(dbuf);

      expect(in, COMMA);
      expect(in, Json.asKey(PK));
      CharBuffer pkBuf = in.asReadOnlyBuffer();
      final PublicKey pk = new PublicKeyCodec.Decoder().decode(in);
      pkBuf.limit(in.position());
      setLastPk(pkBuf);

      HashDigest hdc;
      in.mark();

      try {
        expect(in, COMMA);
        expect(in, Json.asKey(HDC));
        hdc = new HashDigest(in);

      } catch (BufferUnderflowException | IOException e) {
        in.reset(); // hdc not present in this header
        hdc = null;
      }

      expect(in, END_OBJECT);

      return new OwnershipProxyHeader(pe, r, g, d, pk, hdc);
    }

    /**
     * Returns the 'd' tag as {@link CharBuffer}.
     *
     * @return {@link CharBuffer}
     */
    public CharBuffer getLastD() {
      CharBuffer dst = CharBuffer.allocate(lastD.length);
      dst.put(lastD);
      dst.flip();
      return dst;
    }

    private void setLastD(final CharBuffer src) {
      final char[] chars = new char[src.remaining()];
      src.get(chars);
      this.lastD = chars;
    }

    /**
     * Returns the 'g' tag as {@link CharBuffer}.
     *
     * @return {@link CharBuffer}
     */
    public CharBuffer getLastG() {
      CharBuffer dst = CharBuffer.allocate(lastG.length);
      dst.put(lastG);
      dst.flip();
      return dst;
    }

    private void setLastG(final CharBuffer src) {
      final char[] chars = new char[src.remaining()];
      src.get(chars);
      this.lastG = chars;
    }

    /**
     * Returns the 'pk' tag as {@link CharBuffer}.
     *
     * @return {@link CharBuffer}
     */
    public CharBuffer getLastPk() {
      CharBuffer dst = CharBuffer.allocate(lastPk.length);
      dst.put(lastPk);
      dst.flip();
      return dst;
    }

    private void setLastPk(final CharBuffer src) {
      final char[] chars = new char[src.remaining()];
      src.get(chars);
      this.lastPk = chars;
    }
  }

  public static class OwnershipProxyHeaderEncoder implements SdoEncoder<OwnershipProxyHeader> {

    @Override
    public void encode(Writer out, OwnershipProxyHeader val) throws IOException {

      out.write(BEGIN_OBJECT);

      out.write(Json.asKey(PV));
      new Uint32Codec().encoder().apply(out, val.getPv().intValue());

      out.write(COMMA);
      out.write(Json.asKey(PE));
      new KeyEncodingCodec().encoder().apply(out, val.getPe());

      out.write(COMMA);
      out.write(Json.asKey(R));
      new RendezvousInfoCodec().encoder().apply(out, val.getR());

      out.write(COMMA);
      out.write(Json.asKey(G));
      new UuidCodec().encoder().apply(out, val.getG());

      out.write(COMMA);
      out.write(Json.asKey(D));
      new StringCodec().encoder().apply(out, val.getD());

      out.write(COMMA);
      out.write(Json.asKey(PK));
      new PublicKeyCodec.Encoder(val.getPe()).encode(out, val.getPk());

      HashDigest hdc = val.getHdc();

      if (null != hdc) {
        out.write(COMMA);
        out.write(Json.asKey(HDC));
        out.write(hdc.toString());
      }

      out.write(END_OBJECT);
    }
  }
}
