// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.rest;

import java.sql.Timestamp;
import org.sdo.iotplatformsdk.to0scheduler.to0service.To0PropertiesLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mp/113/health")
public class To0HealthController {

  public To0HealthController() {}

  /**
   * Check the health of the application.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> checkHealth() {
    final String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    final String response =
        "{\"version\":\"" + To0PropertiesLoader.getProperty("application.version") + "\","
            + "\"timestamp\":\"" + timestamp + "\"}";
    return ResponseEntity.ok(response);
  }
}
