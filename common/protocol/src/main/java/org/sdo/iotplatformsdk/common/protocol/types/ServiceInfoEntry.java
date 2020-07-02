// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.common.protocol.types;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * A single key:value service info entry.
 *
 * <p>A ServiceInfo object or message can contain one or more of these.
 */
public class ServiceInfoEntry extends SimpleEntry<CharSequence, CharSequence> {

  public ServiceInfoEntry(CharSequence key, CharSequence value) {
    super(key, value);
  }

  public ServiceInfoEntry(Entry<? extends CharSequence, ? extends CharSequence> entry) {
    super(entry);
  }
}
