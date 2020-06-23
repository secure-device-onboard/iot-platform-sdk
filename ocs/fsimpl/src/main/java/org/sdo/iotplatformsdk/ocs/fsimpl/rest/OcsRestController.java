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

package org.sdo.iotplatformsdk.ocs.fsimpl.rest;

import java.io.FileNotFoundException;

import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;
import org.sdo.iotplatformsdk.common.rest.OwnerVoucher;
import org.sdo.iotplatformsdk.common.rest.SetupInfoResponse;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.sdo.iotplatformsdk.common.rest.SviMessage;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ocs.services.OcsRestContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import reactor.core.publisher.Mono;

@RestController
@EnableWebMvc
@RequestMapping("v1")
public final class OcsRestController {

  private final OcsRestContract ocsRestContractImpl;
  private static final Logger LOGGER = LoggerFactory.getLogger(OcsRestController.class);

  /**
   * Constructor.
   */
  public OcsRestController(OcsRestContract ocsRestContract) {
    this.ocsRestContractImpl = ocsRestContract;
  }

  /**
   * Returns the {@link OwnerVoucher} object representing the owner voucher for the specified
   * device.
   *
   * @param deviceId the device identifier.
   * @return owner voucher of the device.
   */
  @GetMapping(path = "/devices/{deviceId}/voucher", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<String>>
      getDeviceVoucher(@PathVariable(value = "deviceId", required = true) final String deviceId) {

    return Mono.defer(() -> {

      try {
        String response = ocsRestContractImpl.getDeviceVoucher(deviceId);
        return Mono.just(new ResponseEntity<String>(response, HttpStatus.OK));
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Stores the received {@link OwnerVoucher} for the specified device.
   *
   * @param voucher  owner voucher json contents.
   * @return
   */
  @PostMapping(path = "/devices/voucher", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity> putDeviceVoucher(@RequestBody(required = true) final String voucher) {

    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.putDeviceVoucher(voucher);
        return Mono.just(ResponseEntity.ok().build());
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Returns the {@link DeviceState} for the specified device.
   *
   * @param deviceId the device identifier.
   * @return the state information of the device.
   */
  @GetMapping(path = "/devices/{deviceId}/state", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<DeviceState>>
      getDeviceState(@PathVariable(value = "deviceId", required = true) final String deviceId) {

    return Mono.defer(() -> {
      try {
        DeviceState state = ocsRestContractImpl.getDeviceState(deviceId);
        return Mono.just(new ResponseEntity<DeviceState>(state, HttpStatus.OK));
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Store the received {@link DeviceState} of the specified device.
   *
   * @param deviceId  the device identifier.
   * @param stateInfo the state information of the device.
   * @return
   */
  @PostMapping(path = "/devices/{deviceId}/state", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity> postState(
      @PathVariable(value = "deviceId", required = true) final String deviceId,
      @RequestBody(required = true) final DeviceState stateInfo) {

    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.postDeviceState(deviceId, stateInfo);
        return Mono.just(ResponseEntity.ok().build());
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });

  }

  /**
   * Returns an array of all the {@link SviMessage} representing the serviceInfo messages for the
   * specified device.
   *
   * @param deviceId the device identifier
   * @return an array of serviceinfo messages.
   */
  @GetMapping(path = "/devices/{deviceId}/msgs", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<SviMessage[]>>
      getServiceInfo(@PathVariable(value = "deviceId", required = true) final String deviceId) {

    return Mono.defer(() -> {
      try {
        final SviMessage[] response = ocsRestContractImpl.getMessage(deviceId);
        return Mono.just(new ResponseEntity<SviMessage[]>(response, HttpStatus.OK));
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Put the service info list as received for the device identifier.
   *
   * @param deviceId    the device identifier.
   * @param sviMessages an array of serviceinfo messages.
   * @return
   */
  @PutMapping(path = "/devices/{deviceId}/msgs", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity> putSvi(
      @PathVariable(value = "deviceId", required = true) final String deviceId,
      @RequestBody(required = true) final SviMessage[] sviMessages) {

    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.putMessage(deviceId, sviMessages);
        return Mono.just(ResponseEntity.ok().build());
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Deletes the service info list for the specified device identifier.
   *
   * @param deviceId the device identifier.
   * @return
   */
  @DeleteMapping(path = "/devices/{deviceId}/msgs", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity>
      deleteSvi(@PathVariable(value = "deviceId", required = true) final String deviceId) {

    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.deleteMessage(deviceId);
        return Mono.just(ResponseEntity.ok().build());
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Store the received {@link ModuleMessage} representing deviceServiceInfo messages for the
   * specified device.
   *
   * @param deviceId the device identifier.
   * @param message  device serviceinfo message.
   * @return
   */
  @PostMapping(path = "/devices/{deviceId}/msgs", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity> postMessage(
      @PathVariable(value = "deviceId", required = true) final String deviceId,
      @RequestBody(required = true) final ModuleMessage[] message) {

    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.postMessage(deviceId, message);
        return Mono.just(ResponseEntity.ok().build());
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Returns an array of bytes ranging from the start to end indexes for the specified serviceInfo
   * message.
   *
   * @param deviceId device identifier
   * @param valueId  serviceinfo identifier
   * @param start    index at which the bytes will be read from.
   * @param end      index at which the bytes will be read to.
   * @return a ranged array of bytes representing serviceinfo.
   */
  @GetMapping(path = "/devices/{deviceId}/values/{valueId:.+}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity<byte[]>> getSviValue(
      @PathVariable(value = "deviceId", required = true) String deviceId,
      @PathVariable(value = "valueId", required = true) String valueId,
      @RequestParam(value = "start", required = true) int start,
      @RequestParam(value = "end", required = true) int end) {

    return Mono.defer(() -> {
      try {
        final byte[] response = ocsRestContractImpl.getValue(deviceId, valueId, start, end);
        return Mono.just(new ResponseEntity<byte[]>(response, HttpStatus.OK));
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Stores an array of bytes as service info blob.
   *
   * @param deviceId device identifier
   * @param valueId  the service info identifier.
   * @param value    array of bytes to be stored.
   * @return
   */
  @PutMapping(path = "/devices/{deviceId}/values/{valueId}",
      consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity> putValue(
      @PathVariable(value = "deviceId", required = true) String deviceId,
      @PathVariable(value = "valueId", required = true) String valueId,
      @RequestBody(required = true) final byte[] value) {

    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.putValue(deviceId, valueId, value);
        return Mono.just(ResponseEntity.ok().build());
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Deletes the specified service info value identified by the device identifier.
   *
   * @param deviceId device identifier
   * @param valueId  the service info identifier.
   * @return
   */
  @DeleteMapping(path = "/devices/{deviceId}/values/{valueId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity> deleteValue(
      @PathVariable(value = "deviceId", required = true) String deviceId,
      @PathVariable(value = "valueId", required = true) String valueId) {

    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.deleteValue(deviceId, valueId);
        return Mono.just(ResponseEntity.ok().build());
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Returns the {@link ModuleMessage} object representing the the pre-serviceinfo for the specified
   * device.
   *
   * @param deviceId the device identifier.
   * @return pre-serviceinfo for the device.
   */
  @GetMapping(path = "/devices/{deviceId}/psi", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<ModuleMessage[]>>
      getPsi(@PathVariable(value = "deviceId", required = true) final String deviceId) {

    return Mono.defer(() -> {
      try {
        final ModuleMessage[] response = ocsRestContractImpl.getPsi(deviceId);
        return Mono.just(new ResponseEntity<ModuleMessage[]>(response, HttpStatus.OK));
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Returns the {@link SetupInfoResponse} object representing the new rendezvous information and
   * the device identifier for the specified device.
   *
   * @param deviceId the device identifier.
   * @return an object containing the rendezvous information and guid.
   */
  @GetMapping(path = "/devices/{deviceId}/setupinfo", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<SetupInfoResponse>>
      getSetupInfo(@PathVariable(value = "deviceId", required = true) final String deviceId) {
    return Mono.defer(() -> {
      try {
        final SetupInfoResponse response = ocsRestContractImpl.getSetupInfo(deviceId);
        return Mono.just(new ResponseEntity<SetupInfoResponse>(response, HttpStatus.OK));
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(new ResponseEntity<SetupInfoResponse>(HttpStatus.INTERNAL_SERVER_ERROR));
      }
    });
  }

  /**
   * Store the {@link DeviceState} object representing the error information for the specified
   * device.
   *
   * @param deviceId   the device identifier.
   * @param errorState the error information.
   * @return
   */
  @PostMapping(path = "/devices/{deviceId}/errors", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity> postErrors(
      @PathVariable(value = "deviceId", required = true) final String deviceId,
      @RequestBody(required = false) final DeviceState errorState) {
    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.postErrors(deviceId, errorState);
        return Mono.just(new ResponseEntity(HttpStatus.OK));
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Return a {@link SignatureResponse} object containing the signature of the received data, along
   * with the public key and its algorithm that could verify the signatureOwner.
   *
   * @param deviceId the device identifier.
   * @param input    the data to be signed.
   * @return the signature, public key and its algorithm.
   */
  @PostMapping(path = "/signatures/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<SignatureResponse>> postSignature(
      @PathVariable(value = "deviceId", required = true) final String deviceId,
      @RequestBody(required = false) final String input) {
    return Mono.defer(() -> {
      try {
        final SignatureResponse response = ocsRestContractImpl.postSignature(deviceId, input);
        return Mono.just(new ResponseEntity<SignatureResponse>(response, HttpStatus.OK));
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(new ResponseEntity<SignatureResponse>(HttpStatus.INTERNAL_SERVER_ERROR));
      }
    });
  }

  /**
   * Perform the cipher operation on input array of bytes and return the resulting array of bytes.
   *
   * <p>Only supports cipher operations for a single owner key-pair.
   *
   * @param input the data to be deciphered.
   * @return byte array.
   */
  @PostMapping(path = "/ciphers/{deviceId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE,
      consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity<byte[]>> asymCipherOperations(
      @PathVariable(value = "deviceId", required = true) final String deviceId,
      @RequestParam(value = "operation", required = true) String operation,
      @RequestBody(required = false) final byte[] input) {
    return Mono.defer(() -> {
      try {
        final byte[] response = ocsRestContractImpl.cipherOperations(deviceId, input, operation);
        return Mono.just(new ResponseEntity<byte[]>(response, HttpStatus.OK));
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR));
      }
    });
  }

  /**
   * Return the {@link To2DeviceSessionInfo} for the given device identifier.
   *
   * @param deviceId the device identifier.
   * @return the session information.
   */
  @GetMapping(path = "/devices/{deviceId}/sessioninfo", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<To2DeviceSessionInfo>> getDeviceSessionInfo(
      @PathVariable(value = "deviceId", required = true) final String deviceId) {

    return Mono.defer(() -> {

      try {
        final To2DeviceSessionInfo response = ocsRestContractImpl.getDeviceSessionInfo(deviceId);
        return Mono.just(new ResponseEntity<To2DeviceSessionInfo>(response, HttpStatus.OK));
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Store the {@link To2DeviceSessionInfo} for the given device identifier.
   *
   * @param deviceId             the device identifier.
   * @param to2DeviceSessionInfo the session information.
   * @return
   */
  @PostMapping(path = "/devices/{deviceId}/sessioninfo",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity> postDeviceSessionInfo(
      @PathVariable(value = "deviceId", required = true) final String deviceId,
      @RequestBody(required = false) final To2DeviceSessionInfo to2DeviceSessionInfo) {
    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.postDeviceSessionInfo(deviceId, to2DeviceSessionInfo);
        return Mono.just(ResponseEntity.ok().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Remove the session information for the given device identifier.
   *
   * @param deviceId the device identifier.
   * @return
   */
  @DeleteMapping(path = "/devices/{deviceId}/sessioninfo",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity> postDeviceSessionInfo(
      @PathVariable(value = "deviceId", required = true) final String deviceId) {
    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.deleteDeviceSessionInfo(deviceId);
        return Mono.just(ResponseEntity.ok().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Deletes the entire device blob and all of its related data.
   *
   * @param deviceId the device identifier.
   * @return
   */
  @DeleteMapping(path = "/devices/{deviceId}/blob", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity>
      deleteDevice(@PathVariable(value = "deviceId", required = true) final String deviceId) {
    return Mono.defer(() -> {
      try {
        ocsRestContractImpl.deleteDevice(deviceId);
        return Mono.just(ResponseEntity.ok().build());
      } catch (FileNotFoundException e) {
        return Mono.just(ResponseEntity.notFound().build());
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  /**
   * Returns the boolean value representing whether the Resale protocol is supported for the
   * specified device.
   *
   * @param deviceId the device identifier.
   * @return boolean value 'true' or 'false'
   */
  @GetMapping(path = "/devices/{deviceId}/resale", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Boolean>>
      getResaleFlag(@PathVariable(value = "deviceId", required = true) final String deviceId) {
    return Mono.defer(() -> {
      try {
        final boolean response = ocsRestContractImpl.isOwnerResaleSupported(deviceId);
        return Mono.just(new ResponseEntity<Boolean>(Boolean.valueOf(response), HttpStatus.OK));
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      }
    });
  }
}
