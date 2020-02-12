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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.FactoryBean;

public class SimpleOwnerEventHandlerFactoryBean implements FactoryBean<OwnerEventHandler> {

  private Path outputDir = Paths.get(System.getProperty("java.io.tmpdir"));

  @Override
  public OwnerEventHandler getObject() throws Exception {
    SimpleOwnerEventHandler handler = new SimpleOwnerEventHandler();
    handler.setOutputDir(getOutputDir());
    return handler;
  }

  @Override
  public Class<?> getObjectType() {
    return OwnerEventHandler.class;
  }

  public Path getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(Path outputDir) {
    this.outputDir = outputDir;
  }
}
