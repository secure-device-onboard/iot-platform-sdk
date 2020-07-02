// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ocs.fsimpl.fs;

import static java.nio.charset.StandardCharsets.US_ASCII;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.util.encoders.Hex;
import org.sdo.iotplatformsdk.common.rest.CipherOperation;
import org.sdo.iotplatformsdk.common.rest.DeviceCryptoInfo;
import org.sdo.iotplatformsdk.common.rest.DeviceState;
import org.sdo.iotplatformsdk.common.rest.Iso8061Timestamp;
import org.sdo.iotplatformsdk.common.rest.Message41Store;
import org.sdo.iotplatformsdk.common.rest.Message45Store;
import org.sdo.iotplatformsdk.common.rest.Message47Store;
import org.sdo.iotplatformsdk.common.rest.ModuleMessage;
import org.sdo.iotplatformsdk.common.rest.OwnerVoucher;
import org.sdo.iotplatformsdk.common.rest.OwnerVoucherEntry;
import org.sdo.iotplatformsdk.common.rest.RendezvousInstruction;
import org.sdo.iotplatformsdk.common.rest.SetupInfoResponse;
import org.sdo.iotplatformsdk.common.rest.SignatureResponse;
import org.sdo.iotplatformsdk.common.rest.SviMessage;
import org.sdo.iotplatformsdk.common.rest.To0Request;
import org.sdo.iotplatformsdk.common.rest.To2DeviceSessionInfo;
import org.sdo.iotplatformsdk.ocs.fsimpl.rest.FsRestClient;
import org.sdo.iotplatformsdk.ocs.services.DataManager;
import org.sdo.iotplatformsdk.ocs.services.DataObject;
import org.sdo.iotplatformsdk.ocs.services.OcsRestContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A file-system based implementation of the {@link OcsRestContract}.
 */
public class FsOcsContractImpl implements OcsRestContract {

  private static final Logger LOGGER = LoggerFactory.getLogger(FsOcsContractImpl.class);
  private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private final DataManager dataManager;
  private final ObjectMapper mapper;
  private final FsRestClient fsRestClient;

  private final int to0WaitSeconds = FsPropertiesLoader.getProperty("to0.waitseconds") != null
      ? Integer.parseInt(FsPropertiesLoader.getProperty("to0.waitseconds"))
      : 3600;
  private final String rootDir = FsPropertiesLoader.getProperty("fs.root.dir");
  //path to owner keystore.
  private final String ownerKeystoreDir = FsPropertiesLoader.getProperty("fs.owner.keystore");
  //password of owner keystore and its underlying entries.
  private final String ownerKeystorePwd =
      FsPropertiesLoader.getProperty("fs.owner.keystore-password");
  private final String devicesDir = FsPropertiesLoader.getProperty("fs.devices.dir");
  private final String valuesDir = FsPropertiesLoader.getProperty("fs.values.dir");
  private final boolean to2ReuseEnabled =
      FsPropertiesLoader.getProperty("to2.credential-reuse.enabled") != null
          ? Boolean.valueOf(FsPropertiesLoader.getProperty("to2.credential-reuse.enabled"))
          : false;
  private final boolean to2ResaleEnabled =
      FsPropertiesLoader.getProperty("to2.owner-resale.enabled") != null
          ? Boolean.valueOf(FsPropertiesLoader.getProperty("to2.owner-resale.enabled"))
          : true;
  // interval at which to0 scheduler will run repeatedly. Default: 60 seconds.
  private final int toSchedulerInterval =
      FsPropertiesLoader.getProperty("to0.scheduler.interval") != null
          ? Integer.parseInt(FsPropertiesLoader.getProperty("to0.scheduler.interval"))
          : 60;

  private static final String CIPHER = "RSA/NONE/OAEPWithSHA256AndMGF1Padding";
  private static final Character QUOTE = '"';
  private static final String[] OWNER_ALGORITHMS = new String[] {"EC", "RSA"};
  private static final String OWNER_KEYSTORE_TYPE = "PKCS12";

