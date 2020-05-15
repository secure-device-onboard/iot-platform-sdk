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

package org.sdo.iotplatformsdk.ops.rest;

import java.nio.CharBuffer;
import java.util.concurrent.Callable;

import org.sdo.iotplatformsdk.common.protocol.codecs.To2HelloDeviceCodec;
import org.sdo.iotplatformsdk.common.protocol.rest.AuthToken;
import org.sdo.iotplatformsdk.common.protocol.types.SdoProtocolException;
import org.sdo.iotplatformsdk.common.protocol.types.To2HelloDevice;
import org.sdo.iotplatformsdk.ops.to2library.Message255Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message40Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message42Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message44Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message46Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message48Handler;
import org.sdo.iotplatformsdk.ops.to2library.Message50Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mp/113/msg")
public class OpsTransferOwnershipController {

  protected static final Logger LOGGER =
      LoggerFactory.getLogger(OpsTransferOwnershipController.class);

  private final Message40Handler message40Handler;
  private final Message42Handler message42Handler;
  private final Message44Handler message44Handler;
  private final Message46Handler message46Handler;
  private final Message48Handler message48Handler;
  private final Message50Handler message50Handler;
  private final Message255Handler message255Handler;

  /**
   * Constructor.
   */
  public OpsTransferOwnershipController(Message40Handler message40Handler,
      Message42Handler message42Handler, Message44Handler message44Handler,
      Message46Handler message46Handler, Message48Handler message48Handler,
      Message50Handler message50Handler, Message255Handler message255Handler) {
    this.message40Handler = message40Handler;
    this.message42Handler = message42Handler;
    this.message44Handler = message44Handler;
    this.message46Handler = message46Handler;
    this.message48Handler = message48Handler;
    this.message50Handler = message50Handler;
    this.message255Handler = message255Handler;
  }

  /**
   * Typical REST end-point that represents Type 40 of Transfer Ownership Protocol 2.
   *
   * @param requestEntity {@link RequestEntity} containing String request
   * @return              {@link ResponseEntity} containing response
   */
  @PostMapping("/40")
  public Callable<ResponseEntity<String>>
      message40Handler(final RequestEntity<String> requestEntity) {

    return () -> {
      try {
        LOGGER.info("[HTTP Request]: " + requestEntity.toString());
        final String response = message40Handler.onPost(requestEntity.getBody());
        final To2HelloDevice helloDevice =
            new To2HelloDeviceCodec().decoder().apply(CharBuffer.wrap(requestEntity.getBody()));
        final ResponseEntity<String> responseEntity = ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, new AuthToken(helloDevice.getG2()).toString())
            .contentType(MediaType.APPLICATION_JSON).body(response);
        LOGGER.info("[HTTP Response]: " + responseEntity.toString());
        return responseEntity;
      } catch (SdoProtocolException spe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(spe.getError().toString());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    };
  }

