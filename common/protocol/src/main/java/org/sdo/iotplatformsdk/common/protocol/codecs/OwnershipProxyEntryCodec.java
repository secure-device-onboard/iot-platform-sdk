// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_OBJECT;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.security.PublicKey;
import org.sdo.iotplatformsdk.common.protocol.types.HashDigest;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxyEntry;

/**
 * Codec for {@link OwnershipProxyEntry}.
 */
public class OwnershipProxyEntryCodec {

  private static final String HC = "hc";
  private static final String HP = "hp";
  private static final String PK = "pk";

  public static class Decoder implements SdoDecoder<OwnershipProxyEntry> {

    @Override
    public OwnershipProxyEntry decode(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(HP));
      final HashDigest hp = new HashDigest(in);

      expect(in, COMMA);
      expect(in, Json.asKey(HC));
      final HashDigest hc = new HashDigest(in);

      expect(in, COMMA);
      expect(in, Json.asKey(PK));
      final PublicKey pk = new PublicKeyCodec.Decoder().decode(in);

      expect(in, END_OBJECT);

      return new OwnershipProxyEntry(hp, hc, pk);
    }
  }

  public static class Encoder implements SdoEncoder<OwnershipProxyEntry> {

    private final PublicKeyCodec.Encoder pkEncoder;

    public Encoder(PublicKeyCodec.Encoder pkEncoder) {
      this.pkEncoder = pkEncoder;
    }

    @Override
    public void encode(Writer writer, OwnershipProxyEntry value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(HP));
      writer.write(value.getHp().toString());

      writer.write(COMMA);
      writer.write(Json.asKey(HC));
      writer.write(value.getHc().toString());

      writer.write(COMMA);
      writer.write(Json.asKey(PK));
      pkEncoder.encode(writer, value.getPk());

      writer.write(END_OBJECT);
    }
  }
}