  /**
   * The constructor is responsible for scheduling the the devices for TO0. It sends requests to
   * To0Scheduler at a fixed interval. It also initializes the necessary
   */
  public FsOcsContractImpl(ObjectMapper mapper) {
    this.fsRestClient = new FsRestClient();
    this.dataManager = new FsDataManager();
    this.mapper = mapper;
    scheduler.scheduleWithFixedDelay(new Runnable() {

      @Override
      public void run() {
        try {
          final To0Request request = new To0Request();
          request.setGuids(getDevicesForScheduling());
          request.setWaitSeconds(String.valueOf(to0WaitSeconds));
          fsRestClient.postDevicesForTo0(request);
        } catch (Exception e) {
          LOGGER.error("Error occurred at the scheduled executor service: " + e.getMessage());
        }
      }
    }, 5, toSchedulerInterval, TimeUnit.SECONDS);
  }

  /*
   * Reads and returns the device voucher file named voucher.json, identified by the deviceId.
   */
  @Override
  public String getDeviceVoucher(final String deviceId) throws Exception {
    final String key = getVoucherKey(deviceId);

    try (InputStream in = getDataManager().getObject(key).getInputStream()) {

      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }

  }

  /*
   * Stores the received contents of owner voucher for the specified device in the file named
   * voucher.json.
   */
  @Override
  public void putDeviceVoucher(final String voucher) throws Exception {

    // extract the guid.
    final String guid = getGuidFromOwnershipVoucher(voucher);
    final String key = getVoucherKey(guid);

    try (ByteArrayInputStream input =
        new ByteArrayInputStream(voucher.getBytes(StandardCharsets.UTF_8))) {
      getDataManager().putObject(key, input);
    }
  }

  /*
   * Reads and returns the device state file named state.json, identified by the deviceId.
   */
  @Override
  public DeviceState getDeviceState(final String deviceId) throws Exception {
    final String key = getStateKey(deviceId);

    try (InputStream in = getDataManager().getObject(key).getInputStream()) {
      return getObjectMapper().readValue(in, DeviceState.class);
    }

  }

  /*
   * Stores the specified device state for the device in the file named state.json. To maintain the
   * contents of previous state, which don't need to be overridden, read the state and put it back
   * into the file.
   */
  @Override
  public void postDeviceState(final String deviceId, final DeviceState postInfo) throws Exception {

    final String key = getStateKey(deviceId);
    DeviceState current;
    try {
      current = getDeviceState(deviceId);
    } catch (IOException e) {
      // no device state is present. Could be a new device. Create a new state in such
      // a scenario.
      current = new DeviceState();
    }

    if (postInfo.getTo2State().isPresent()) {
      current.setTo2State(postInfo.getTo2State());
    }
    if (postInfo.getTo2Timestamp().isPresent()) {
      current.setTo2Timestamp(postInfo.getTo2Timestamp());
    }
    current.setTo2Error(postInfo.getTo2Error());

    if (postInfo.getTo0Timestamp().isPresent()) {
      current.setTo0Timestamp(postInfo.getTo0Timestamp());
    }
    if (postInfo.getTo0Ws().isPresent()) {
      current.setTo0Ws(postInfo.getTo0Ws());
    }
    current.setTo0Error(postInfo.getTo0Error());

    if (postInfo.getG3().isPresent()) {
      current.setG3(postInfo.getG3());
    }

    try (ByteArrayInputStream input =
        new ByteArrayInputStream(getObjectMapper().writeValueAsBytes(current))) {
      getDataManager().putObject(key, input);
    }
  }

  /*
   * Reads and returns the contents of device's file svi.json.
   */
  @Override
  public SviMessage[] getMessage(final String deviceId) throws Exception {

    final String key = getSviKey(deviceId);

    final ObjectMapper mapper = getObjectMapper();

    try (InputStream in = getDataManager().getObject(key).getInputStream()) {
      SviMessage[] messages = mapper.readValue(in, new TypeReference<SviMessage[]>() {});

      for (SviMessage message : messages) {
        if (message.getValueLen() < 0) {
          final String valueKey = getValueKey(message.getValueId());
          final DataObject dataObj = getDataManager().getObject(valueKey);
          message.setValueLen(((Long) dataObj.getContentLength()).intValue());
        }
      }
      return messages;
    }
  }

