// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.iotplatformsdk.ops.opsimpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sdo.iotplatformsdk.common.protocol.security.OnDieEcdsaMaterialUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpsOnDieEcdsaMaterialUtil implements OnDieEcdsaMaterialUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpsOnDieEcdsaMaterialUtil.class);

  private final String ondieEcdsaMaterialPath;
  private final String sourceUrls;
  private final boolean updateCrls;
  private static final ConcurrentHashMap<String, byte[]> crlsMap =
      new ConcurrentHashMap<String, byte[]>();
  private final String sourceUrlsDelimiter = ",";

  /**
   * Constructor.
   */
  public OpsOnDieEcdsaMaterialUtil(String ondieEcdsaMaterialPath, String sourceUrls,
      boolean updateCrls) {
    this.ondieEcdsaMaterialPath = ondieEcdsaMaterialPath;
    this.sourceUrls = sourceUrls;
    this.updateCrls = updateCrls;
    final File onDieEcdsaMaterialDir = Paths.get(ondieEcdsaMaterialPath).toFile();
    if (null == onDieEcdsaMaterialDir || !onDieEcdsaMaterialDir.exists()
        || !onDieEcdsaMaterialDir.isDirectory()) {
      LOGGER.error("On-Die ECDSA material path does not exist {}.", ondieEcdsaMaterialPath);
    }
    if (updateCrls) {
      fetchCrlsFromSources();
    }
  }

  @Override
  public byte[] getCrl(final String crlName) {
    try {
      if (!crlsMap.containsKey(crlName)) {
        final Path crl = Paths.get(ondieEcdsaMaterialPath, crlName);
        if (null != crl && !crl.toFile().exists()) {
          LOGGER.error("CRL {} not present at {}.", crlName, ondieEcdsaMaterialPath);
          throw new FileNotFoundException();
        }
        crlsMap.put(crlName, Files.readAllBytes(crl));
      }
      return crlsMap.get(crlName);
    } catch (Exception e) {
      LOGGER.error("Error in loading {} from {}.", crlName, ondieEcdsaMaterialPath);
      return new byte[0];
    }
  }

  // Download CRLs from multiple sources, separated by ','.
  private void fetchCrlsFromSources() {
    final String[] urls = sourceUrls.split(sourceUrlsDelimiter);
    for (final String url : urls) {
      try {
        LOGGER.info("Downloading CRLs from {}.", url);
        final Document document = Jsoup.connect(url).get();
        final Elements elements = document.select("a[href]");
        for (final Element element : elements) {
          for (final Attribute attribute : element.attributes()) {
            if (attribute.getKey().equalsIgnoreCase("href")
                && attribute.getValue().contains(".crl")) {
              final URL crl = new URL(new URL(url), attribute.getValue());
              Files.write(Paths.get(ondieEcdsaMaterialPath, attribute.getValue()),
                  crl.openStream().readAllBytes());
            }
          }
        }
      } catch (Exception e) {
        LOGGER.error("Unable to download on-die ecdsa material from {} due to :{}.", url,
            e.getMessage());
        LOGGER.debug(e.getMessage(), e);
      }
    }
  }
}
