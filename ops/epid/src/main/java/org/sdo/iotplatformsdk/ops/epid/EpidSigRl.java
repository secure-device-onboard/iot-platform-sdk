/*******************************************************************************
 * Copyright 2020 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.sdo.iotplatformsdk.ops.epid;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpidSigRl extends EpidSignedMaterial {
  private static final Logger mlog = LoggerFactory.getLogger(EpidSigRl.class);

  public EpidGroupingVersion version;
  public byte[] data = null;
  public byte[] parsedData = null;
  private int rlver; // 32 bit BigEndian
  public int n2; // 32 bit BigEndian

  private byte[] gid = null; // 32 or 128 bit BigEndian
  private static final int header1xSize = EpidConstants.EPID1X_GID_SIZE + rlVerSize + n2Size;
  private static final int header2xSize = EpidConstants.EPID2X_GID_SIZE + rlVerSize + n2Size;
  private static final int bkSize = 128;

  /**
   * The EpidSigRl class does not allow null or malformed data to build an invalid EpidSigRl.
   *
   * @param gid     - the group id of the SigRl we are building
   * @param data    - the data that we think is a sigRl
   * @param version - the Epid version for the sigRl
   */
  public EpidSigRl(byte[] gid, byte[] data, EpidGroupingVersion version) throws IOException {
    super(data, version, EpidFileTypes.kSigRlFile);
    // When constructing, try to parse and then throw exception if not valid
    if (gid == null) {
      throw new IOException("Invalid params, gid was null");
    }
    this.gid = gid;
    this.version = version;

    if (null == data || 0 == data.length) {
      // Support the production of a null content SigRl
      this.data = new byte[0];
      this.parsedData = new byte[0];
      return;
    }

    this.data = data;

    // Check based on reported version
    switch (version) {
      case kEpid2x:

        // Determine if this is a signed file and requires stripping
        if ((data.length - (fileHeaderSize + header2xSize + ECDSAsigSize)) % bkSize == 0) {
          // Size is correct for a signed sigRl

          if (this.wasSigned) {
            // This is a valid file object
            // Get the number of revocation entries
            this.n2 = EpidSignedMaterial.readNval(data,
                (fileHeaderSize + EpidConstants.EPID2X_GID_SIZE + rlVerSize));

            // Now we know the size, make the parsed value
            this.parsedData = EpidSignedMaterial.stripFileHeaderAndSig(this.data);

            // Now check for validity, if fails, throws IOException
            parseUnpacked2x(gid, this.n2, parsedData);
            this.isValid = true;

          } else {
            // Was not a SigRl file type
            mlog.error("Not a valid SigRl file");
            throw new IOException("Not a valid SigRl file");
          }
        } else {
          // Does not match classic Signed 2.0 size
          // Maybe it is already stripped, use as is if valid

          if (data.length < header2xSize) {
            this.isValid = false;
            mlog.error("SigRl data was not valid, not signed, and too small to be unsigned");
            throw new IOException("Not a valid unsigned SigRl file");
          }

          // Get the number of revocation entries
          this.n2 = EpidSignedMaterial.readNval(data, (EpidConstants.EPID2X_GID_SIZE + rlVerSize));

          this.parsedData = data;
          parseUnpacked2x(gid, this.n2, this.parsedData);
          this.isValid = true;
        }
        break;

      case kEpid1x:
        // Was Epid 1.x, is it a valid 1.x signed SigRl?
        if ((data.length - (fileHeaderSize + header1xSize + ECDSAsigSize)) % bkSize == 0) {

          if (this.wasSigned) {
            // Get the number of revocation entries
            this.n2 = EpidSignedMaterial.readNval(data,
                (fileHeaderSize + EpidConstants.EPID1X_GID_SIZE + rlVerSize));

            // Now we know the size, make the return value
            this.parsedData = EpidSignedMaterial.stripFileHeaderAndSig(this.data);

            parseUnpacked1x(this.gid, this.n2, this.parsedData);
            this.isValid = true;
          } else {
            throw new IOException("blobId was wrong");
          }
        } else {
          // Maybe it is already stripped, use as is if valid
          if ((data.length - (fileHeaderSize + header1xSize)) % bkSize == 0) {
            // It had a file header
            this.parsedData = new byte[this.data.length - fileHeaderSize];
            System.arraycopy(this.data, fileHeaderSize, this.parsedData, 0,
                this.data.length - fileHeaderSize);
          } else if ((this.data.length - header1xSize) % bkSize == 0) {
            // Valid unsigned no file header
            this.parsedData = this.data;
          } else {
            mlog.error("Appears to be junk of length " + this.data.length);
            throw new IOException("Invalid data");
          }

          // Get the number of revocation entries
          this.n2 = EpidSignedMaterial.readNval(data, (EpidConstants.EPID1X_GID_SIZE + rlVerSize));

          parseUnpacked1x(this.gid, this.n2, this.parsedData);
          // if not verified will throw IOException, Data verified
          this.isValid = true;
        }
        break;

      default:
        mlog.error("Invalid version for EpidVersion passed to constructor");
    }
  }

  private void parseUnpacked1x(byte[] gid, int n2, byte[] data) throws IOException {
    // First check the size to make sure we don't overrun
    if (data == null || gid == null) {
      mlog.error("parseUnpacked1x : Null parameter passed");
      throw new IOException("parseUnpacked1x : Null parameter");
    }
    if (gid.length != EpidConstants.EPID1X_GID_SIZE) {
      mlog.error("Invalid sized gid was passed");
      throw new IOException("GID incorrect length");
    }

    if (data.length != (header1xSize + (n2 * bkSize))) {
      mlog.error("SigRl data was incorrect size");
      throw new IOException("SigRl data was incorrect size");
    }

    // Get and compare the SigRl's gid
    this.gid = EpidSignedMaterial.readGid(data, 0, EpidConstants.EPID1X_GID_SIZE);
    if (!Arrays.areEqual(gid, this.gid)) {
      mlog.error("SigRl not matching GID");
      throw new IOException("SigRl not matching GID");
    }

    // Now parse the RLver value
    this.rlver = EpidSignedMaterial.readRlVer(data, EpidConstants.EPID1X_GID_SIZE);
  }

  private void parseUnpacked2x(byte[] gid, int n2, byte[] data) throws IOException {
    if (data == null || gid == null) {
      mlog.error("SigRl data or gid was null");
      throw new IOException("parseUnpacked2x : Null parameter");
    }

    if (gid.length != EpidConstants.EPID2X_GID_SIZE) {
      mlog.error("GID was incorrect size");
      throw new IOException("GID was incorrect size");
    }

    if (data.length != (header2xSize + (n2 * bkSize))) {
      mlog.error("SigRl data was incorrect size");
      throw new IOException("SigRl data was incorrect size");
    }

    // Get and compare the SigRl's gid
    this.gid = EpidSignedMaterial.readGid(data, 0, EpidConstants.EPID2X_GID_SIZE);
    if (!Arrays.areEqual(gid, this.gid)) {
      mlog.error("SigRl not matching GID");
      throw new IOException("SigRl not matching GID");
    }

    // Now parse the RLver value
    this.rlver = EpidSignedMaterial.readRlVer(data, EpidConstants.EPID2X_GID_SIZE);
  }

  /**
   * Provide a toString method to allow the displaying of a SigRl.
   *
   * @return A String object containing the SigRl data
   */
  @Override
  public String toString() {
    if (this.isValid) {
      return String.format("\n======================================================="
          + "\nSigRl was Valid for " + this.epidVersion.toString() + ", GID : 0x"
          + DatatypeConverter.printHexBinary(this.gid) + "\nWas Signed : " + this.wasSigned
          + "\nRLver : " + this.rlver + "\nHashCode : " + this.hashCode() + "\nEntries : " + this.n2
          + "\nSource File size : " + this.data.length + "\nParsed File size : "
          + this.parsedData.length + "\n=======================================================");
    } else {
      return String.format("\n======================================================="
          + "\nSigRl was Not Valid for " + this.epidVersion.toString() + ", GID : 0x"
          + DatatypeConverter.printHexBinary(this.gid) + "\nSource File size : " + this.data.length
          + "\nSource File : 0x" + DatatypeConverter.printHexBinary(this.data)
          + "\n=======================================================");
    }
  }
}
