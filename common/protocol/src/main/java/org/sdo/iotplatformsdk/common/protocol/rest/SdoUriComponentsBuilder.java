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

package org.sdo.iotplatformsdk.common.protocol.rest;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * A {@link UriComponentsBuilder} configured for SDO REST paths.
 */
public class SdoUriComponentsBuilder extends UriComponentsBuilder {

  public SdoUriComponentsBuilder(final URI uri) {
    this(uri.getScheme(), uri.getHost(), uri.getPort());
  }

  public SdoUriComponentsBuilder(final String scheme, final String host, final int port) {
    super();
    this.scheme(scheme).host(host).port(port).path("/mp/{version}/msg/{message}");
  }
}
