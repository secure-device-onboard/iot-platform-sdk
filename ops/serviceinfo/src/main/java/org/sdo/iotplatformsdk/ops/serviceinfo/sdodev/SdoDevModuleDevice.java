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

package org.sdo.iotplatformsdk.ops.serviceinfo.sdodev;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoSource;

/**
 * The SDO ServiceInfo SdoDevice Module, device-side.
 */
public final class SdoDevModuleDevice implements ServiceInfoSource {

  @Override
  public List<ServiceInfoEntry> getServiceInfo() {

    List<ServiceInfoEntry> values = new LinkedList<>();

    String value = "1";
    values.add(new ServiceInfoEntry(SdoDev.KEY_ACTIVE, value));

    value = System.getProperty("os.name");
    values.add(new ServiceInfoEntry(SdoDev.KEY_OS, value));

    value = System.getProperty("os.arch");
    values.add(new ServiceInfoEntry(SdoDev.KEY_ARCH, value));
    values.add(new ServiceInfoEntry(SdoDev.KEY_BIN, value));

    value = System.getProperty("os.version");
    values.add(new ServiceInfoEntry(SdoDev.KEY_VERSION, value));

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("SDO reference device, using JRE ");
    stringBuilder.append(System.getProperty("java.version"));
    stringBuilder.append(" (");
    stringBuilder.append(System.getProperty("java.vendor"));
    stringBuilder.append(")");
    value = stringBuilder.toString();
    values.add(new ServiceInfoEntry(SdoDev.KEY_DEVICE, value));

    value = "0";
    values.add(new ServiceInfoEntry(SdoDev.KEY_SN, value));

    value = File.separator;
    values.add(new ServiceInfoEntry(SdoDev.KEY_PATHSEP, value));

    value = File.pathSeparator;
    values.add(new ServiceInfoEntry(SdoDev.KEY_SEP, value));

    value = System.getProperty("line.separator");
    values.add(new ServiceInfoEntry(SdoDev.KEY_NL, value));

    value = System.getProperty("java.io.tmpdir");
    values.add(new ServiceInfoEntry(SdoDev.KEY_TMP, value));

    values.add(new ServiceInfoEntry(SdoDev.KEY_DIR, value));

    return values;
  }
}
