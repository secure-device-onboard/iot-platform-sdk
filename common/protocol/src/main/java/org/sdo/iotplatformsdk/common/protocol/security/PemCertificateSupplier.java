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
import java.security.cert.Certificate;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemCertificateSupplier implements CertificateSupplier {

  private static final Logger LOG = LoggerFactory.getLogger(PemPrivateKeySupplier.class);

  private final URL url;

  public PemCertificateSupplier(URL url) {
    this.url = url;
  }

  @Override
  public Certificate get() {

    Certificate cert = null;
    URL url = getUrl();

    if (null != url) {
      try (InputStream instream = url.openStream()) {
        if (null != instream) {
          try (InputStreamReader reader = new InputStreamReader(instream);
              PEMParser pem = new PEMParser(reader)) {

            for (Object o = pem.readObject(); null == cert && null != o; o = pem.readObject()) {

              if (o instanceof X509CertificateHolder) {

                X509CertificateHolder holder = (X509CertificateHolder) o;
                JcaX509CertificateConverter cc = new JcaX509CertificateConverter();
                cc.setProvider(new BouncyCastleSupplier().get());
                cert = cc.getCertificate(holder);
              }
            } // foreach PEM object
          } // try-with-reader
        } // if non-null instream

      } catch (Throwable t) {
        LOG.debug(t.getMessage(), t);
      } // try-with-input stream
    } // if non-null url

    return cert;
  }

  private URL getUrl() {
    return this.url;
  }
}
