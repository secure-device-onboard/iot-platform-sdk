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

public class EpidGroupPublicKey extends EpidSignedMaterial {
  private static final Logger mlog = LoggerFactory.getLogger(EpidGroupPublicKey.class);

  public byte[] data;
  public byte[] parsedData = null;

  private byte[] gid; // 32 or 128 bit BigEndian

  private static final int h11xSize = 96;
  private static final int h21xSize = 96;
  private static final int h1Size = 64;
  private static final int h2Size = 64;
  private static final int wSize = 128;
  private static final int body1xSize = h11xSize + h21xSize + wSize;
  private static final int bodySize = h1Size + h2Size + wSize;

  private static final int header1xSize = EpidConstants.EPID1X_GID_SIZE + body1xSize;
  private static final int header2xSize = EpidConstants.EPID2X_GID_SIZE + bodySize;

  /**
   * Constructor.
   */
  public EpidGroupPublicKey(byte[] gid, byte[] data, EpidGroupingVersion version)
      throws IOException {
    super(data, version, EpidFileTypes.kGroupPubKeyFile);
    // When constructing, try to parse and then throw exception if not valid
    // Save passed values

    // The super did the signed data parsing, now we should have the
    // unsigned data so do the parsing on it.
    this.data = data;
    this.epidVersion = version;
    this.gid = gid;
    if (data == null || gid == null) {
      mlog.error("EpidGroupPublicKey invalid data");
      throw new IOException("Invalid params");
    }

    // Check based on reported version
    switch (version) {
      case kEpid2x:
        this.epidVersion = EpidGroupingVersion.kEpid2x;

        // Determine if this is a signed file and requires stripping
        if (data.length == (fileHeaderSize + header2xSize + ECDSAsigSize)) {
          // Size is correct for a signed GrpPubKey

          if (this.wasSigned) {
            // Now we know the size, make the parsed value
            this.parsedData = stripFileHeaderAndSig(this.data);

            // Now check for validity, if fails, throws IOException
            parseUnpacked2x(gid, parsedData);
            this.isValid = true;

          } else {
            // Was not a GrpPubKey file type
            mlog.error("Not a valid GrpPubKey file");
            throw new IOException("Not a valid GrpPubKey file");
          }
        } else {

          // Does not match classic Signed 2.0 size
          // Maybe it is already stripped, use as is if valid
          this.wasSigned = false;
          if (data.length < header2xSize) {
            this.isValid = false;
            mlog.error("GrpPubKey data was not valid, not signed, and too small to be unsigned");
            break;
          }

          parsedData = data;
          parseUnpacked2x(gid, parsedData);
          // if not verified will throw IOException, Data verified
          this.isValid = true;

        }
        break;

      case kEpid1x:
        // Was Epid 1.x, is it a valid 1.x signed SigRl?
        if (data.length == fileHeaderSize + header1xSize + ECDSAsigSize) {

          if (this.wasSigned) {
            // Now we know the size, make the return value
            this.parsedData = stripFileHeaderAndSig(this.data);
          }

          parseUnpacked1x(this.gid, this.parsedData);
          this.isValid = true;

        } else {
          // Maybe it is already stripped, use as is if valid
          this.wasSigned = false;

          if (data.length == fileHeaderSize + header1xSize + ECDSAsigSize) {
            // It had a file header
            this.parsedData = stripFileHeaderAndSig(this.data);
            this.parsedData = new byte[this.data.length - fileHeaderSize];
            System.arraycopy(this.data, fileHeaderSize, this.parsedData, 0,
                this.data.length - fileHeaderSize);
          } else if (this.data.length == header1xSize) {
            // Valid unsigned no file header
            this.parsedData = this.data;
          } else {
            mlog.error("Appears to be junk of length " + this.data.length);
            throw new IOException("Invalid data");
          }

          parseUnpacked1x(this.gid, this.parsedData);
          // if not verified will throw IOException, Data verified
          this.isValid = true;
        }
        break;

      default:
        mlog.error("Invalid version for EpidVersion passed to constructor");
    }
  }

  private void parseUnpacked1x(byte[] gid, byte[] data) throws IOException {
    // First check the size to make sure we don't overrun
    if (data == null || gid == null) {
      mlog.error("parseUnpacked1x : Null parameter passed");
      throw new IOException("parseUnpacked1x : Null parameter");
    }
    if (gid.length != EpidConstants.EPID1X_GID_SIZE) {
      mlog.error("parseUnpacked1x : Invalid sized gid was passed");
      throw new IOException("parseUnpacked1x : GID incorrect length");
    }

    if (data.length < header1xSize) {
      mlog.error("parseUnpacked1x : invalid data size");
      throw new IOException("parseUnpacked1x : Data too small");
    }

    // Get and compare the GrpPubKey's gid
    this.gid = readGid(data, 0, EpidConstants.EPID1X_GID_SIZE);
    if (!Arrays.areEqual(gid, this.gid)) {
      mlog.error("parseUnpacked1x : GID not correct");
      throw new IOException("parseUnpacked1x : GID not correct");
    }
  }

  private void parseUnpacked2x(byte[] gid, byte[] data) throws IOException {
    // First check the size to make sure we don't overrun
    if (data == null || gid == null) {
      throw new IOException("parseUnpacked2x : Null parameter");
    }

    if (data.length < header2xSize) {
      throw new IOException("parseUnpacked2x : Data too small");
    }

    // Get and compare the GrpPubKey's gid
    this.gid = readGid(data, 0, EpidConstants.EPID2X_GID_SIZE);
    if (!Arrays.areEqual(gid, this.gid)) {
      mlog.error("GrpPubKey not valid");
      throw new IOException("GrpPubKey not valid");
    }
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
          + "\nGrpPubKey was Valid for " + this.epidVersion.toString() + ", GID : 0x"
          + DatatypeConverter.printHexBinary(this.gid) + "\nWas Signed : " + this.wasSigned
          + "\nSource File size : " + this.data.length + "\nParsed File size : "
          + this.parsedData.length + "\n=======================================================");
    } else {
      return String.format("\n======================================================="
          + "\nGrpPubKey was Not Valid for " + this.epidVersion.toString() + ", GID : 0x"
          + DatatypeConverter.printHexBinary(this.gid) + "\nSource File size : " + this.data.length
          + "\nSource File : 0x" + DatatypeConverter.printHexBinary(this.data)
          + "\n=======================================================");
    }
  }
}
