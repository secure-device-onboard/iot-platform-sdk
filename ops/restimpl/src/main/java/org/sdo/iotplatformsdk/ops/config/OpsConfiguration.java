/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.ops.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.util.HashSet;
import java.util.concurrent.Executors;

import org.sdo.iotplatformsdk.common.protocol.config.EpidOptionBean;
import org.sdo.iotplatformsdk.common.protocol.config.ObjectFactory;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactory;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsAsymKexCodec;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsOwnerEventHandlerFactory;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsPropertiesLoader;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsProxyStorage;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsResaleSupport;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsServiceInfoModule;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsSessionStorageFactory;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsSetupDeviceServiceInfo;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsSignatureServiceFactory;
import org.sdo.iotplatformsdk.ops.rest.OpsControllerAdvice;
import org.sdo.iotplatformsdk.ops.rest.OpsCustomSslContextFactory;
import org.sdo.iotplatformsdk.ops.rest.OpsHealthController;
import org.sdo.iotplatformsdk.ops.rest.OpsTransferOwnershipController;
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoModule;
import org.sdo.iotplatformsdk.ops.to2library.KeyExchangeDecoder;
import org.sdo.iotplatformsdk.ops.to2library.Message255Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message40Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message42Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message44Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message46Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message48Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message50Handler;
import org.sdo.iotplatformsdk.ops.to2library.OwnerEventHandler;
import org.sdo.iotplatformsdk.ops.to2library.OwnerResaleSupport;
import org.sdo.iotplatformsdk.ops.to2library.SessionStorage;
import org.sdo.iotplatformsdk.ops.to2library.SetupDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for the application that registers beans.
 */
