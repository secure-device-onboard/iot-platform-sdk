// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.util.UUID;
import org.sdo.iotplatformsdk.ops.rest.RestClient;

public abstract class OpsCharSequence implements CharSequence {

  private final RestClient client;
  protected int length;
  protected int offset;
  protected final String valueId;
  private final UUID deviceId;

  /**
   * Constructor.
   *
   * @param deviceId the device identifier.
   * @param client   {@link RestClient} instance.
   * @param valueId  serviceinfo identifier.
   */
  public OpsCharSequence(final RestClient client, final String valueId, final UUID deviceId) {
    this.client = client;
    this.valueId = valueId;
    this.length = 0;
    this.offset = 0;
    this.deviceId = deviceId;
  }

  @Override
  public int length() {
    return this.length;
  }

  @Override
  public char charAt(int i) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public CharSequence subSequence(int start, int end) {

    OpsCharSequence seq = newSequence();
    seq.offset = start;
    seq.length = end - start;

    return seq;
  }

  /*
   * Calculate the block size, and send request to retrieve the serviceinfo identified valueId.
   */
  @Override
  public String toString() {
    final int firstBlock = getOffset() / getCharsPerBlock();
    final int lastBlock = (getOffset() + length()) / getCharsPerBlock() + 1;

    final int nSourceBytes = (lastBlock - firstBlock) * getBytesPerBlock();

    final int start = firstBlock * getBytesPerBlock();
    final int end = start + nSourceBytes;

    byte[] buf = getClient().getValue(getDeviceId(), getValueId(), start, end);

    return getContent(buf);

  }

  protected abstract OpsCharSequence newSequence();

  protected abstract String getContent(byte[] data);

  protected int getOffset() {
    return offset;
  }

  protected String getValueId() {
    return valueId;
  }

  protected UUID getDeviceId() {
    return deviceId;
  }

  protected RestClient getClient() {
    return client;
  }

  protected int getCharsPerBlock() {
    return 1;
  }

  protected int getBytesPerBlock() {
    return 1;
  }

  public void setContentLength(int length) {
    this.length = length;
  }
}
