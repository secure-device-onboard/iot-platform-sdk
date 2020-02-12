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

/*
 * WARNING: THIS FILE CONTAINS DEMO CODE THAT IS NOT INTENDED FOR SECURE DEPLOYMENT. CUSTOMERS MUST
 * REPLACE THESE CLASSES WITH AN IMPLEMENTATION THAT IS SECURE WITHIN THEIR ENVIRONMENT.
 */

package org.sdo.iotplatformsdk.to0scheduler.to0service;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.sdo.iotplatformsdk.common.protocol.config.CertificateFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.ClientHttpRequestFactoryCreatingFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.PrivateKeyFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties;
import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SimpleWaitSecondsBuilderFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.WaitSecondsBuilder;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoControllerAdvice;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel;
import org.sdo.iotplatformsdk.common.protocol.security.CryptoLevel112;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.types.To1SdoRedirect;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0ClientSession;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0Controller;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0ProxyStore;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0Scheduler;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0SchedulerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

/**
 * Defines the configuration for the this application. Mainly contains the bean
 * definitions that are used throughout the application.
 */
@Configuration
@EnableAutoConfiguration
@EnableAsync
@EnableConfigurationProperties(SdoProperties.class)
public class To0ServiceConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(To0ServiceConfiguration.class);
  private final SdoProperties sdoProperties;

  // the number of thread in the ThreadPoolTaskExecutor.
  @Value("${thread.pool.size:10}")
  private int threadPoolSize;

  @Value("${server.ssl.key-store-type}")
  private String keyStoreType;

  @Value("${server.ssl.trust-store-type}")
  private String trustStoreType;

  @Value("${server.ssl.key-store}")
  private String keyStoreFile;

  @Value("${server.ssl.key-store-password}")
  private String keyStorePwd;

  @Value("${server.ssl.trust-store}")
  private String trustStoreFile;

  @Value("${server.ssl.trust-store-password}")
  private String trustStorePwd;

  /**
   * Constructor.
   *
   * @param sdoProperties {@link SdoProperties} containing input parameters
   */
  public To0ServiceConfiguration(SdoProperties sdoProperties) {
    this.sdoProperties = sdoProperties;
  }

  protected SdoProperties getProperties() {
    return sdoProperties;
  }

  @Bean
  protected To0Scheduler to0Scheduler() {
    return new To0Scheduler();
  }

  @Bean
  protected To0SchedulerEvents to0SchedulerEvents() {
    return new To0SchedulerEventsImpl();
  }

  @Bean
  protected ClientHttpRequestFactoryCreatingFactoryBean
      clientHttpRequestFactoryCreatingFactoryBean() {
    return new ClientHttpRequestFactoryCreatingFactoryBean();
  }

  @Bean
  protected To1SdoRedirect to1SdoRedirect() {
    SdoProperties.To0.OwnerSign ownerSign = getProperties().getTo0().getOwnerSign();
    SdoProperties.To0.OwnerSign.To1d.Bo to1d = ownerSign.getTo1d().getBo();

    return new To1SdoRedirect(to1d.getI1(), to1d.getDns1(), to1d.getPort1(), null);
  }

  @Bean
  protected CryptoLevel cryptoLevel() {
    return new CryptoLevel112();
  }

  @Bean
  protected SignatureServiceFactory signatureServiceFactory() {
    return new To0SignatureServiceFactoryImpl();
  }

  @Bean
  @Scope("prototype")
  protected To0ClientSession to0Session() {
    To0ClientSession to0ClientSession = new To0ClientSession(signatureServiceFactory());
    return to0ClientSession;
  }

  @Bean
  protected FactoryBean<WaitSecondsBuilder> waitSecondsBuilderFactoryBean() {
    SimpleWaitSecondsBuilderFactoryBean bean = new SimpleWaitSecondsBuilderFactoryBean();
    bean.setWaitSeconds(getProperties().getTo0().getOwnerSign().getTo0d().getWs());
    return bean;
  }

  /**
   * Create a thread pool for task scheduler.
   *
   * @return {@link ThreadPoolTaskScheduler} object.
   */
  @Bean
  protected ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(threadPoolSize);

    threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
    return threadPoolTaskScheduler;
  }

  @Bean
  protected FactoryBean<SecureRandom> secureRandomFactoryBean() {
    return new SecureRandomFactoryBean();
  }

  @Bean
  protected SdoControllerAdvice sdoControllerAdvice() {
    return new SdoControllerAdvice();
  }

  @Bean
  protected FactoryBean<SSLContext> sslContextFactoryBean() {
    return new SslContextFactoryBean();
  }

  @Bean
  protected FactoryBean<PrivateKey> privateKeyFactoryBean() {
    PrivateKeyFactoryBean factory = new PrivateKeyFactoryBean();
    return factory;
  }

  @Bean
  protected FactoryBean<PublicKey> publicKeyFactoryBean() throws Exception {

    return new FactoryBean<PublicKey>() {

      @Override
      public PublicKey getObject() throws Exception {
        return null;
      }

      @Override
      public Class<?> getObjectType() {
        return PublicKey.class;
      }
    };
  }

  @Bean
  protected FactoryBean<Certificate> certificateFactoryBean() {
    CertificateFactoryBean factory = new CertificateFactoryBean();
    return factory;
  }

  @Bean
  protected To0Controller to0Controller() {
    return new To0Controller(getProperties().getTo0().getOwnerSign().getTo0d().getWs());
  }

  @Bean
  protected RestUri restUri() {
    return new RestUri();
  }

  @Bean
  @DependsOn("restUri")
  @Scope("prototype")
  protected RestClient restClient() {
    return new RestClient();
  }

  @Bean
  protected To0ProxyStore proxyStore() {
    return new To0ProxystoreImpl();
  }

  @Bean
  @Scope("prototype")
  protected RestTemplate restTemplate() {
    return new RestTemplate(getRequestFactory());
  }

  /**
   * Returns an instance of {@link ClientHttpRequestFactory}.
   *
   * <p>The keystore and truststore files are read to create {@link SSLContext}
   * instance, that is used to generate the http client. If the keystore and
   * truststore files are not valid, no request will be made by any method of this
   * application.
   *
   * @return http request factory.
   */
  private HttpComponentsClientHttpRequestFactory getRequestFactory() {
    try {

      KeyStore identityKeyStore = KeyStore.getInstance(keyStoreType);
      File keystoreFile = new File(keyStoreFile);
      FileInputStream identityKeyStoreFile = new FileInputStream(keystoreFile);
      identityKeyStore.load(identityKeyStoreFile, keyStorePwd.toCharArray());

      KeyManagerFactory kmf =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(identityKeyStore, keyStorePwd.toCharArray());
      KeyManager[] km = kmf.getKeyManagers();

      KeyStore trustKeyStore = KeyStore.getInstance(trustStoreType);
      File truststoreFile = new File(trustStoreFile);
      FileInputStream trustKeyStoreFile = new FileInputStream(truststoreFile);
      trustKeyStore.load(trustKeyStoreFile, trustStorePwd.toCharArray());

      TrustManagerFactory tmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(trustKeyStore);
      TrustManager[] tm = tmf.getTrustManagers();

      SSLContext sslContext = new SSLContextBuilder().build();
      sslContext.init(km, tm, new SecureRandom());

      SSLConnectionSocketFactory csf =
          new SSLConnectionSocketFactory(sslContext, new DefaultHostnameVerifier());
      CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
      HttpComponentsClientHttpRequestFactory requestFactory =
          new HttpComponentsClientHttpRequestFactory();
      requestFactory.setHttpClient(httpClient);
      return requestFactory;
    } catch (Exception e) {
      LOGGER.error("Error occurred while creating request factory. ", e.getMessage());
      LOGGER.debug(e.getMessage(), e);
      return null;
    }
  }
}
