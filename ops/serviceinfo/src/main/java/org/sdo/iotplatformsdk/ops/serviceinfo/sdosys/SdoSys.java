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

package org.sdo.iotplatformsdk.ops.serviceinfo.sdosys;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Constants common to the sdosys management agent and service.
 */
public interface SdoSys {

  String NAME = "sdo_sys";
  Charset CHARSET = StandardCharsets.UTF_8;
  String KEY_ACTIVE = NAME + ":active";
  String KEY_EXEC = NAME + ":exec";
  String KEY_FILEDESC = NAME + ":filedesc";
  String KEY_WRITE = NAME + ":write";
}