@Configuration
@EnableAutoConfiguration
@EnableAsync
public class OpsConfiguration implements WebMvcConfigurer {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpsConfiguration.class);

  private ConcurrentTaskExecutor executor;

  private final int threadPoolSize = null != OpsPropertiesLoader.getProperty("thread.pool.size")
      ? Integer.parseInt(OpsPropertiesLoader.getProperty("thread.pool.size"))
      : 10;
  private final boolean epidTestMode =
      null != OpsPropertiesLoader.getProperty("org.sdo.epid.test-mode")
          ? Boolean.valueOf(OpsPropertiesLoader.getProperty("org.sdo.epid.test-mode"))
          : false;
  private final String epidOnlineUrl =
      OpsPropertiesLoader.getProperty("org.sdo.epid.epid-online-url");

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setTaskExecutor(getTaskExecutor());
  }

  private ConcurrentTaskExecutor getTaskExecutor() {
    if (null == this.executor) {
      this.executor = new ConcurrentTaskExecutor(Executors.newFixedThreadPool(threadPoolSize));
    }
    return executor;
  }

  /**
   * Registers the controller {@link OpsHealthController}.
   */
  @Bean
  protected OpsHealthController opsHealthController() {
    return new OpsHealthController();
  }

  /**
   * Registers the controller {@link OpsTransferOwnershipController}.
   */
  @Bean
  protected OpsTransferOwnershipController opsTransferOwnershipController() throws Exception {
    return new OpsTransferOwnershipController(message40Handler(), message42Handler(),
        message44Handler(), message46Handler(), message48Handler(), message50Handler(),
        message255Handler());
  }

  /**
   * Register the controller advice {@link OpsControllerAdvice}.
   */
  @Bean
  protected OpsControllerAdvice sdoControllerAdvice() {
    return new OpsControllerAdvice();
  }

  /**
   * Creates and returns a singleton instance of {@link SecureRandomFactory}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected SecureRandomFactory secureRandomFactory() {
    return new SecureRandomFactory();
  }

  /**
   * Creates and returns a singleton instance of {@link OpsCustomSslContextFactory}. Calls to
   * this method returns the same instance, always. Used to send requests to Owner Companion
   * Service (OCS).
   */
  @Bean
  protected OpsCustomSslContextFactory opsCustomSslContextFactory() {
    return new OpsCustomSslContextFactory(secureRandomFactory().getObject());
  }

  /**
   * Creates and returns an instance of {@link RestClient}. Calls to this method returns
   * a new instance, always.
   */
  @Bean
  @Scope("prototype")
  protected RestClient restClient() {
    return new RestClient(opsCustomSslContextFactory().getObject());
  }

  /**
   * Creates and returns a singleton {@link HashSet} containing {@link ServiceInfoModule}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected HashSet<ServiceInfoModule> serviceInfoModules() {
    final HashSet<ServiceInfoModule> serviceInfoModules = new HashSet<>();
    serviceInfoModules.add(new OpsServiceInfoModule(restClient()));
    return serviceInfoModules;
  }

  /**
   * Creates and returns a singleton instance of {@link OwnershipProxyStorage}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected OwnershipProxyStorage ownershipProxyStorage() {
    return new OpsProxyStorage(restClient());
  }

  /**
   * Creates and returns a singleton instance of {@link OpsOwnerEventHandlerFactory}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected ObjectFactory<OwnerEventHandler> ownerEventHandlerFactory() {
    return new OpsOwnerEventHandlerFactory(restClient());
  }

  /**
   * Creates and returns a singleton instance of {@link ObjectMapper}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected ObjectMapper objectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
    return mapper;
  }

  /**
   * Creates and returns a singleton instance of an implementation of
   * {@link SignatureServiceFactory}. Calls to this method returns the same instance, always.
   */
  @Bean
  protected SignatureServiceFactory signatureServiceFactory() {
    return new OpsSignatureServiceFactory(restClient());
  }

  /**
   * Creates and returns a singleton instance of an implementation of {@link AsymKexCodec}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected AsymKexCodec asymKexCodec() throws Exception {
    return new OpsAsymKexCodec(restClient());
  }

  /**
   * Creates and returns a singleton instance of an implementation of {@link SetupDeviceService}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected SetupDeviceService setupDeviceService() {
    return new OpsSetupDeviceServiceInfo(restClient());
  }

  /**
   * Creates and returns a singleton instance of an implementation of
   * {@link OpsSessionStorageFactory}. Calls to this method returns the same instance, always.
   */
  @Bean
  protected ObjectFactory<SessionStorage> sessionStorageFactory() {
    return new OpsSessionStorageFactory(restClient());
  }

  /**
   * Creates and returns a singleton instance of {@link KeyExchangeDecoder}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected KeyExchangeDecoder keyExchangeDecoder() throws Exception {
    return new KeyExchangeDecoder(asymKexCodec(), secureRandomFactory().getObject());
  }

  /**
   * Creates and returns a singleton instance of {@link EpidOptionBean}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected EpidOptionBean epidOptionBean() {
    final EpidOptionBean epidOptionBean = new EpidOptionBean();
    if (null != epidOnlineUrl) {
      epidOptionBean.setEpidOnlineUrl(epidOnlineUrl);
    }
    epidOptionBean.setTestMode(epidTestMode);
    return epidOptionBean;
  }

  /**
   * Creates and returns a singleton instance of {@link OpsResaleSupport}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected OwnerResaleSupport ownerResaleSupport() {
    return new OpsResaleSupport(restClient());
  }

  /**
   * Creates and returns a singleton instance of {@link Message40Handler}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected Message40Handler message40Handler() throws Exception {
    final Message40Handler message40Handler =
        new Message40Handler(signatureServiceFactory(), ownerEventHandlerFactory().getObject(),
            ownershipProxyStorage(), secureRandomFactory().getObject(),
            sessionStorageFactory().getObject(), keyExchangeDecoder());
    message40Handler.setEpidOptions(epidOptionBean());
    return message40Handler;
  }

  /**
   * Creates and returns a singleton instance of {@link Message42Handler}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected Message42Handler message42Handler() throws Exception {
    return new Message42Handler(sessionStorageFactory().getObject());
  }

  /**
   * Creates and returns a singleton instance of {@link Message44Handler}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected Message44Handler message44Handler() throws Exception {
    final Message44Handler message44Handler =
        new Message44Handler(secureRandomFactory().getObject(), sessionStorageFactory().getObject(),
            keyExchangeDecoder(), serviceInfoModules());
    message44Handler.setEpidOptions(epidOptionBean());
    return message44Handler;
  }

  /**
   * Creates and returns a singleton instance of {@link Message46Handler}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected Message46Handler message46Handler() throws Exception {
    return new Message46Handler(signatureServiceFactory(), sessionStorageFactory().getObject(),
        setupDeviceService(), secureRandomFactory().getObject(), keyExchangeDecoder(),
        serviceInfoModules());
  }

  /**
   * Creates and returns a singleton instance of {@link Message48Handler}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected Message48Handler message48Handler() throws Exception {
    return new Message48Handler(sessionStorageFactory().getObject(),
        secureRandomFactory().getObject(), keyExchangeDecoder(), serviceInfoModules());
  }

  /**
   * Creates and returns a singleton instance of {@link Message50Handler}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected Message50Handler message50Handler() throws Exception {
    return new Message50Handler(ownerEventHandlerFactory().getObject(),
        sessionStorageFactory().getObject(), ownershipProxyStorage(),
        secureRandomFactory().getObject(), keyExchangeDecoder(), ownerResaleSupport());
  }

  /**
   * Creates and returns a singleton instance of {@link Message255Handler}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected Message255Handler message255Handler() throws Exception {
    return new Message255Handler(sessionStorageFactory().getObject(),
        ownerEventHandlerFactory().getObject());
  }
}
