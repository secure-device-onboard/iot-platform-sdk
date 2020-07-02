// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.services;

import org.sdo.iotplatformsdk.common.rest.CipherOperation;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;
import org.sdo.iotplatformsdk.common.rest.OwnerVoucher;
import org.sdo.iotplatformsdk.common.rest.SetupInfoResponse;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.sdo.iotplatformsdk.common.rest.SviMessage;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;

/**
 * The interface containing the methods whose implementation must be provided for the REST APIs to
 * work.
 */
public interface OcsRestContract {

  /**
   * Return a String representation of {@link OwnerVoucher} for the specified device identifier.
   *
   * @param deviceId the device identifier.
   * @return the owner voucher.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public String getDeviceVoucher(String deviceId) throws Exception;

  /**
   * Store the specified string representation of {@link OwnerVoucher}.
   *
   * @param voucher the String representation of OwnerVoucher.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void putDeviceVoucher(String voucher) throws Exception;

  /**
   * Return a {@link DeviceState} object for the specified device identifier.
   *
   * @param deviceId the unique device identifier.
   * @return device
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public DeviceState getDeviceState(String deviceId) throws Exception;

  /**
   * Store the specified {@link DeviceState} for the given device identifier.
   *
   * @param deviceId  the unique device identifier.
   * @param stateInfo a state object representing the device's state.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void postDeviceState(String deviceId, DeviceState stateInfo) throws Exception;

  /**
   * Return an array of {@link SviMessage} representing the list of serviceinfo messages for the
   * specified device identifier.
   *
   * @param deviceId the unique device identifier.
   * @return an array of serviceinfo messages for this device.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public SviMessage[] getMessage(String deviceId) throws Exception;

  /**
   * Store an array of {@link SviMessage} representing the list of serviceinfo messages for the
   * specified device identifier.
   *
   * @param deviceId the unique device identifier.
   * @param sviValue an array of serviceinfo messages for this device.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void putMessage(String deviceId, SviMessage[] sviValue) throws Exception;

  /**
   * Delete the service info message array for the specified device identifier.
   *
   * @param deviceId the unique device identifier.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void deleteMessage(String deviceId) throws Exception;

  /**
   * Store the specified {@link ModuleMessage} representing the device serviceinfo messages for the
   * given device identifier.
   *
   * @param deviceId the unique device identifier.
   * @param message  device service-info message.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void postMessage(String deviceId, ModuleMessage[] message) throws Exception;

  /**
   * Return the bytes of the specified service-info identifier, ranging from the starting index to
   * the ending index.
   *
   * @param deviceId   the device identifier.
   * @param valueId    the service-info identifier.
   * @param startParam index of the first byte that will be read.
   * @param endParam   index of the last byte that will be read.
   * @return ranged byte array of the service-info.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public byte[] getValue(String deviceId, String valueId, int startParam, int endParam)
      throws Exception;

  /**
   * Store an array of bytes as service info value into the service info identifier.
   *
   * @param deviceId the device identifier.
   * @param valueId  the service-info identifier.
   * @param value    the byte array of service -info.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void putValue(String deviceId, String valueId, byte[] value) throws Exception;

  /**
   * Deletes the service info value specified by the value identifier.
   *
   * @param deviceId the device identifier.
   * @param valueId  the service-info identifier.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void deleteValue(String deviceId, String valueId) throws Exception;

  /**
   * Return an array of {@link ModuleMessage} representing the pre-serviceinfo messages for the
   * given device identifier.
   *
   * @param deviceId the unique device identifier.
   * @return pre-service for the device.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public ModuleMessage[] getPsi(String deviceId) throws Exception;

  /**
   * Return the {@link SetupInfoResponse} for the specified device identifier.
   *
   * @param deviceId the unique device identifier.
   * @return device's new rendezvous information and device identifier.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public SetupInfoResponse getSetupInfo(String deviceId) throws Exception;

  /**
   * Store the error information from the received {@link DeviceState} object for the specified
   * device identifier.
   *
   * @param deviceId   the unique device identifier.
   * @param errorState device's error information.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void postErrors(String deviceId, DeviceState errorState) throws Exception;

  /**
   * Return {@link SignatureResponse} object containing the specified data's signature, along with
   * the public key and its algorithm that could validate the signature.
   *
   * @param deviceId the unique device identifier.
   * @param input    data to be signed.
   * @return the signature along with the public key.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public SignatureResponse postSignature(String deviceId, String input) throws Exception;

  /**
   * Return the resultant bytes of data after performing the specified {@link CipherOperation} when
   * asymmetric key exchange is used, for the given device.
   *
   * @param deviceId  the unique device identifier.
   * @param input     data on which cipher operation will be performed.
   * @param operation the cipher operation to perform.
   *
   * @return byte array.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public byte[] cipherOperations(String deviceId, byte[] input, String operation) throws Exception;

  /**
   * Store the TO2 session information of the device as stored in {@link To2DeviceSessionInfo}
   * instance.
   *
   * @param deviceId             the unique device identifier.
   * @param to2DeviceSessionInfo To2 session information of the device.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void postDeviceSessionInfo(String deviceId, To2DeviceSessionInfo to2DeviceSessionInfo)
      throws Exception;

  /**
   * Return the {@link To2DeviceSessionInfo} instance that represents the session information of the
   * TO2 protocol.
   *
   * @param deviceId the unique device identifier.
   * @return To2 session information of the device.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public To2DeviceSessionInfo getDeviceSessionInfo(String deviceId) throws Exception;

  /**
   * Removes the TO2 session information for the given device identifier.
   *
   * @param deviceId the unique device identifier.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void deleteDeviceSessionInfo(String deviceId) throws Exception;

  /**
   * Deletes the device along with all the associated data for the given device identifier.
   *
   * @param deviceId the device identifier.
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public void deleteDevice(String deviceId) throws Exception;

  /**
   * Return boolean value representing whether the Owner supports the Resale
   * protocol as per the specification, for the given device identifier.
   *
   * @param deviceId the device identifier.
   * @return boolean value 'true' or 'false'
   * @throws Exception Exception that is thrown in case of any operational error.
   */
  public boolean isOwnerResaleSupported(String deviceId) throws Exception;
}
