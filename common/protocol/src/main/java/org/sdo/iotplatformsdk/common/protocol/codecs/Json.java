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

package org.sdo.iotplatformsdk.common.protocol.codecs;

public class Json {

  public static final Character BEGIN_ARRAY = '[';
  public static final Character BEGIN_OBJECT = '{';
  public static final Character COLON = ':';
  public static final Character COMMA = ',';
  public static final Character END_ARRAY = ']';
  public static final Character END_OBJECT = '}';
  public static final Character QUOTE = '"';

  public static String asKey(String name) {
    return QUOTE + name + QUOTE + COLON;
  }
}
