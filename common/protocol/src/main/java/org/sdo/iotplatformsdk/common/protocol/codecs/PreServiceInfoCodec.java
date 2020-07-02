// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.codecs;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfoEntry;

public class PreServiceInfoCodec extends Codec<PreServiceInfo> {

  private static final String PSI_KEY_SEPARATOR = "~";
  private static final String PSI_VALUE_SEPARATOR = ",";

  private final Codec<String> stringCodec = new StringCodec();

  @Override
  public Codec<PreServiceInfo>.Decoder decoder() {
    return new Decoder();
  }

  @Override
  public Codec<PreServiceInfo>.Encoder encoder() {
    return new Encoder();
  }

  private Codec<String> getStringCodec() {
    return stringCodec;
  }

  private class Decoder extends Codec<PreServiceInfo>.Decoder {

    @Override
    public PreServiceInfo apply(CharBuffer in) throws IOException {

      PreServiceInfo result = new PreServiceInfo();
      String encoded = getStringCodec().decoder().apply(in);

      for (String entry : encoded.split(PSI_VALUE_SEPARATOR)) {
        String[] tokens = entry.split(PSI_VALUE_SEPARATOR, 2);
        if (2 == tokens.length) {
          result.add(new PreServiceInfoEntry(tokens[0], tokens[1]));
        }
      }

      return result;
    }
  }

  private class Encoder extends Codec<PreServiceInfo>.Encoder {

    @Override
    public void apply(Writer writer, PreServiceInfo value) throws IOException {

      StringBuilder builder = new StringBuilder();
      String separator = null;

      for (PreServiceInfoEntry entry : value) {

        if (null != separator) {
          builder.append(separator);

        } else {
          separator = PSI_VALUE_SEPARATOR;
        }

        builder.append(entry.getKey().toString());
        builder.append(PSI_KEY_SEPARATOR);
        builder.append(entry.getValue().toString());
      }

      getStringCodec().encoder().apply(writer, builder.toString());
    }
  }
}
