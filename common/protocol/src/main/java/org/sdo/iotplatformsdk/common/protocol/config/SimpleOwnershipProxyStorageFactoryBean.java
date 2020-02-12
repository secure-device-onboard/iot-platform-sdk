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

package org.sdo.iotplatformsdk.common.protocol.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.sdo.iotplatformsdk.common.protocol.codecs.OwnershipProxyCodec.OwnershipProxyDecoder;
import org.sdo.iotplatformsdk.common.protocol.types.OwnershipProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class SimpleOwnershipProxyStorageFactoryBean implements FactoryBean<OwnershipProxyStorage> {

  private static final Logger LOG =
      LoggerFactory.getLogger(SimpleOwnershipProxyStorageFactoryBean.class);
  private Path proxyDir = Paths.get("");

  @Override
  public OwnershipProxyStorage getObject() {

    return new OwnershipProxyStorage() {

      Map<UUID, OwnershipProxy> proxyMap = new HashMap<>();

      @Override
      public OwnershipProxy load(UUID uuid) {
        reload(getProxyDir());
        return getProxyMap().get(uuid);
      }

      @Override
      public void store(OwnershipProxy proxy) {
        getProxyMap().put(proxy.getOh().getG(), proxy);
        reload(getProxyDir());
      }

      private Map<UUID, OwnershipProxy> getProxyMap() {
        return proxyMap;
      }

      private void setProxyMap(Map<UUID, OwnershipProxy> proxyMap) {
        this.proxyMap = proxyMap;
      }

      private void reload(Path proxyDir) {

        Map<UUID, OwnershipProxy> map = new HashMap<>();
        File[] files = getProxyDir().toFile().listFiles();

        for (File file : null == files ? new File[0] : files) {

          try {
            StringBuilder builder = new StringBuilder();

            try (Reader reader = new FileReader(file)) {
              int i;

              while ((i = reader.read()) >= 0) {
                builder.append((char) i);
              }
            }

            OwnershipProxy proxy =
                new OwnershipProxyDecoder().decode(CharBuffer.wrap(builder.toString()));
            map.put(proxy.getOh().getG(), proxy);

          } catch (BufferUnderflowException | IOException e) {
            // Ignore files that don't contain valid proxies, this is not a big deal
            LOG.debug(file + " is not an ownership voucher");
          }
        }

        setProxyMap(map);
      }
    };
  }

  @Override
  public Class<?> getObjectType() {
    return OwnershipProxyStorage.class;
  }

  public Path getProxyDir() {
    return proxyDir;
  }

  public void setProxyDir(Path proxyDir) {
    this.proxyDir = proxyDir;
  }
}
