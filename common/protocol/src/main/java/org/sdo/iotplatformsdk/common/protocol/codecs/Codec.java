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

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * Prefer {@link SdoDecoder}, {@link SdoEncoder}.
 */
public abstract class Codec<T> {

  public abstract Decoder decoder();

  public abstract Encoder encoder();

  public abstract class Decoder {

    // Decoders and Encoders are asymmetric because decoding involves computing hashes
    // and signatures based on back-references into the original encoded version of the data.
    //
    // It would be possible to wrap such things in a fancy Reader, but
    // such a symmetric implementation would sacrifice much in clarity.
    // CharBuffers make for a clumsier design, but clearer code.
    public abstract T apply(CharBuffer in) throws IOException;
  }

  public abstract class Encoder {

    public abstract void apply(Writer out, T value) throws IOException;
  }
}
