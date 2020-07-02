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
import org.sdo.iotplatformsdk.common.protocol.types.CipherText;
import org.sdo.iotplatformsdk.common.protocol.types.EncryptedMessage;
import org.sdo.iotplatformsdk.common.protocol.types.To2CipherHashMac;

/**
 * Codec for {@link EncryptedMessage}.
 */
public class EncryptedMessageCodec extends Codec<EncryptedMessage> {

  private static final String CT = "ct";
  private static final String HMAC = "hmac";

  private final CipherTextCodec ctCodec = new CipherTextCodec();

  @Override
  public Codec<EncryptedMessage>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<EncryptedMessage>.Encoder encoder() {
    return new Encoder();
  }

  private CipherTextCodec getCtCodec() {
    return ctCodec;
  }

  private class Decoder extends Codec<EncryptedMessage>.Decoder {

    @Override
    public EncryptedMessage apply(CharBuffer in) throws IOException {

      expect(in, BEGIN_OBJECT);

      expect(in, Json.asKey(CT));
      final CipherText ct = getCtCodec().decoder().apply(in);

      expect(in, COMMA);
      expect(in, Json.asKey(HMAC));
      final To2CipherHashMac hmac = new To2CipherHashMac(in);

      expect(in, END_OBJECT);

      return new EncryptedMessage(ct, hmac);
    }
  }

  private class Encoder extends Codec<EncryptedMessage>.Encoder {

    @Override
    public void apply(Writer writer, EncryptedMessage value) throws IOException {

      writer.write(BEGIN_OBJECT);

      writer.write(Json.asKey(CT));
      getCtCodec().encoder().apply(writer, value.getCt());

      writer.write(COMMA);
      writer.write(Json.asKey(HMAC));
      writer.write(value.getHmac().toString());

      writer.write(END_OBJECT);
    }
  }
}
