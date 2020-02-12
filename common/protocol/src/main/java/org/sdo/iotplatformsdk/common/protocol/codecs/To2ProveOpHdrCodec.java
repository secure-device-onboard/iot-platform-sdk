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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyHeaderCodec.OwnershipProxyHeaderDecoder;
import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyHeaderCodec.OwnershipProxyHeaderEncoder;
import org.sdo.iotplatformsdk.common.protocol.types.HashMac;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyHeader;
import org.sdo.iotplatformsdk.common.protocol.types.SigInfo;
import org.sdo.iotplatformsdk.common.protocol.types.To2ProveOpHdr;

public class To2ProveOpHdrCodec extends Codec<To2ProveOpHdr> {

  private static String EB = "eB";
  private static String HMAC = "hmac";
  private static String N5 = "n5";
  private static String N6 = "n6";
  private static String OH = "oh";
  private static String SZ = "sz";
  private static String XA = "xA";
  private final Decoder decoder = new Decoder();
  private final Codec<SigInfo> ebCodec = new SigInfoCodec();
  private final Encoder encoder = new Encoder();
  private final Codec<Number> szCodec = new Uint32Codec();
  private final Codec<ByteBuffer> xaCodec = new KexParamCodec();

  @Override
  public Codec<To2ProveOpHdr>.Decoder decoder() {
    return getDecoder();
  }

  @Override
  public Codec<To2ProveOpHdr>.Encoder encoder() {
    return getEncoder();
  }

  public Decoder getDecoder() {
    return decoder;
  }

  public Encoder getEncoder() {
    return encoder;
  }

  private Codec<SigInfo> getEbCodec() {
    return ebCodec;
  }

  private Codec<Number> getSzCodec() {
    return szCodec;
  }

  private Codec<ByteBuffer> getXaCodec() {
    return xaCodec;
  }

  public class Decoder extends Codec<To2ProveOpHdr>.Decoder {

    private char[] lastHmac = new char[0];
    private char[] lastOh = new char[0];

    private final OwnershipProxyHeaderDecoder ohDec = new OwnershipProxyHeaderDecoder();

    @Override
    public To2ProveOpHdr apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(SZ));
      final Number sz = getSzCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(OH));
      CharBuffer ohBuf = in.asReadOnlyBuffer();
      final OwnershipProxyHeader oh = ohDec.decode(in);
      ohBuf.limit(in.position());
      setLastOh(ohBuf.asReadOnlyBuffer());

      expect(in, COMMA);
      expect(in, Json.asKey(HMAC));
      CharBuffer hmacBuf = in.asReadOnlyBuffer();
      final HashMac hmac = new HashMac(in);
      hmacBuf.limit(in.position());
      setLastHmac(hmacBuf.asReadOnlyBuffer());

      expect(in, COMMA);
      expect(in, Json.asKey(N5));
      final Nonce n5 = new Nonce(in);

      expect(in, COMMA);
      expect(in, Json.asKey(N6));
      final Nonce n6 = new Nonce(in);

      expect(in, COMMA);
      expect(in, Json.asKey(EB));
      final SigInfo eb = getEbCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(XA));
      final ByteBuffer xa = getXaCodec().decoder().apply(in);

      expect(in, END_OBJECT);

      return new To2ProveOpHdr(sz.intValue(), oh, hmac, n5, n6, eb, xa);
    }

    public CharBuffer getLastD() {
      return ohDec.getLastD();
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
      CharBuffer dst = CharBuffer.allocate(lastHmac.length);
      dst.put(lastHmac);
      dst.flip();
      return dst;
    }

    private void setLastHmac(final CharBuffer src) {
      final char[] chars = new char[src.remaining()];
      src.get(chars);
      this.lastHmac = chars;
    }

    /**
     * Returns the 'oh' tag as {@link CharBuffer}.
     *
     * @return {@link CharBuffer}
     */
    public CharBuffer getLastOh() {
      CharBuffer dst = CharBuffer.allocate(lastOh.length);
      dst.put(lastOh);
      dst.flip();
      return dst;
    }

    private void setLastOh(final CharBuffer src) {
      final char[] chars = new char[src.remaining()];
      src.get(chars);
      this.lastOh = chars;
    }
  }

  public class Encoder extends Codec<To2ProveOpHdr>.Encoder {

    @Override
    public void apply(Writer writer, To2ProveOpHdr value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(SZ));
      getSzCodec().encoder().apply(writer, value.getSz());

      writer.write(COMMA);
      writer.write(Json.asKey(OH));
      new OwnershipProxyHeaderEncoder().encode(writer, value.getOh());

      writer.write(COMMA);
      writer.write(Json.asKey(HMAC));
      writer.write(value.getHmac().toString());

      writer.write(COMMA);
      writer.write(Json.asKey(N5));
      writer.write(value.getN5().toString());

      writer.write(COMMA);
      writer.write(Json.asKey(N6));
      writer.write(value.getN6().toString());

      writer.write(COMMA);
      writer.write(Json.asKey(EB));
      getEbCodec().encoder().apply(writer, value.getEb());

      writer.write(COMMA);
      writer.write(Json.asKey(XA));
      getXaCodec().encoder().apply(writer, value.getXa());

      writer.write(END_OBJECT);
    }
  }
}