  /*
   * Stores the received device service-info into the device's file dvi.json.
   */
  @Override
  public void postMessage(final String deviceId, final ModuleMessage[] message) throws Exception {
    final String valueKey = getDviKey(deviceId);
    final ObjectMapper mapper = getObjectMapper();
    ModuleMessage[] currentMessages = null;
    ModuleMessage[] combinedMessages = null;

    // restore current messages first, as they need to be kept. Avoid overriding the previous
    // messages.
    try (InputStream in = getDataManager().getObject(valueKey).getInputStream()) {
      currentMessages = mapper.readValue(in, new TypeReference<ModuleMessage[]>() {});
    } catch (Exception e) {
      // do nothing as there may not be a device serviceinfo file yet.
    }
    if (null != currentMessages) {
      combinedMessages = Stream.concat(Arrays.stream(currentMessages), Arrays.stream(message))
          .toArray(ModuleMessage[]::new);
      try (ByteArrayInputStream input =
          new ByteArrayInputStream(getObjectMapper().writeValueAsBytes(combinedMessages))) {
        getDataManager().putObject(valueKey, input);
      }
    } else {
      try (ByteArrayInputStream input =
          new ByteArrayInputStream(getObjectMapper().writeValueAsBytes(message))) {
        getDataManager().putObject(valueKey, input);
      }
    }
  }

  /*
   * Reads and returns the contents of the file identified by the file named valueId, from start to
   * end index. The device identifier is not in use in this implementation.
   */
  @Override
  public byte[] getValue(final String deviceId, final String valueId, int startParam, int endParam)
      throws Exception {
    final String valueKey = getValueKey(valueId);

    return getDataManager().getObject(valueKey).withRange(startParam, endParam).getInputStream()
        .readAllBytes();

  }

  /*
   * Reads and returns the contents of device's file psi.json.
   */
  @Override
  public ModuleMessage[] getPsi(String deviceId) throws Exception {
    final String key = getPsiKey(deviceId);

    final ObjectMapper mapper = getObjectMapper();

    try (InputStream in = getDataManager().getObject(key).getInputStream()) {
      ModuleMessage[] messages = mapper.readValue(in, new TypeReference<ModuleMessage[]>() {});
      return messages;

    }
  }

  /*
   * Returns a random UUID as device identifier if cred-reuse is not enabled, else returns the same
   * device identifier along with and an empty list.
   */
  @Override
  public SetupInfoResponse getSetupInfo(final String deviceId) throws Exception {
    final SetupInfoResponse response = new SetupInfoResponse();

    if (to2ReuseEnabled) {
      response.setG3(deviceId);
    } else {
      response.setG3(UUID.randomUUID().toString());
    }
    response.setR3(Collections.<RendezvousInstruction>emptyList());
    return response;
  }

  /*
   * Stores the error information in the devices's file state.json.
   */
  @Override
  public void postErrors(final String deviceId, final DeviceState errorState) throws Exception {
    postDeviceState(deviceId, errorState);
  }

  /*
   * Signs the received contents using the private key and returns the signature, the corresponding
   * public key and its algorithm name.
   */
  @Override
  public SignatureResponse postSignature(final String deviceId, final String input)
      throws Exception {

    final PublicKey publicKey;
    PrivateKey signingKey = null;
    try {
      final KeyPair keyPair = findOwnerKeyPair(deviceId);
      if (keyPair != null && keyPair.getPublic() != null && keyPair.getPrivate() != null) {
        publicKey = keyPair.getPublic();
        signingKey = keyPair.getPrivate();
        final Signature signer = Signatures.getInstance(publicKey);
        signer.initSign(signingKey);
        signer.update(US_ASCII.encode(input));
        final byte[] sg = signer.sign();
        final String publicKeyAsString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        final String signatureAsString = Base64.getEncoder().encodeToString(sg);
        final SignatureResponse signatureResponse = new SignatureResponse();
        signatureResponse.setPk(publicKeyAsString);
        signatureResponse.setAlg(publicKey.getAlgorithm());
        signatureResponse.setSg(signatureAsString);
        return signatureResponse;
      } else {
        throw new RuntimeException("No owner key-pair found corresponding to " + deviceId);
      }
    } catch (Exception e) {
      LOGGER.debug(e.getMessage(), e);
      throw e;
    } finally {
      try {
        if (null != signingKey) {
          signingKey.destroy();
        }
      } catch (DestroyFailedException e) {
        // nothing to do.
      }
    }

  }

