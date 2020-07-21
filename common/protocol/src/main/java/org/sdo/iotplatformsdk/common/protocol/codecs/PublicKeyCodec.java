// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.BEGIN_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.COMMA;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Json.END_ARRAY;
import static org.sdo.iotplatformsdk.common.protocol.codecs.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.security.PublicKey;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey10;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey11;
import org.sdo.iotplatformsdk.common.protocol.types.EpidKey20;
import org.sdo.iotplatformsdk.common.protocol.types.KeyEncoding;
import org.sdo.iotplatformsdk.common.protocol.types.KeyType;
import org.sdo.iotplatformsdk.common.protocol.types.Keys;

/**
 * Codec for {@link PublicKey}, represented in SDO as the 'PublicKey' type.
 *
 * @see "SDO Protocol Specification, 1.13b, 3.2: Composite Types"
 */
public class PublicKeyCodec {

  public static class Decoder implements SdoDecoder<PublicKey> {

    @Override
    public PublicKey decode(CharBuffer in) throws IOException {

      expect(in, BEGIN_ARRAY);
      KeyType pkType = new KeyTypeCodec().decoder().apply(in);

      expect(in, COMMA);
      KeyEncoding pkEnc = new KeyEncodingCodec().decoder().apply(in);

      expect(in, COMMA);
      PublicKey key;

      switch (pkEnc) {

        case NONE:
          key = new PkNullCodec().decoder().apply(in);
          break;

        case X_509:
          key = new PkX509Codec().decoder().apply(in);
          break;

        case RSAMODEXP:
          key = new PkRmeCodec().decoder().apply(in);
          break;

        case ONDIE_ECDSA:
          key = new PkOnDieEcdsaCodec().decoder().apply(in);
          break;

        case EPID:
          byte[] keyBytes = new PkEpidCodec().decoder().apply(in).getEncoded();
          switch (pkType) {
            case EPIDV1_0:
              key = new EpidKey10(keyBytes);
              break;
            case EPIDV1_1:
              key = new EpidKey11(keyBytes);
              break;
            case EPIDV2_0:
              key = new EpidKey20(keyBytes);
              break;
            default:
              throw new UnsupportedOperationException(pkType.name());
          }
          break;

        default:
          throw new UnsupportedOperationException(pkEnc.toString());
      }

      expect(in, END_ARRAY);

      return key;
    }
  }

  public static class Encoder implements SdoEncoder<PublicKey> {

    private final KeyEncoding pe;

    public Encoder(KeyEncoding pe) {
      this.pe = pe;
    }

    @Override
    public void encode(Writer writer, PublicKey value) throws IOException {

      final KeyEncoding keyEncoding = null == value ? KeyEncoding.NONE : getPe();
      writer.write(BEGIN_ARRAY);
      final KeyType keyType = Keys.toType(value);
      new KeyTypeCodec().encoder().apply(writer, keyType);

      // Key type and key encoding have a 1:1 relationship - for each key type there is
      // only one valid encoding and vice versa.
      writer.write(COMMA);
      new KeyEncodingCodec().encoder().apply(writer, keyEncoding);

      writer.write(COMMA);

      switch (keyEncoding) {

        case NONE:
          new PkNullCodec().encoder().apply(writer, value);
          break;

        case X_509:
          new PkX509Codec().encoder().apply(writer, value);
          break;

        case RSAMODEXP:
          new PkRmeCodec().encoder().apply(writer, value);
          break;

        case ONDIE_ECDSA:
          new PkOnDieEcdsaCodec().encoder().apply(writer, value);
          break;

        case EPID:
          new PkEpidCodec().encoder().apply(writer, value);
          break;

        default:
          throw new UnsupportedOperationException(keyEncoding.toString());
      }

      writer.write(END_ARRAY);
    }

    private KeyEncoding getPe() {
      return pe;
    }

  }
}
