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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.SSLContext;

import org.sdo.iotplatformsdk.common.protocol.config.CertificateFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.ClientHttpRequestFactoryCreatingFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.PrivateKeyFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties;
import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SimpleWaitSecondsBuilderFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.WaitSecondsBuilder;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel11;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureService;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.security.Signatures;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.types.To1SdoRedirect;
import org.sdo.iotplatformsdk.common.protocol.util.Destroyer;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0Client;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0ClientSession;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(SdoProperties.class)
public class To0ClientConfiguration {

  private final SdoProperties properties;

  public To0ClientConfiguration(SdoProperties properties) {
    this.properties = properties;
  }

  @Bean
  protected FactoryBean<Certificate> certificateFactoryBean() {
    CertificateFactoryBean factory = new CertificateFactoryBean();
    factory.setUri(getProperties().getOwner().getCert());
    return factory;
  }

  @Bean
  protected FactoryBean<ClientHttpRequestFactory> clientHttpRequestFactoryCreatingFactoryBean() {
    return new ClientHttpRequestFactoryCreatingFactoryBean();
  }

  @Bean
  @SuppressWarnings("unused")
  protected CryptoLevel cryptoLevel() {
    return new CryptoLevel11();
  }

  protected SdoProperties getProperties() {
    return properties;
  }

  @Bean
  protected FactoryBean<PrivateKey> privateKeyFactoryBean() {
    PrivateKeyFactoryBean factory = new PrivateKeyFactoryBean();
    factory.setUri(getProperties().getOwner().getKey());
    return factory;
  }

  @Bean
  protected FactoryBean<SecureRandom> secureRandomFactoryBean() {
    return new SecureRandomFactoryBean();
  }

  @Bean
  @SuppressWarnings("unused")
  protected SignatureService signatureService() {

    return data -> CompletableFuture.supplyAsync(() -> {
      try (Destroyer<PrivateKey> hkey = new Destroyer<>(privateKeyFactoryBean().getObject())) {
        final Signature s = Signatures.getInstance(hkey.get());
        s.initSign(hkey.get());
        s.update(StandardCharsets.US_ASCII.encode(CharBuffer.wrap(data)));
        return new SignatureBlock(CharBuffer.wrap(data),
            Objects.requireNonNull(certificateFactoryBean().getObject()).getPublicKey(),
            ByteBuffer.wrap(s.sign()));

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Bean
  @SuppressWarnings("unused")
  protected SignatureServiceFactory signatureServiceFactory() {
    return (uuids) -> signatureService();
  }

  @Bean
  protected FactoryBean<SSLContext> sslContextFactoryBean() {
    return new SslContextFactoryBean();
  }

  @Bean
  @Scope("prototype")
  protected To0Client to0Client() {
    return new To0Client();
  }

  @Bean
  @Scope("prototype")
  protected To0ClientSession to0Session() {
    return new To0ClientSession(signatureServiceFactory());
  }

  @Bean
  protected To1SdoRedirect to1SdoRedirect() {
    SdoProperties.To0.OwnerSign ownerSign = getProperties().getTo0().getOwnerSign();
    SdoProperties.To0.OwnerSign.To1d.Bo to1d = ownerSign.getTo1d().getBo();

    return new To1SdoRedirect(to1d.getI1(), to1d.getDns1(), to1d.getPort1(), null);
  }

  @Bean
  protected FactoryBean<WaitSecondsBuilder> waitSecondsBuilderFactoryBean() {
    SimpleWaitSecondsBuilderFactoryBean bean = new SimpleWaitSecondsBuilderFactoryBean();
    bean.setWaitSeconds(getProperties().getTo0().getOwnerSign().getTo0d().getWs());
    return bean;
  }
}
