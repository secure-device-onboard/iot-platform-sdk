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

package org.sdo.iotplatformsdk.to0scheduler.to0client;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec.OwnershipProxyDecoder;
import org.sdo.iotplatformsdk.common.protocol.config.SdoSpringProperties;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0Client;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class To0ClientApp implements ApplicationRunner, ExitCodeGenerator, ExitCodeExceptionMapper {

  private ObjectFactory<To0Client> clientProvider;

  public To0ClientApp(ObjectFactory<To0Client> clientProvider) {
    this.clientProvider = clientProvider;
  }

  /**
   * Main method.
   */
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(To0ClientApp.class, To0ClientConfiguration.class);
    app.setBannerMode(Mode.OFF);
    app.setWebApplicationType(WebApplicationType.NONE);
    app.setDefaultProperties(new SdoSpringProperties());
    app.run(args);
  }

  public ObjectFactory<To0Client> getClientProvider() {
    return clientProvider;
  }

  public void setClientProvider(ObjectFactory<To0Client> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public int getExitCode() {
    return 0;
  }

  @Override
  public int getExitCode(Throwable t) {
    if (t instanceof SdoProtocolException) {
      SdoProtocolException pe = (SdoProtocolException) t;
      return pe.getError().getEc().toInteger();
    }
    return 1;
  }

  @Override
  public void run(ApplicationArguments args) throws GeneralSecurityException, InterruptedException,
      IOException, TimeoutException, URISyntaxException {

    // Expected args, which can be in any order:
    // - ownership voucher (.op) files to run TO0 on

    for (String arg : args.getNonOptionArgs()) {
      StringBuilder builder = new StringBuilder();

      try (FileReader reader = new FileReader(new File(arg))) {

        int i = -1;

        while ((i = reader.read()) > -1) {
          builder.append((char) i);
        }
      } // try with reader

      OwnershipProxy proxy;
      try {
        proxy = new OwnershipProxyDecoder().decode(CharBuffer.wrap(builder.toString()));

      } catch (IOException e) {
        LoggerFactory.getLogger(getClass()).error(e.getMessage());
        continue;
      }

      To0Client client = getClientProvider().getObject();
      if (null != client) {
        client.setProxy(proxy);
        client.run(this::onTo0Complete);
      }
    } // foreach arg
  }

  private void onTo0Complete(UUID key, Duration ws) {
    LoggerFactory.getLogger(getClass()).info("TO0 complete for " + key + ". ws = " + ws);
  }
}
