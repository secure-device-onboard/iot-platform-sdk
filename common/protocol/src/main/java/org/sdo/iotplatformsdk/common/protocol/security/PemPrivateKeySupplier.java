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

package org.sdo.iotplatformsdk.common.protocol.security;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PrivateKey;
import java.util.function.Supplier;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemPrivateKeySupplier implements Supplier<PrivateKey> {

  private static final Logger LOG = LoggerFactory.getLogger(PemPrivateKeySupplier.class);

  private final URL url;

  public PemPrivateKeySupplier(URL url) {
    this.url = url;
  }

  /**
   * Return the {@link PrivateKey}.
   *
   * <p>It is not OK to keep PrivateKeys in RAM, so load from storage every time.
   *
   * @return the {@link PrivateKey}.
   */
  @Override
  public PrivateKey get() {

    PrivateKey key = null;
    URL url = getUrl();

    if (null != url) {
      try (InputStream in = url.openStream();
          PEMParser pem = new PEMParser(new InputStreamReader(in))) {

        PrivateKeyInfo privateKeyInfo = null;

        for (Object o = pem.readObject(); null != o && null == privateKeyInfo; o =
            pem.readObject()) {

          if (o instanceof PrivateKeyInfo) {
            privateKeyInfo = (PrivateKeyInfo) o;

          } else if (o instanceof PEMKeyPair) {
            privateKeyInfo = ((PEMKeyPair) o).getPrivateKeyInfo();
          }
        }

        if (null != privateKeyInfo) {
          key = new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
        }

      } catch (Throwable t) {
        LOG.debug(t.getMessage(), t);
      } // try-with-resources
    } // if non-null url

    return key;
  }

  private URL getUrl() {
    return this.url;
  }
}
