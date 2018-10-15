package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.os.Fingerprinter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class OsReleaseFingerprinter implements LayerFileHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OsReleaseFingerprinter.class);
  private static final Pattern FILENAME_PATTERN = Pattern.compile("(?i:^((?:\\.)?(?:/)?etc/.*-release|(?:\\.)?(?:/)?usr/lib/os-release)$)");
  private Fingerprinter osReleaseParser;

  public OsReleaseFingerprinter(Fingerprinter osReleaseParser) {
    this.osReleaseParser = osReleaseParser;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, Layer layer) throws IOException {
    if (!entry.isSymbolicLink() && FILENAME_PATTERN.matcher(name).matches())
      if (layer.getOperatingSystem() == null || name.endsWith("/os-release")) {
        OperatingSystem os = osReleaseParser.parse(contents, name, convert(configuration.getArchitecture()));
        if (os != null) {
          MDC.put("operating_system", os.toString());
          try {
            LOGGER.info("Operating system detected on layer.");
            layer.setOperatingSystem(os);

            // given all packages detected on the layer this OS reference
            layer.getPackages().forEach(pkg -> pkg.setOperatingSystem(os));
          } finally {
            MDC.remove("operating_system");
          }
        }
      }
  }

  // maps values to what content expects
  private String convert(String architecture) {
    switch (architecture) {
      case "amd64":
        return "x86_64";
      case "386":
        return "x86";
      case "arm":
        return "ARM";
      default:
        return architecture;
    }
  }
}
