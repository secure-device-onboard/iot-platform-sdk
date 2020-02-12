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

package org.sdo.iotplatformsdk.ops.opsimpl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.concurrent.Executors;

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
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.config.PrivateKeyFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
import org.sdo.iotplatformsdk.ops.to2library.OwnerConfiguration;
import org.sdo.iotplatformsdk.ops.to2library.OwnerEventHandler;
import org.sdo.iotplatformsdk.ops.to2library.SessionStorage;
import org.sdo.iotplatformsdk.ops.to2library.SetupDeviceService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for the application. Overrides certain bean definitions from the parent
 * configuration.
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(SdoProperties.class)
public class OpsConfiguration extends OwnerConfiguration implements WebMvcConfigurer {

  private ConcurrentTaskExecutor executor;

  @Value("${thread.pool.size:10}")
  private int threadPoolSize;

  @Value("${client.ssl.key-store-type}")
  private String keyStoreType;

  @Value("${client.ssl.trust-store-type}")
  private String trustStoreType;

  @Value("${client.ssl.key-store}")
  private String keyStoreFile;

  @Value("${client.ssl.key-store-password}")
  private String keyStorePwd;

  @Value("${client.ssl.trust-store}")
  private String trustStoreFile;

  @Value("${client.ssl.trust-store-password}")
  private String trustStorePwd;

  public OpsConfiguration(SdoProperties sdoProperties) {
    super(sdoProperties);
  }

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setTaskExecutor(getTaskExecutor());
  }

  @Override
  protected FactoryBean<TaskExecutor> taskExecutorFactoryBean() {
    return new FactoryBean<TaskExecutor>() {

      @Override
      public TaskExecutor getObject() throws Exception {
        return getTaskExecutor();
      }

      @Override
      public Class<?> getObjectType() {
        return TaskExecutor.class;
      }
    };
  }

  private ConcurrentTaskExecutor getTaskExecutor() {
    if (null == this.executor) {
      this.executor = new ConcurrentTaskExecutor(Executors.newFixedThreadPool(threadPoolSize));
    }
    return executor;
  }

  @Bean
  protected OpsRestUri opsRestUri() {
    return new OpsRestUri();
  }

  @Bean
  @DependsOn("opsRestUri")
  @Scope("prototype")
  public RestClient restClient() {
    return new RestClient();
  }

  @Bean
  @DependsOn("restClient")
  protected ServiceInfoModule demoServiceInfoModule() {
    return new OpsServiceInfoModule();
  }

  @Bean
  protected OwnershipProxyStorage ownershipProxyStorage() {
    return new OpsProxyStorage();
  }

  @Bean
  @Override
  protected FactoryBean<OwnerEventHandler> ownerEventHandlerFactoryBean() {
    return new OpsOwnerEventHandlerFactoryBean();
  }

  @Override
  @Bean
  protected ClientHttpRequestFactoryCreatingFactoryBean
      clientHttpRequestFactoryCreatingFactoryBean() {
    return new ClientHttpRequestFactoryCreatingFactoryBean();
  }

  @Override
  @Bean
  protected SslContextFactoryBean sslContextFactoryBean() {
    return new SslContextFactoryBean();
  }

  @Bean
  protected ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
    return mapper;
  }

  @Override
  @Bean
  protected SignatureServiceFactory signatureServiceFactory() {
    return new OpsSignatureServiceFactory();
  }

  @Override
  @Bean
  protected FactoryBean<Certificate> certificateFactoryBean() {
    CertificateFactoryBean factory = new CertificateFactoryBean();
    return factory;
  }

  @Override
  @Bean
  protected FactoryBean<PrivateKey> privateKeyFactoryBean() {
    PrivateKeyFactoryBean factory = new PrivateKeyFactoryBean();
    return factory;
  }

  @Override
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

  @Override
  @Bean
  protected AsymKexCodec asymKexCodec() throws Exception {
    return new OpsAsymKexCodec(restClient());
  }

  @Override
  @Bean
  protected SetupDeviceService setupDeviceService() {
    return new OpsSetupDeviceServiceInfo();
  }

  @Override
  @Bean
  protected FactoryBean<SessionStorage> sessionStorageFactoryBean() {
    return new OpsSessionStorageFactoryBean();
  }

  @Bean
  @Scope("prototype")
  protected RestTemplate restTemplate() {
    return new RestTemplate(getRequestFactory());
  }

  /**
   * Returns an instance of {@link ClientHttpRequestFactory}.
   *
   * <p>The keystore and truststore files are read to create {@link SSLContext} instance,
   * that is used to generate the http client. If the keystore and truststore files are
   * not valid, no request will be made by any method of this application.
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
      return null;
    }
  }
}
