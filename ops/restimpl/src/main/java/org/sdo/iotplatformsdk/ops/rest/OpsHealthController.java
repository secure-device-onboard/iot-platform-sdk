// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.rest;

import java.sql.Timestamp;
import org.sdo.iotplatformsdk.ops.opsimpl.OpsPropertiesLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mp/113/health")
public class OpsHealthController {

  public OpsHealthController() {}

  /**
   * Check the health of the application.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> checkHealth() {
    final String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    final String response =
        "{\"version\":\"" + OpsPropertiesLoader.getProperty("application.version") + "\","
            + "\"timestamp\":\"" + timestamp + "\"}";
    return ResponseEntity.ok(response);
  }
}
