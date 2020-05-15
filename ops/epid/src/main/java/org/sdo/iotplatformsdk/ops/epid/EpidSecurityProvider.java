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

package org.sdo.iotplatformsdk.ops.epid;

import java.io.IOException;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;

import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactory;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class EpidSecurityProvider extends Provider {

  public static final String ALGORITHM = "EPID";
  public static final String PROVIDER_INFO =
      "https://software.intel.com/en-us/articles/intel-enhanced-privacy-id-epid-security-technology";
  public static final String PROVIDER_NAME = "EPID";
  private static HttpClient httpClient;
  private static String epidOnlineHostUrl;
  private static final Logger mlog = LoggerFactory.getLogger(EpidLib.class);
  private static EpidLib epidLib;

  @SuppressWarnings("deprecation")
  private EpidSecurityProvider() throws NoSuchAlgorithmException {
    super(PROVIDER_NAME, 1.0, PROVIDER_INFO);
    httpClient = HttpClient.newBuilder()
        .sslContext(new SslContextFactory(new SecureRandomFactory().getObject()).getObject())
        .build();
    putService(new Provider.Service(this, "Signature", ALGORITHM,
        EpidSignatureSpi.class.getCanonicalName(),
        Arrays.asList("EPIDV1_0", "EPIDV1_1", "EPIDV2_0"), null));
  }

  /**
   * Return an {@link EpidLib} instance.
   *
   * @return an instance of EpidLib
   * @throws IOException when an exception occurs
   */
  public static EpidLib getEpidLib() throws IOException {
    if (epidLib == null) {
      epidLib = new EpidLib();
    }
    return epidLib;
  }

  /**
   * If epid backend url specified then overwrite default. testMode sets the url to
   * the sandbox but testmode is only applicable if host url not specified.
   *
   * @param url      epid online url
   * @param testMode whether epid test urls is to be used
   */
  public static void setEpidOptions(String url, boolean testMode) {
    if (null != url && !url.isEmpty()) {
      epidOnlineHostUrl = url;
    } else if (testMode) {
      epidOnlineHostUrl = EpidConstants.sandboxEpidUrlDefault;
      mlog.warn("***");
      mlog.warn("Epid test use has been enabled.");
      mlog.warn("This should only be used in test and development environments.");
      mlog.warn("If this is a production environment then you should update");
      mlog.warn("the configuration to set test mode to false.");
      mlog.warn("***");
    } else {
      // no host url specified and testMode is disabled so go to the default
      epidOnlineHostUrl = EpidConstants.onlineEpidUrlDefault;
    }
    mlog.debug("Epid URL set to: " + epidOnlineHostUrl);
  }

  public static String getEpidOnlineHostUrl() {
    return epidOnlineHostUrl;
  }

  /**
   * Loads the EPID JCE provider.
   *
   * <p>This method is idempotent, and may be called repeatedly without side effect.
   *
   * @return The EPID JCE provider.
   * @throws NoSuchAlgorithmException when exception is thrown.
   */
  public static Provider load() throws NoSuchAlgorithmException {
    Provider provider = Security.getProvider(PROVIDER_NAME);

    if (null == provider) {
      provider = new EpidSecurityProvider();
      Security.addProvider(provider);
    }
    return provider;
  }

}
