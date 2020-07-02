// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.to0scheduler.rest;

import java.time.Duration;
import java.util.Arrays;
import org.sdo.iotplatformsdk.common.rest.To0Request;
import org.sdo.iotplatformsdk.to0scheduler.to0library.To0Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Main Controller class that contains the REST API definition to get list of devices for TO0.
 *
 */
@RestController
@RequestMapping("v1")
public class To0Controller {

  protected static final Logger logger = LoggerFactory.getLogger(To0Controller.class);

  private final Duration ws;
  private final To0Scheduler scheduler;

  public To0Controller(To0Scheduler to0Scheduler, Duration ws) {
    this.scheduler = to0Scheduler;
    this.ws = ws;
  }

  /**
   * Take the list of devices as input and return with an Ok. Depending on the available sessions,
   * the devices will be scheduled. The unscheduled devices are dropped as the client can again
   * make the call to schedule the dropped devices.
   *
   * @return string
   */
  @PostMapping(path = "/to0/devices", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Mono<ResponseEntity<String>>
      getDeviceVoucher(@RequestBody(required = true) final To0Request to0Request) {
    return Mono.defer(() -> {
      try {
        Duration waitSeconds;
        // in case of error here, use default waitSeconds.
        try {
          waitSeconds = Duration.ofSeconds(Long.parseLong(to0Request.getWaitSeconds()));
        } catch (Exception e) {
          waitSeconds = ws;
        }

        // Pass the devices to be scheduled and leave.
        if (to0Request.getGuids() != null) {
          logger.debug("Request received to schedule following clients: "
              + Arrays.asList(to0Request.getGuids()).toString());
          final boolean scheduleSucceded = scheduler.run(to0Request.getGuids(), waitSeconds);
          if (scheduleSucceded) {
            return Mono.just(new ResponseEntity<String>(HttpStatus.OK));
          } else {
            return Mono.just(new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR));
          }
        }
        return Mono.just(new ResponseEntity<String>(HttpStatus.BAD_REQUEST));
      } catch (Exception e) {
        logger.error(e.getMessage());
        logger.debug(e.getMessage(), e);
        return Mono.just(new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR));
      }
    });
  }
}