  /*
   * Perform cipher operations on the input byte array using the single owner's key-pair. Since we
   * have a single owner key-pair, deviceId is not used.
   */
  @Override
  public byte[] cipherOperations(String deviceId, final byte[] buf, final String cipherOp)
      throws Exception {
    final KeyPair keyPair = findOwnerKeyPair(deviceId);
    if (keyPair != null && keyPair.getPublic() != null && keyPair.getPrivate() != null) {
      if (cipherOp.equals(CipherOperation.ENCIPHER.toString())) {
        try {
          final PublicKey pubKey = keyPair.getPublic();
          final Cipher cipher = Cipher.getInstance(CIPHER, BouncyCastleSupplier.load());
          cipher.init(Cipher.ENCRYPT_MODE, pubKey);
          final byte[] b = cipher.doFinal(buf);
          return b;

        } catch (Exception e) {
          LOGGER.error(e.getMessage());
          throw new Exception(e);
        }
      } else if (cipherOp.equals(CipherOperation.DECIPHER.toString())) {
        try {
          final PrivateKey privKey = keyPair.getPrivate();
          final Cipher cipher = Cipher.getInstance(CIPHER, BouncyCastleSupplier.load());
          cipher.init(Cipher.DECRYPT_MODE, privKey);
          final byte[] b = cipher.doFinal(buf);
          return b;

        } catch (Exception e) {
          LOGGER.error(e.getMessage());
          throw new Exception(e);
        }
      } else {
        throw new UnsupportedOperationException();
      }
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public To2DeviceSessionInfo getDeviceSessionInfo(final String deviceId) throws Exception {
    final String msg41StoreKey = getMessage41StoreKey(deviceId);
    final String msg45StoreKey = getMessage45StoreKey(deviceId);
    final String msg47StoreKey = getMessage47StoreKey(deviceId);
    final String sessionInfoKey = getSessionInfoKey(deviceId);

    Message41Store msg41Store;
    Message45Store msg45Store;
    Message47Store msg47Store;
    DeviceCryptoInfo sessionInfo;

    final ObjectMapper mapper = getObjectMapper();

    try (InputStream in = getDataManager().getObject(msg41StoreKey).getInputStream()) {
      msg41Store = mapper.readValue(in, new TypeReference<Message41Store>() {});
    } catch (Exception e) {
      // there will not be a session information at the start. This is expected.
      msg41Store = new Message41Store();
    }
    try (InputStream in = getDataManager().getObject(msg45StoreKey).getInputStream()) {
      msg45Store = mapper.readValue(in, new TypeReference<Message45Store>() {});
    } catch (

    Exception e) {
      // there will not be a session information at the start. This is expected.
      msg45Store = new Message45Store();
    }
    try (InputStream in = getDataManager().getObject(msg47StoreKey).getInputStream()) {
      msg47Store = mapper.readValue(in, new TypeReference<Message47Store>() {});
    } catch (Exception e) {
      // there will not be a session information at the start. This is expected.
      msg47Store = new Message47Store();
    }
    try (InputStream in = getDataManager().getObject(sessionInfoKey).getInputStream()) {
      sessionInfo = mapper.readValue(in, new TypeReference<DeviceCryptoInfo>() {});
    } catch (Exception e) {
      // there will not be a session information at the start. This is expected.
      sessionInfo = new DeviceCryptoInfo();
    }

    final To2DeviceSessionInfo to2DeviceSessionInfo =
        new To2DeviceSessionInfo(msg41Store, msg45Store, msg47Store, sessionInfo);
    return to2DeviceSessionInfo;
  }

  @Override
  public void postDeviceSessionInfo(final String deviceId,
      final To2DeviceSessionInfo to2DeviceSessionInfo) throws Exception {

    To2DeviceSessionInfo currentSessionInfo = null;
    // restoring current TO2 session information for the device.
    try {
      currentSessionInfo = getDeviceSessionInfo(deviceId);
    } catch (Exception e) {
      // there will not be a session information at the start. This is expected.
    }

    final String msg41StoreKey = getMessage41StoreKey(deviceId);
    final String msg45StoreKey = getMessage45StoreKey(deviceId);
    final String msg47StoreKey = getMessage47StoreKey(deviceId);
    final String deviceCryptoInfoKey = getSessionInfoKey(deviceId);

    if (null == to2DeviceSessionInfo.getMessage41Store()
        && null != currentSessionInfo.getMessage41Store()) {
      try (ByteArrayInputStream input = new ByteArrayInputStream(
          getObjectMapper().writeValueAsBytes(currentSessionInfo.getMessage41Store()))) {
        getDataManager().putObject(msg41StoreKey, input);
      }
    } else {
      try (ByteArrayInputStream input = new ByteArrayInputStream(
          getObjectMapper().writeValueAsBytes(to2DeviceSessionInfo.getMessage41Store()))) {
        getDataManager().putObject(msg41StoreKey, input);
      }
    }

    if (null == to2DeviceSessionInfo.getMessage45Store()
        && null != currentSessionInfo.getMessage45Store()) {
      try (ByteArrayInputStream input = new ByteArrayInputStream(
          getObjectMapper().writeValueAsBytes(currentSessionInfo.getMessage45Store()))) {
        getDataManager().putObject(msg45StoreKey, input);
      }
    } else {
      try (ByteArrayInputStream input = new ByteArrayInputStream(
          getObjectMapper().writeValueAsBytes(to2DeviceSessionInfo.getMessage45Store()))) {
        getDataManager().putObject(msg45StoreKey, input);
      }
    }

    if (null == to2DeviceSessionInfo.getMessage47Store()
        && null != currentSessionInfo.getMessage47Store()) {
      try (ByteArrayInputStream input = new ByteArrayInputStream(
          getObjectMapper().writeValueAsBytes(currentSessionInfo.getMessage47Store()))) {
        getDataManager().putObject(msg47StoreKey, input);
      }
    } else {
      try (ByteArrayInputStream input = new ByteArrayInputStream(
          getObjectMapper().writeValueAsBytes(to2DeviceSessionInfo.getMessage47Store()))) {
        getDataManager().putObject(msg47StoreKey, input);
      }
    }
    if (null == to2DeviceSessionInfo.getDeviceCryptoInfo()
        && null != currentSessionInfo.getDeviceCryptoInfo()) {
      try (ByteArrayInputStream input = new ByteArrayInputStream(
          getObjectMapper().writeValueAsBytes(currentSessionInfo.getDeviceCryptoInfo()))) {
        getDataManager().putObject(deviceCryptoInfoKey, input);
      }
    } else {
      try (ByteArrayInputStream input = new ByteArrayInputStream(
          getObjectMapper().writeValueAsBytes(to2DeviceSessionInfo.getDeviceCryptoInfo()))) {
        getDataManager().putObject(deviceCryptoInfoKey, input);
      }
    }
  }

  @Override
  public void deleteDeviceSessionInfo(final String deviceId) throws Exception {
    final String msg41StoreKey = getMessage41StoreKey(deviceId);
    final String msg45StoreKey = getMessage45StoreKey(deviceId);
    final String msg47StoreKey = getMessage47StoreKey(deviceId);
    final String sessionInfo = getSessionInfoKey(deviceId);

    getDataManager().removeObject(msg41StoreKey);
    getDataManager().removeObject(msg45StoreKey);
    getDataManager().removeObject(msg47StoreKey);
    getDataManager().removeObject(sessionInfo);
  }

  /*
   * Extract guid from the ownership voucher's 'g' field.
   */
  private String getGuidFromOwnershipVoucher(final String voucher)
      throws JsonParseException, JsonMappingException, IOException {
    TypeReference<HashMap<String, Object>> typeRef =
        new TypeReference<HashMap<String, Object>>() {};
    final HashMap<String, Object> voucherMap = getObjectMapper().readValue(voucher, typeRef);
    final HashMap<String, Object> ownershipVoucherHeader = (HashMap) voucherMap.get("oh");
    final String encodedGuid = (String) ownershipVoucherHeader.get("g");
    final String hexGuid = Hex.toHexString(java.util.Base64.getDecoder().decode(encodedGuid));
    final String guid = hexGuid.replaceFirst(
        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
        "$1-$2-$3-$4-$5");
    return guid;
  }

  /**
   * Extract the owner public key from the ownership voucher for the given device identifier.
   *
   * @param deviceId the device identifier
   * @return {@link PublicKey} instance containing owner's public key
   * @throws Exception thrown when an exception occurs
   */
  private PublicKey getVoucherPublicKey(final String deviceId) throws Exception {
    final String voucher = getDeviceVoucher(deviceId);
    if (null == voucher) {
      throw new IOException("Ownership voucher with " + deviceId + " does not exist.");
    }
    final OwnerVoucher ownerVoucher = getObjectMapper().readValue(voucher, OwnerVoucher.class);
    final int enSize = ownerVoucher.getEn().size();
    if (enSize < 1) {
      // if there are no owner entries, there is nothing to extract.
      throw new Exception("Ownership voucher with " + deviceId + " has no owner entries.");
    }
    // get the last ownership voucher entry, ie, 'en-1' entry.
    final OwnerVoucherEntry voucherEntry = ownerVoucher.getEn().get(enSize - 1);
    // 2nd element is the public key encoding in the ownership voucher entry body.
    final int encoding = (int) voucherEntry.getBo().getPk().get(1);
    // 3rd element is the public key in the ownership voucher entry body.
    final List<Object> pkList = (ArrayList<Object>) voucherEntry.getBo().getPk().get(2);
    final PublicKey publicKey;
    switch (encoding) {
      case 0:
        // No Public key present.
        throw new Exception();
      case 1:
        // Public key with X.509 encoding.
        final String encodedPublicKey = QUOTE + (String) pkList.get(1) + QUOTE;
        publicKey =
            toPublicKey(new X509EncodedKeySpec(toHex(CharBuffer.wrap(encodedPublicKey)).array()));
        break;
      case 3:
        // Public key with RSAMODEXP encoding.
        final String mod = QUOTE + (String) pkList.get(1) + QUOTE;
        final String exp = QUOTE + (String) pkList.get(3) + QUOTE;
        publicKey =
            toPublicKey(new RSAPublicKeySpec(new BigInteger(1, toHex(CharBuffer.wrap(mod)).array()),
                new BigInteger(1, toHex(CharBuffer.wrap(exp)).array())));
        break;
      default:
        throw new Exception();
    }
    return publicKey;
  }

  /**
   * Find the matching owner {@link KeyPair} for the device identifier by comparing the certificate
   * present in the ownership voucher to the ones present in the keystore. Returns null if no match
   * is found.
   *
   * @param deviceId the device identifier
   * @return KeyPair containing the public and private key.
   * @throws Exception when an exception occurs.
   */
  private KeyPair findOwnerKeyPair(final String deviceId) throws Exception {
    final KeyStore ownerKeyStore = KeyStore.getInstance(OWNER_KEYSTORE_TYPE);
    final File keystoreFile = new File(this.rootDir + File.separator + this.ownerKeystoreDir);
    ownerKeyStore.load(new FileInputStream(keystoreFile), this.ownerKeystorePwd.toCharArray());
    final PublicKey voucherPublicKey = getVoucherPublicKey(deviceId);
    if (null != voucherPublicKey) {
      final Iterator<String> aliases = ownerKeyStore.aliases().asIterator();
      while (aliases.hasNext()) {
        final String alias = aliases.next();
        final Certificate certificate = ownerKeyStore.getCertificate(alias);
        if (null == certificate) {
          continue;
        }
        if (Arrays.equals(certificate.getPublicKey().getEncoded(), voucherPublicKey.getEncoded())) {
          final KeyPair keyPair = new KeyPair(certificate.getPublicKey(), toPrivateKey(
              ownerKeyStore.getKey(alias, this.ownerKeystorePwd.toCharArray()).getEncoded()));
          return keyPair;
        }
      }
    }
    return null;
  }

  /**
   * Return the {@link PublicKey} instance by trying to generate the public key using the given
   * {@link KeySpec}.
   *
   * @param key the public key
   * @return {@link PrivateKey}
   * @throws NoSuchAlgorithmException thrown when such an exception occurs
   */
  private PublicKey toPublicKey(final KeySpec keySpec) throws NoSuchAlgorithmException {

    for (String algorithm : OWNER_ALGORITHMS) {
      try {
        final KeyFactory factory = KeyFactory.getInstance(algorithm);
        final PublicKey key = factory.generatePublic(keySpec);
        return key;

      } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
        // failures are expected, try the next algorithm
      }
    }
    throw new NoSuchAlgorithmException(keySpec.toString());
  }

  /**
   * Return the {@link PrivateKey} instance by trying to generate the private key using the given
   * byte array.
   *
   * @param key the private key
   * @return {@link PrivateKey}
   * @throws NoSuchAlgorithmException thrown when such an exception occurs
   */
  private PrivateKey toPrivateKey(final byte[] key) throws NoSuchAlgorithmException {
    final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
    for (String algorithm : OWNER_ALGORITHMS) {
      try {
        final KeyFactory factory = KeyFactory.getInstance(algorithm);
        final PrivateKey privateKey = factory.generatePrivate(keySpec);
        return privateKey;
      } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
        // failures are expected, try the next algorithm
      }
    }
    throw new NoSuchAlgorithmException(keySpec.toString());
  }

