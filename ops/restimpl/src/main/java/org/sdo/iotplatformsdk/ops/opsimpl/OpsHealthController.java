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

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.sql.Timestamp;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mp/113/health")
public class OpsHealthController {

  private final String version;

  public OpsHealthController(final String version) {
    this.version = version;
  }

  /**
   * Check the health of the application.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> checkHealth() {
    final String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    final String response =
        "{\"version\":\"" + version + "\"," + "\"timestamp\":\"" + timestamp + "\"}";
    return ResponseEntity.ok(response);
  }
}
