// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.sdo.iotplatformsdk.common.protocol.types.PreServiceInfoEntry;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;
import org.sdo.iotplatformsdk.common.rest.MessageEncoding;
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;
import org.sdo.iotplatformsdk.common.rest.SviMessage;
import org.sdo.iotplatformsdk.ops.rest.RestClient;
import org.sdo.iotplatformsdk.ops.serviceinfo.PreServiceInfoMultiSource;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMultiSink;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMultiSource;

public class OpsServiceInfoModule
    implements ServiceInfoMultiSource, ServiceInfoMultiSink, PreServiceInfoMultiSource {

  private final RestClient client;

  public OpsServiceInfoModule(RestClient client) {
    this.client = client;
  }

  /*
   * Send a request to retrieve an array of serviceinfo information for the specified device
   * identifier.
   *
   * <p>Create a list of ServiceInfoEntry and return the list.
   */
  @Override
  public List<ServiceInfoEntry> getServiceInfo(final UUID deviceId) {

    final List<ServiceInfoEntry> list = new LinkedList<>();
    final SviMessage[] messages = getClient().getMessage(deviceId.toString());
    for (SviMessage message : messages) {
      if (null != message) {
        String key = String.format("%1$s:%2$s", message.getModule(), message.getMsg());
        OpsCharSequence seq = newSequence(getClient(), message, deviceId);
        seq.setContentLength(message.getValueLen());
        ServiceInfoEntry entry = new ServiceInfoEntry(key, seq);
        list.add(entry);
      }
    }
    return list;
  }

  /*
   * Send a request to retrieve the pre-serviceinfo for the device. Generate a list of
   * PreServiceInfoEntry from the received content and return the list.
   */
  @Override
  public List<PreServiceInfoEntry> getPreServiceInfo(final UUID deviceId) {
    final ModuleMessage[] messages = getClient().getPsi(deviceId.toString());
    final List<PreServiceInfoEntry> list = new LinkedList<>();
    for (ModuleMessage msg : messages) {
      if (null != msg) {
        String key = String.format("%1$s:%2$s", msg.getModule(), msg.getMsg());
        list.add(new PreServiceInfoEntry(key, msg.getValue()));
      }
    }

    return list;
  }

  private RestClient getClient() {
    return client;
  }

  private OpsCharSequence newSequence(RestClient client, SviMessage message, UUID deviceId) {
    if (null != message.getEnc()) {
      if (message.getEnc().equals(MessageEncoding.BASE64.toString())) {
        return new OpsBase64Sequence(client, message.getValueId(), deviceId);
      } else if (message.getEnc().equals(MessageEncoding.ASCII.toString())) {
        return new OpsAsciiSequence(client, message.getValueId(), deviceId);
      } else {
        throw new RuntimeException(new UnsupportedEncodingException(message.getEnc()));
      }
    }
    throw new RuntimeException(new Exception("No message encoding specified."));
  }

  /*
   * Send a request to update the device serviceinfo. Each entry is being sent in a separate call.
   */
  @Override
  public void putServiceInfo(UUID deviceId, ServiceInfo serviceInfo) {

    List<ModuleMessage> deviceModuleMessages = new ArrayList<ModuleMessage>();
    for (ServiceInfoEntry serviceInfoEntry : serviceInfo) {
      final CharSequence keySeq = serviceInfoEntry.getKey();
      final int keyLen = keySeq.length();
      final char findChar = ':';
      for (int i = 0; i < keyLen; i++) {
        if (keySeq.charAt(i) == findChar) {

          String name = keySeq.subSequence(0, i).toString();
          String msg = keySeq.subSequence(i + 1, keyLen).toString();

          ModuleMessage message = new ModuleMessage();
          message.setModule(name);
          message.setMsg(msg);
          message.setValue(serviceInfoEntry.getValue().toString());

          deviceModuleMessages.add(message);
        }
      }
    }
    getClient().postMessage(deviceId.toString(), deviceModuleMessages);

  }
}
