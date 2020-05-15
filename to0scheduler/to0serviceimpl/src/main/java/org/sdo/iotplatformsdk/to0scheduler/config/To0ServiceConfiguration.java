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

package org.sdo.iotplatformsdk.to0scheduler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.net.http.HttpClient;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.sdo.iotplatformsdk.common.protocol.config.ObjectFactory;
import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactory;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactory;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.to0scheduler.rest.RestClient;
import org.sdo.iotplatformsdk.to0scheduler.rest.To0Controller;
import org.sdo.iotplatformsdk.to0scheduler.rest.To0ControllerAdvice;
import org.sdo.iotplatformsdk.to0scheduler.rest.To0CustomSslContextFactory;
import org.sdo.iotplatformsdk.to0scheduler.rest.To0HealthController;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0ClientSession;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0Scheduler;
import org.sdo.iotplatformsdk.to0scheduler.to0service.To0PropertiesLoader;
import org.sdo.iotplatformsdk.to0scheduler.to0service.To0ProxystoreImpl;
import org.sdo.iotplatformsdk.to0scheduler.to0service.To0SchedulerEventsImpl;
import org.sdo.iotplatformsdk.to0scheduler.to0service.To0SignatureServiceFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Defines the configuration for the this application. Mainly contains the bean
 * definitions that are used throughout the application.
 */
@Configuration
@EnableAutoConfiguration
@EnableAsync
public class To0ServiceConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(To0ServiceConfiguration.class);

  // the number of thread in the ThreadPoolTaskExecutor.
  private final int threadPoolSize = null != To0PropertiesLoader.getProperty("thread.pool.size")
      ? Integer.parseInt(To0PropertiesLoader.getProperty("thread.pool.size"))
      : 10;
  private final int sessionPoolSize = null != To0PropertiesLoader.getProperty("session.pool.size")
      ? Integer.parseInt(To0PropertiesLoader.getProperty("session.pool.size"))
      : 10;
  private final Duration defaultWaitSeconds =
      null != To0PropertiesLoader.getProperty("org.sdo.to0.ownersign.to0d.ws")
          ? Duration.parse(To0PropertiesLoader.getProperty("org.sdo.to0.ownersign.to0d.ws"))
          : Duration.ofHours(1);
  private final String redirectInfo =
      To0PropertiesLoader.getProperty("org.sdo.to0.ownersign.to1d.bo");
  private final Duration httpClientTimeout = Duration.ofSeconds(30);

  /**
   * Registers the controller {@link To0HealthController}.
   */
  @Bean
  protected To0HealthController to0HealthController() {
    return new To0HealthController();
  }

  /**
   * Registers the controller {@link To0Controller}.
   */
  @Bean
  protected To0Controller to0Controller() throws NumberFormatException, Exception {
    return new To0Controller(to0Scheduler(), defaultWaitSeconds);
  }

  /**
   * Register the controller advice {@link To0ControllerAdvice}.
   */
  @Bean
  protected To0ControllerAdvice sdoControllerAdvice() {
    return new To0ControllerAdvice();
  }

  /**
   * Creates and returns a singleton instance of {@link To0Scheduler}. Calls to this method
   * returns the same instance, always.
   */
  @Bean
  protected To0Scheduler to0Scheduler() throws NumberFormatException, Exception {
    return new To0Scheduler(executorService(), to0ClientSessionFactory(),
        new To0SchedulerEventsImpl(restClient()), new To0ProxystoreImpl(restClient()),
        sessionPoolSize);
  }

  /**
   * Creates and returns a singleton instance of {@link To0ClientSession} using
   * {@link ObjectFactory}. Calls to this method returns the same instance, always.
   */
  @Bean
  protected ObjectFactory<To0ClientSession> to0ClientSessionFactory() throws Exception {
    return new ObjectFactory<To0ClientSession>() {

      @Override
      public To0ClientSession getObject() {
        return new To0ClientSession(signatureServiceFactory(),
            Paths.get(".").toUri().resolve(redirectInfo).normalize(),
            HttpClient.newBuilder().sslContext(sslContextFactory().getObject())
                .connectTimeout(httpClientTimeout).build());
      }
    };
  }

  /**
   * Creates and returns a singleton instance of {@link SignatureServiceFactory} implementation.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected SignatureServiceFactory signatureServiceFactory() {
    return new To0SignatureServiceFactoryImpl(restClient());
  }

  /**
   * Create a thread pool executor.
   *
   * @return {@link ScheduledThreadPoolExecutor} object.
   */
  protected ExecutorService executorService() {
    ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(threadPoolSize);
    return threadPoolExecutor;
  }

  /**
   * Creates and returns a singleton instance of an implementation of {@link SecureRandomFactory}.
   * Calls to this method returns the same instance, always.
   */
  @Bean
  protected ObjectFactory<SecureRandom> secureRandomFactory() {
    return new SecureRandomFactory();
  }

  /**
   * Creates and returns a singleton instance of {@link SslContextFactory}. Calls to this
   * method returns the same instance, always. Used to send requests to Rendezvous Service.
   */
  @Bean
  protected SslContextFactory sslContextFactory() {
    return new SslContextFactory(secureRandomFactory().getObject());
  }

  /**
   * Creates and returns a singleton instance of {@link To0CustomSslContextFactory}. Calls to
   * this method returns the same instance, always. Used to send requests to Owner Companion
   * Service (OCS).
   */
  @Bean
  protected To0CustomSslContextFactory to0CustomSslContextFactory() {
    return new To0CustomSslContextFactory(secureRandomFactory().getObject());
  }

  /**
   * Creates and returns an instance of {@link RestClient}. Calls to this method returns
   * a new instance, always.
   */
  @Bean
  @Scope("prototype")
  protected RestClient restClient() {
    return new RestClient(to0CustomSslContextFactory().getObject(), objectMapper());
  }

  /**
   * Creates and returns a singleton instance of {@link ObjectMapper}. Calls
   * to this method returns the same instance, always.
   */
  @Bean
  protected ObjectMapper objectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    return mapper;
  }
}
