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

package org.sdo.iotplatformsdk.ops.epid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;

import org.sdo.iotplatformsdk.common.protocol.types.EpidKey;
import org.sdo.iotplatformsdk.common.protocol.types.EpidSignatureParameterSpec;
import org.sdo.iotplatformsdk.common.protocol.types.Nonce;
import org.sdo.iotplatformsdk.common.protocol.util.Buffers;

public class EpidSignatureSpi extends SignatureSpi {

  private EpidKey keySpec;
  private ByteArrayOutputStream messageBuffer;
  private Nonce nonce;
  private ByteBuffer taId = ByteBuffer.allocate(0);

  public byte[] getNonce() {
    return nonce.getBytes();
  }

  public void setNonce(Nonce nonce) {
    this.nonce = nonce;
  }

  public ByteBuffer getTaId() {
    return taId.asReadOnlyBuffer();
  }

  public void setTaId(ByteBuffer taId) {
    this.taId = Buffers.clone(taId);
  }

  @Override
  protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {

    if (publicKey instanceof EpidKey) {
      setKeySpec((EpidKey) publicKey);

    } else {
      throw new InvalidKeyException(publicKey.toString());
    }

    setMessageBuffer(new ByteArrayOutputStream());
  }

  @Override
  protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {

    if (privateKey instanceof EpidKey) {
      setKeySpec((EpidKey) privateKey);

    } else {
      throw new InvalidKeyException(privateKey.toString());
    }

    setMessageBuffer(new ByteArrayOutputStream());
  }

  @Override
  protected void engineUpdate(byte b) throws SignatureException {

    ByteArrayOutputStream buf = getMessageBuffer();

    if (null == buf) {
      throw new IllegalStateException();
    }

    buf.write(b);
  }

  @Override
  protected void engineUpdate(byte[] bytes, int off, int len) throws SignatureException {

    ByteArrayOutputStream buf = getMessageBuffer();

    if (null == buf) {
      throw new IllegalStateException();
    }

    buf.write(bytes, off, len);
  }

  @Override
  protected byte[] engineSign() throws SignatureException {
    // not supported
    throw new SignatureException();
  }

  @Override
  protected boolean engineVerify(byte[] signature) throws SignatureException {

    ByteArrayOutputStream buf = getMessageBuffer();
    EpidKey key = getKeySpec();
    int result;

    if (null == buf || null == key) {
      throw new IllegalStateException();
    }

    try {

      switch (key.getType()) {

        case EPIDV2_0:
          result = EpidSecurityProvider.getEpidLib().verify20Signature(key.getEncoded(),
              EpidLib.HashAlg.KSHA256.getValue(), signature, buf.toByteArray());
          break;

        case EPIDV1_1:
          result = EpidSecurityProvider.getEpidLib().verify11Signature(key.getEncoded(), signature,
              buf.toByteArray(), getNonce(), Buffers.unwrap(getTaId()));
          break;

        case EPIDV1_0:
          result = EpidSecurityProvider.getEpidLib().verify10Signature(key.getEncoded(), signature,
              buf.toByteArray(), getNonce(), Buffers.unwrap(getTaId()));
          break;

        default:
          throw new UnsupportedOperationException(key.getType().toString());
      }

    } catch (IOException e) {
      throw new SignatureException(e);
    }
    return (0 == result);
  }

  @Deprecated
  @Override
  protected void engineSetParameter(String s, Object o) throws InvalidParameterException {

  }

  @Override
  protected void engineSetParameter(AlgorithmParameterSpec parameterSpec)
      throws InvalidParameterException {

    if (parameterSpec instanceof EpidSignatureParameterSpec) {
      EpidSignatureParameterSpec params = (EpidSignatureParameterSpec) parameterSpec;
      setNonce(params.getNonce());
      setTaId(params.getTaId());

    } else {
      throw new InvalidParameterException();
    }
  }

  @Deprecated
  @Override
  protected Object engineGetParameter(String s) throws InvalidParameterException {
    return null;
  }

  protected EpidKey getKeySpec() {
    return keySpec;
  }

  protected void setKeySpec(EpidKey keySpec) {
    this.keySpec = keySpec;
  }

  protected ByteArrayOutputStream getMessageBuffer() {
    return messageBuffer;
  }

  protected void setMessageBuffer(ByteArrayOutputStream messageBuffer) {
    this.messageBuffer = messageBuffer;
  }
}