  /**
   * Typical REST end-point that represents Type 42 of Transfer Ownership Protocol 2.
   *
   * @param requestEntity {@link RequestEntity} containing String request
   * @return              {@link ResponseEntity} containing response
   */
  @PostMapping("/42")
  public Callable<ResponseEntity<String>>
      message42Handler(final RequestEntity<String> requestEntity) {

    return () -> {
      try {
        LOGGER.info("[HTTP Request]: " + requestEntity.toString());
        final AuthToken authToken =
            new AuthToken(requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (authToken == null || authToken.getUuid() == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final String sessionId = authToken.getUuid().toString();
        final String response = message42Handler.onPost(requestEntity.getBody(), sessionId);
        final ResponseEntity<String> responseEntity =
            ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authToken.toString())
                .contentType(MediaType.APPLICATION_JSON).body(response);
        LOGGER.info("[HTTP Response]: " + responseEntity.toString());
        return responseEntity;
      } catch (SdoProtocolException spe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(spe.getError().toString());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    };
  }

  /**
   * Typical REST end-point that represents Type 44 of Transfer Ownership Protocol 2.
   *
   * @param requestEntity {@link RequestEntity} containing String request
   * @return              {@link ResponseEntity} containing response
   */
  @PostMapping("/44")
  public Callable<ResponseEntity<String>>
      message44Handler(final RequestEntity<String> requestEntity) {

    return () -> {
      try {
        LOGGER.info("[HTTP Request]: " + requestEntity.toString());
        final AuthToken authToken =
            new AuthToken(requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (authToken == null || authToken.getUuid() == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final String sessionId = authToken.getUuid().toString();
        final String response = message44Handler.onPost(requestEntity.getBody(), sessionId);
        final ResponseEntity<String> responseEntity =
            ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authToken.toString())
                .contentType(MediaType.APPLICATION_JSON).body(response);
        LOGGER.info("[HTTP Response]: " + responseEntity.toString());
        return responseEntity;
      } catch (SdoProtocolException spe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(spe.getError().toString());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    };
  }

  /**
   * Typical REST end-point that represents Type 46 of Transfer Ownership Protocol 2.
   *
   * @param requestEntity {@link RequestEntity} containing String request
   * @return              {@link ResponseEntity} containing response
   */
  @PostMapping("/46")
  public Callable<ResponseEntity<String>>
      message46Handler(final RequestEntity<String> requestEntity) {

    return () -> {
      try {
        LOGGER.info("[HTTP Request]: " + requestEntity.toString());
        final AuthToken authToken =
            new AuthToken(requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (authToken == null || authToken.getUuid() == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final String sessionId = authToken.getUuid().toString();
        final String response = message46Handler.onPost(requestEntity.getBody(), sessionId);
        final ResponseEntity<String> responseEntity =
            ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authToken.toString())
                .contentType(MediaType.APPLICATION_JSON).body(response);
        LOGGER.info("[HTTP Response]: " + responseEntity.toString());
        return responseEntity;
      } catch (SdoProtocolException spe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(spe.getError().toString());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    };
  }

  /**
   * Typical REST end-point that represents Type 48 of Transfer Ownership Protocol 2.
   *
   * @param requestEntity {@link RequestEntity} containing String request
   * @return              {@link ResponseEntity} containing response
   */
  @PostMapping("/48")
  public Callable<ResponseEntity<String>>
      message48Handler(final RequestEntity<String> requestEntity) {

    return () -> {
      try {
        LOGGER.info("[HTTP Request]: " + requestEntity.toString());
        final AuthToken authToken =
            new AuthToken(requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (authToken == null || authToken.getUuid() == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final String sessionId = authToken.getUuid().toString();
        final String response = message48Handler.onPost(requestEntity.getBody(), sessionId);
        final ResponseEntity<String> responseEntity =
            ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authToken.toString())
                .contentType(MediaType.APPLICATION_JSON).body(response);
        LOGGER.info("[HTTP Response]: " + responseEntity.toString());
        return responseEntity;
      } catch (SdoProtocolException spe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(spe.getError().toString());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    };
  }

  /**
   * Typical REST end-point that represents Type 50 of Transfer Ownership Protocol 2.
   *
   * @param requestEntity {@link RequestEntity} containing String request
   * @return              {@link ResponseEntity} containing response
   */
  @PostMapping("/50")
  public Callable<ResponseEntity<String>>
      message50Handler(final RequestEntity<String> requestEntity) {

    return () -> {
      try {
        LOGGER.info("[HTTP Request]: " + requestEntity.toString());
        final AuthToken authToken =
            new AuthToken(requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (authToken == null || authToken.getUuid() == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final String sessionId = authToken.getUuid().toString();
        final String response = message50Handler.onPost(requestEntity.getBody(), sessionId);
        final ResponseEntity<String> responseEntity =
            ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, authToken.toString())
                .contentType(MediaType.APPLICATION_JSON).body(response);
        LOGGER.info("[HTTP Response]: " + responseEntity.toString());
        return responseEntity;
      } catch (SdoProtocolException spe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(spe.getError().toString());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    };
  }

  /**
   * Typical REST end-point that represents Type 255 of Transfer Ownership Protocol 2.
   *
   * @param requestEntity {@link RequestEntity} containing String request
   * @return              {@link ResponseEntity} containing response
   */
  @PostMapping("/255")
  public Callable<ResponseEntity<String>>
      message255Handler(final RequestEntity<String> requestEntity) {

    return () -> {
      try {
        LOGGER.info("[HTTP Request]: " + requestEntity.toString());
        final AuthToken authToken =
            new AuthToken(requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (authToken == null || authToken.getUuid() == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final String sessionId = authToken.getUuid().toString();
        message255Handler.onPost(requestEntity.getBody(), sessionId);
        final ResponseEntity<String> responseEntity = ResponseEntity.ok().build();
        LOGGER.info("[HTTP Response]: " + responseEntity.toString());
        return responseEntity;
      } catch (SdoProtocolException spe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(spe.getError().toString());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    };
  }
}
