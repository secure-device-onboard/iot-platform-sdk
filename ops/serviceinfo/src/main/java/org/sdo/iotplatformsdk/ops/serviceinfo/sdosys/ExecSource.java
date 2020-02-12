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

package org.sdo.iotplatformsdk.ops.serviceinfo.sdosys;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;
import org.sdo.iotplatformsdk.ops.serviceinfo.ServiceInfoMultiSource;

/**
 * Source (issuer) of sdo_sys:exec instructions.
 *
 * <p>sdo_sys:exec executes the command described in the command value.
 *
 * <p>Commands are encoded as a base64-encoded null-separated sequence of strings,
 * with the first string being the program/executable and the remainder being arguments
 * to that executable. The entire sequence is terminated by an additional null,
 * resulting in two sequential nulls.
 */
public final class ExecSource implements ServiceInfoMultiSource {

  public static class Builder {

    private List<String> command = new LinkedList<>();

    public ExecSource build() {
      return new ExecSource(command);
    }

    public Builder command(List<String> command) {
      this.command = command;
      return this;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final List<String> commandlist;

  private ExecSource(List<String> command) { // use Builder to create instances
    this.commandlist = command;
  }

  @Override
  public List<ServiceInfoEntry> getServiceInfo(UUID t) {

    final List<ServiceInfoEntry> entries = new LinkedList<>();
    final List<String> command = getCommand();

    final StringBuilder builder = new StringBuilder();
    for (final String token : command) {
      builder.append(token);
      builder.append('\0');
    }
    builder.append('\0');

    final ByteBuffer chars = SdoSys.CHARSET.encode(builder.toString());
    final CharSequence b64 = SdoSys.CHARSET.decode(Base64.getEncoder().encode(chars));

    entries.add(new ServiceInfoEntry(SdoSys.KEY_EXEC, b64));

    return entries;
  }

  private List<String> getCommand() {
    return commandlist;
  }
}
