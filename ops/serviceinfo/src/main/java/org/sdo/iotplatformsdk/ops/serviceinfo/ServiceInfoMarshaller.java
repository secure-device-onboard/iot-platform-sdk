// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.serviceinfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.sdo.iotplatformsdk.common.protocol.rest.SdoConstants;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfo;
import org.sdo.iotplatformsdk.common.protocol.types.ServiceInfoEntry;

/**
 * A utility class for marshalling a sequence of key/value service info values into a sequence of
 * ServiceInfo messages.
 *
 * <p>This class should be treated as internal to this implementation.
 */
public final class ServiceInfoMarshaller {

  private Long mtu = (long) SdoConstants.MTU_LIMIT; // protocol specification default MTU
  private List<ServiceInfoMultiSource> multiSources = new ArrayList<>();
  private List<ServiceInfoSource> sources = new ArrayList<>();

  /**
   * Construct a new object using default settings.
   */
  public ServiceInfoMarshaller() {}

  /**
   * Construct a new object.
   *
   * @param mtu          The maximum desired length of service info messages.
   * @param multiSources The service info multisources to poll for data to marshal.
   * @param sources      The service info sources to poll for data to marshal.
   */
  public ServiceInfoMarshaller(long mtu, List<ServiceInfoMultiSource> multiSources,
      List<ServiceInfoSource> sources) {

    this.mtu = mtu;
    this.multiSources = multiSources;
    this.sources = sources;
  }

  public Long getMtu() {
    return mtu;
  }

  public void setMtu(Long value) {
    mtu = value;
  }

  public List<ServiceInfoMultiSource> getMultiSources() {
    return multiSources;
  }

  public void setMultiSources(List<ServiceInfoMultiSource> value) {
    multiSources = value;
  }

  public List<ServiceInfoSource> getSources() {
    return sources;
  }

  public void setSources(List<ServiceInfoSource> value) {
    sources = value;
  }

  /**
   * Marshal for transmission all available ServiceInfo for the given UUIDs.
   *
   * <p>Poll all the registered sources to collect service info for the given UUIDs,
   * marshalling that data as an iterable sequence of ServiceInfo suppliers.
   *
   * <p>Suppliers are used because service info data can be quite large and expensive
   * to fetch and we don't want to pay the costs associated with that until we're
   * actually ready to transmit.
   *
   * <p>This is a simple implementation which builds a list of records which can,
   * in turn, be used to build the final ServiceInfos. This is easy, but the list
   * of records could itself be really big.
   * An optimized lazy implementation of Iterable could save space, should the need arise.
   *
   * @param uuids The UUIDs for which to marshal serviceInfo.
   * @return An Iterable of ServiceInfo Suppliers.
   */
  public Iterable<Supplier<ServiceInfo>> marshal(UUID... uuids) {

    // ServiceInfoSources provide key/value 'entries' in an ordered sequence with no
    // awareness
    // of how those entries will be carried by the transport layer. Value data is
    // concealed behind
    // CharSequence interfaces because we need both lazy reads and deterministic
    // subsequences.
    //
    List<ServiceInfoEntry> allEntries = concatenateAllServiceInfoEntries(uuids);
    return new LazyIterable(allEntries, getMtu());
  }

  // Assemble all provided service info entries into a homogeneous list
  //
  private List<ServiceInfoEntry> concatenateAllServiceInfoEntries(UUID... uuids) {

    final List<ServiceInfoEntry> allEntries = new LinkedList<>();

    for (final ServiceInfoSource source : getSources()) {
      allEntries.addAll(source.getServiceInfo());
    }

    for (final ServiceInfoMultiSource multiSource : getMultiSources()) {
      for (UUID uuid : uuids) {
        allEntries.addAll(multiSource.getServiceInfo(uuid));
      }
    }

    return allEntries;
  }

  private class LazyIterable implements Iterable<Supplier<ServiceInfo>> {

    private final List<ServiceInfoEntry> entries;
    private final long mtulimit;

    public LazyIterable(final List<ServiceInfoEntry> entries, final long mtu) {

      this.entries = entries;
      this.mtulimit = mtu;
    }

    public List<ServiceInfoEntry> getEntries() {
      return this.entries;
    }

    public long getMtu() {
      return this.mtulimit;
    }

    @Override
    public Iterator<Supplier<ServiceInfo>> iterator() {
      return new LazyIterator(this);
    }
  }

  private class LazyIterator implements Iterator<Supplier<ServiceInfo>> {

    private int index;
    private final LazyIterable iterable;
    private int subSequenceStart;

    public LazyIterator(final LazyIterable iterable) {
      this.iterable = iterable;
      this.index = 0;
      this.subSequenceStart = 0;
    }

    @Override
    public boolean hasNext() {
      return index < iterable.getEntries().size();
    }

    @Override
    public Supplier<ServiceInfo> next() {
      return pack();
    }

    private Supplier<ServiceInfo> pack() {

      final List<ServiceInfoEntry> subList = new ArrayList<>();

      final long mtulimit = iterable.getMtu();
      final List<ServiceInfoEntry> entries = iterable.getEntries();

      long packed = 2; // '{' '}'

      while (packed < mtulimit && index < entries.size()) {

        if (subList.size() > 0) {
          packed++; // ,
        }

        packed += 5; // " " : " "
        ServiceInfoEntry entry = entries.get(index);
        packed += entry.getKey().length();

        // find how much of the base64 encoded value fits
        int valueFitLen = 0;
        int valueRemaining = entry.getValue().length() - this.subSequenceStart;
        if (valueRemaining > 0) {
          int encodeLen = 0;
          for (;;) {
            // calculate base64 encoded length
            encodeLen = (((valueFitLen + 1) + 2) / 3) * 4;
            if ((packed + encodeLen) > mtulimit) {
              valueFitLen = valueFitLen - (valueFitLen % 4);
              break; // if encoded length does not fit then done packing
            }
            valueFitLen++;
            if (valueFitLen == valueRemaining) {
              break; // if the we are at the end of sequence then done packing
            }
          }
          packed += encodeLen;
        }

        if (valueFitLen <= 0) {
          break; // nothing fits so break for next mtu
        }

        // now we know end of sequence
        int subSequenceEnd = subSequenceStart + valueFitLen;

        if (valueFitLen == valueRemaining) {
          // everything fits so move to next entry
          if (subSequenceStart == 0) {
            subList.add(entry); // no subSequence needed
          } else {
            // Subsequence needed
            subList.add(new ServiceInfoEntry(entry.getKey(),
                entry.getValue().subSequence(subSequenceStart, subSequenceEnd)));
          }
          subSequenceStart = 0;
          index++;
        } else {
          // partial fit
          subList.add(new ServiceInfoEntry(entry.getKey(),
              entry.getValue().subSequence(subSequenceStart, subSequenceEnd)));
          subSequenceStart = subSequenceEnd;
        }
      }

      ServiceInfo serviceInfo = new ServiceInfo();
      serviceInfo.addAll(subList);
      return () -> serviceInfo;
    }
  }
}
