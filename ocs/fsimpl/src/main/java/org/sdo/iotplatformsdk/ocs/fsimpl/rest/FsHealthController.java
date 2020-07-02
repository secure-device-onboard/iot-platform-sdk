// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.fsimpl.rest;

import java.sql.Timestamp;
import org.sdo.iotplatformsdk.ocs.fsimpl.fs.FsPropertiesLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class FsHealthController {

  /**
   * Check the health of the application.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> checkHealth() {
    final String version = FsPropertiesLoader.getProperty("application.version");
    final String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    final String response =
        "{\"version\":\"" + version + "\"," + "\"timestamp\":\"" + timestamp + "\"}";
    return ResponseEntity.ok(response);
  }
}
