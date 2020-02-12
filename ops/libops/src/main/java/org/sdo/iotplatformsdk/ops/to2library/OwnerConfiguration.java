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

package org.sdo.iotplatformsdk.ops.to2library;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.SSLContext;

import org.sdo.iotplatformsdk.common.protocol.config.CertificateFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.ClientHttpRequestFactoryCreatingFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.EpidOptionBean;
import org.sdo.iotplatformsdk.common.protocol.config.OwnershipProxyStorage;
import org.sdo.iotplatformsdk.common.protocol.config.PrivateKeyFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SdoProperties;
import org.sdo.iotplatformsdk.common.protocol.config.SecureRandomFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SimpleOwnershipProxyStorageFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.config.SimpleTaskExecutor;
import org.sdo.iotplatformsdk.common.protocol.config.SslContextFactoryBean;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoControllerAdvice;
import org.sdo.iotplatformsdk.common.protocol.security.AsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.security.SignatureServiceFactory;
import org.sdo.iotplatformsdk.common.protocol.security.Signatures;
import org.sdo.iotplatformsdk.common.protocol.security.SimpleAsymKexCodec;
import org.sdo.iotplatformsdk.common.protocol.types.RendezvousInfo;
import org.sdo.iotplatformsdk.common.protocol.types.SignatureBlock;
import org.sdo.iotplatformsdk.common.protocol.util.Destroyer;
import org.sdo.iotplatformsdk.ops.to2library.SetupDeviceService.Setup;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableConfigurationProperties(SdoProperties.class)
public class OwnerConfiguration {

  private final SdoProperties properties;

  public OwnerConfiguration(SdoProperties properties) {
    this.properties = properties;
  }

  @Bean
  protected FactoryBean<Certificate> certificateFactoryBean() {
    CertificateFactoryBean factory = new CertificateFactoryBean();
    factory.setUri(getProperties().getOwner().getCert());
    return factory;
  }

  @Bean
  @SuppressWarnings("unused")
  protected AsymKexCodec asymKexCodec() throws Exception {
    return new SimpleAsymKexCodec(
        new KeyPair(publicKeyFactoryBean().getObject(), privateKeyFactoryBean().getObject()),
        secureRandomFactoryBean().getObject());
  }

  @Bean
  @SuppressWarnings("unused")
  protected ClientHttpRequestFactoryCreatingFactoryBean
      clientHttpRequestFactoryCreatingFactoryBean() {
    return new ClientHttpRequestFactoryCreatingFactoryBean();
  }

  @Bean
  protected EpidOptionBean epidOptions() {
    return getProperties().getEpid().getOptions();
  }

  protected Path getOutputDir() {
    return getProperties().getOwner().getOutputDir();
  }

  protected SdoProperties getProperties() {
    return properties;
  }

  protected Path getProxyDir() {
    return getProperties().getOwner().getProxyDir();
  }

  @Bean
  @SuppressWarnings("unused")
  protected Message255Handler message255Handler() throws Exception {
    return new Message255Handler(sessionStorageFactoryBean().getObject());
  }

  @Bean
  @SuppressWarnings("unused")
  protected Message40Handler message40Handler() throws Exception {
    return new Message40Handler(signatureServiceFactory());
  }

  @Bean
  @SuppressWarnings("unused")
  protected Message42Handler message42Handler() {
    return new Message42Handler();
  }

  @Bean
  @SuppressWarnings("unused")
  protected Message44Handler message44Handler() {
    return new Message44Handler();
  }

  @Bean
  @SuppressWarnings("unused")
  protected Message46Handler message46Handler() {
    return new Message46Handler(signatureServiceFactory());
  }

  @Bean
  @SuppressWarnings("unused")
  protected Message48Handler message48Handler() {
    return new Message48Handler();
  }

  @Bean
  @SuppressWarnings("unused")
  protected Message50Handler message50Handler() {
    return new Message50Handler();
  }

  @Bean
  @SuppressWarnings("unused")
  protected FactoryBean<OwnerEventHandler> ownerEventHandlerFactoryBean() {
    return new SimpleOwnerEventHandlerFactoryBean();
  }

  @Bean
  @SuppressWarnings("unused")
  protected FactoryBean<OwnershipProxyStorage> ownershipProxyStorageFactoryBean() {
    SimpleOwnershipProxyStorageFactoryBean bean = new SimpleOwnershipProxyStorageFactoryBean();
    bean.setProxyDir(getProxyDir());
    return bean;
  }

  @Bean
  @SuppressWarnings("unused")
  protected FactoryBean<PrivateKey> privateKeyFactoryBean() {
    PrivateKeyFactoryBean factory = new PrivateKeyFactoryBean();
    factory.setUri(getProperties().getOwner().getKey());
    return factory;
  }

  @Bean
  @SuppressWarnings("unused")
  protected FactoryBean<PublicKey> publicKeyFactoryBean() throws Exception {

    return new FactoryBean<PublicKey>() {

      @Override
      public PublicKey getObject() throws Exception {
        Certificate cert = certificateFactoryBean().getObject();
        return null != cert ? cert.getPublicKey() : null;
      }

      @Override
      public Class<?> getObjectType() {
        return PublicKey.class;
      }
    };
  }

  @Bean
  protected FactoryBean<SecureRandom> secureRandomFactoryBean() {
    return new SecureRandomFactoryBean();
  }

  @Bean
  protected FactoryBean<SessionStorage> sessionStorageFactoryBean() {
    return new SimpleSessionStorageFactoryBean();
  }

  @Bean
  @SuppressWarnings("unused")
  protected SetupDeviceService setupDeviceService() {
    return (g2, r2) -> new Setup() {

      @Override
      public UUID g3() {
        return g2;
      }

      @Override
      public RendezvousInfo r3() {
        return r2;
      }
    };
  }

  // This simple factory does not change keys based on UUID,
  // and should only be used for testing.
  @Bean
  @SuppressWarnings("unused")
  protected SignatureServiceFactory signatureServiceFactory() {
    return ignored -> data -> CompletableFuture.supplyAsync(() -> {
      try (Destroyer<PrivateKey> hkey = new Destroyer<>(privateKeyFactoryBean().getObject())) {
        final Signature s = Signatures.getInstance(hkey.get());
        s.initSign(hkey.get());
        s.update(StandardCharsets.US_ASCII.encode(CharBuffer.wrap(data)));
        return new SignatureBlock(CharBuffer.wrap(data), publicKeyFactoryBean().getObject(),
            ByteBuffer.wrap(s.sign()));

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Bean
  @SuppressWarnings("unused")
  protected FactoryBean<SSLContext> sslContextFactoryBean() {
    return new SslContextFactoryBean();
  }

  @Bean
  protected FactoryBean<TaskExecutor> taskExecutorFactoryBean() {
    return new FactoryBean<TaskExecutor>() {

      @Override
      public TaskExecutor getObject() throws Exception {
        return new SimpleTaskExecutor();
      }

      @Override
      public Class<?> getObjectType() {
        return TaskExecutor.class;
      }
    };
  }

  @Bean
  @SuppressWarnings("unused")
  SdoControllerAdvice sdoControllerAdvice() {
    return new SdoControllerAdvice();
  }

  @Bean
  protected KeyExchangeDecoder keyExchangeDecoder() throws Exception {
    return new KeyExchangeDecoder(asymKexCodec(), secureRandomFactoryBean().getObject());
  }
}