  /**
   * Convert the input {@link CharBuffer} to Hex ByteBuffer.
   *
   * @param in character buffer
   * @return corresponding byte buffer.
   * @throws IOException thrown when such an exception occurs
   */
  private ByteBuffer toHex(final CharBuffer in) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    WritableByteChannel byteChannel = Channels.newChannel(bytes);
    char[] b64 = new char[4];
    Character ch = in.get();
    assert ch.equals(QUOTE);
    for (;;) {
      b64[0] = in.get();
      if (QUOTE.equals(b64[0])) {
        return ByteBuffer.wrap(bytes.toByteArray());
      } else {
        in.get(b64, 1, 3);
      }
      ByteBuffer b64AsIso8859 = StandardCharsets.ISO_8859_1.encode(CharBuffer.wrap(b64));
      ByteBuffer decodedBytes = Base64.getDecoder().decode(b64AsIso8859);
      byteChannel.write(decodedBytes);
    }
  }

  /**
   * This method is responsible for generating the list of device identifiers that needs TO0
   * scheduling by scanning the file state.json for each device. If the combined to0 time-stamp and
   * wait seconds time is more than the current time, or, if there's no associated to0 state present
   * for the device, the device is added to the list.
   *
   * @return array of device identifiers eligible for TO0 scheduling.
   * @throws Exception any generic exception.
   */
  public synchronized String[] getDevicesForScheduling() throws Exception {
    final List<String> responseAsJson = new ArrayList<String>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(rootDir, devicesDir))) {

      for (Path path : stream) {
        final File file = path.toFile();
        if (file.isDirectory()) {
          final String voucher;
          final String guid;
          final OwnerVoucher ownerVoucher;
          try {
            voucher = getDeviceVoucher(file.getName());
            guid = getGuidFromOwnershipVoucher(voucher);
            ownerVoucher = getObjectMapper().readValue(voucher, OwnerVoucher.class);
          } catch (Exception e) {
            // invalid/missing voucher. continue looking for the next device.
            continue;
          }
          // if there are no owner entries, do not schedule.
          final int enSize = ownerVoucher.getEn().size();
          if (enSize < 1) {
            LOGGER.debug("The device with guid " + guid
                + " has no owner entries. This device is not being scheduled for TO0");
            continue;
          }
          if (!guid.equals(file.getName())) {
            LOGGER.warn("The device guid in ownership voucher '" + guid
                + "' and its parent directory name '" + file.getName()
                + "' do not match in the filesystem.");
            continue;
          }
          final File stateFile = new File(file.getAbsolutePath(), "state.json");
          if (!stateFile.exists()) {
            // Add the guid as it might have been just added, with no state.
            responseAsJson.add(file.getName());
            continue;
          }
          try (InputStream in =
              getDataManager().getObject(getStateKey(file.getName())).getInputStream()) {
            final DeviceState deviceState;
            try {
              deviceState = getObjectMapper().readValue(in, DeviceState.class);
            } catch (Exception e) {
              // invalid/corrupt state. continue looking for the next device.
              continue;
            }

            // if to0 is not done for a device, to0timestamp is not present,
            // else, if it is present, then check for to0ws.
            if (!deviceState.getTo0Timestamp().isPresent()) {
              responseAsJson.add(file.getName());
            } else if (deviceState.getTo0Ws().isPresent()) {
              if (Iso8061Timestamp.instantPlusSeconds(deviceState.getTo0Timestamp().get(),
                  deviceState.getTo0Ws().get()).isBefore(Instant.now())) {
                responseAsJson.add(file.getName());
              }
            }
            // set a delay of 300 seconds before trying to schedule again.
            deviceState.setTo0Ws(Optional.of(300));
          }
        }
      }
    } catch (Exception e) {
      LOGGER.warn("Error occurred while checking for proxies ", e.getMessage());
      LOGGER.debug(e.getMessage(), e);
    }
    final String[] response = responseAsJson.stream().toArray(String[]::new);
    LOGGER.debug("Scheduling the following devices for Transfer Ownership Protocol 0: "
        + responseAsJson.toString());
    return response;
  }

  private DataManager getDataManager() {
    return dataManager;
  }

  private ObjectMapper getObjectMapper() {
    return mapper;
  }

  /**
   * Returns the relative path of the device's voucher.json.
   *
   * @param deviceId the device identifier.
   * @return relative path of voucher.json.
   */
  private String getVoucherKey(String deviceId) {
    return String.format("%1$s/%2$s/voucher.json", devicesDir, deviceId);
  }

  /**
   * Returns the relative path of the device's svi.json.
   *
   * @param deviceId the device identifier.
   * @return relative path of svi.json.
   */
  private String getSviKey(String deviceId) {
    return String.format("%1$s/%2$s/svi.json", devicesDir, deviceId);
  }

  /**
   * Returns the relative path of the device's psi.json.
   *
   * @param deviceId the device identifier.
   * @return relative path of psi.json.
   */
  private String getPsiKey(String deviceId) {
    return String.format("%1$s/%2$s/psi.json", devicesDir, deviceId);
  }

  /**
   * Returns the relative path of the device's dvi.json.
   *
   * @param deviceId the device identifier.
   * @return relative path of dvi.json.
   */
  private String getDviKey(String deviceId) {
    return String.format("%1$s/%2$s/dvi.json", devicesDir, deviceId);
  }

  /**
   * Returns the relative path of file identified by valueId in the values directory.
   *
   * @param valueId file-name of the file inside values directory.
   * @return
   */
  private String getValueKey(String valueId) {
    return String.format("%1$s/%2$s", valuesDir, valueId);
  }

  /**
   * Returns the relative path of the device's state.json.
   *
   * @param deviceId deviceId the device identifier.
   * @return relative path of state.json.
   */
  private String getStateKey(String deviceId) {
    return String.format("%1$s/%2$s/state.json", devicesDir, deviceId);
  }

  /**
   * Returns the relative path of the device's sessionInfo.json.
   *
   * @param deviceId deviceId the device identifier.
   * @return relative path of
   */
  private String getSessionInfoKey(String deviceId) {
    return String.format("%1$s/%2$s/sessionInfo.json", devicesDir, deviceId);
  }

  /**
   * Returns the relative path of the device's message41Store.json.
   *
   * @param deviceId deviceId the device identifier.
   * @return relative path of
   */
  private String getMessage41StoreKey(String deviceId) {
    return String.format("%1$s/%2$s/message41Store.json", devicesDir, deviceId);
  }

  /**
   * Returns the relative path of the device's message45Store.json.
   *
   * @param deviceId deviceId the device identifier.
   * @return relative path of
   */
  private String getMessage45StoreKey(String deviceId) {
    return String.format("%1$s/%2$s/message45Store.json", devicesDir, deviceId);
  }

  /**
   * Returns the relative path of the device's message45Store.json.
   *
   * @param deviceId deviceId the device identifier.
   * @return relative path of
   */
  private String getMessage47StoreKey(String deviceId) {
    return String.format("%1$s/%2$s/message47Store.json", devicesDir, deviceId);
  }

  @Override
  public void putMessage(String deviceId, SviMessage[] sviValue) throws Exception {
    final String key = getSviKey(deviceId);
    try (ByteArrayInputStream input =
        new ByteArrayInputStream(getObjectMapper().writeValueAsBytes(sviValue))) {
      getDataManager().putObject(key, input);
    }
  }

  @Override
  public void deleteMessage(String deviceId) throws Exception {
    final String key = getSviKey(deviceId);
    getDataManager().removeObject(key);
  }

  @Override
  public void putValue(String deviceId, String valueId, byte[] value) throws Exception {
    final String key = getValueKey(valueId);
    try (ByteArrayInputStream input = new ByteArrayInputStream(value)) {
      getDataManager().putObject(key, input);
    }
  }

  @Override
  public void deleteValue(String deviceId, String valueId) throws Exception {
    final String key = getValueKey(valueId);
    getDataManager().removeObject(key);
  }

  @Override
  public void deleteDevice(String deviceId) throws Exception {
    getDataManager().removeObject(devicesDir + File.separator + deviceId);
  }

  /*
   * Ignores device identifier, and returns the universal to2ResaleEnabled flag.
   */
  @Override
  public boolean isOwnerResaleSupported(String deviceId) throws Exception {
    return to2ResaleEnabled;
  }
}
