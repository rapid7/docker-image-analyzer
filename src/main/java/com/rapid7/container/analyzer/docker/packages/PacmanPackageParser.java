package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.container.analyzer.docker.model.image.PackageValidationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacmanPackageParser implements PackageParser<InputStream> {

  private static final Pattern PACMAN_DESC_PATTERN = Pattern.compile(".*/lib/pacman/local/(?<name>.*)/desc");
  private static final Logger LOGGER = LoggerFactory.getLogger(PacmanPackageParser.class);

  @Override
  public boolean supports(String name, TarArchiveEntry entry) {
    return !entry.isSymbolicLink() && PACMAN_DESC_PATTERN.matcher(name).matches();
  }

  public Set<Package> parse(InputStream input, OperatingSystem operatingSystem) throws FileNotFoundException, IOException {
    Set<Package> packages = new HashSet<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String line = null;
      String pkg = null;
      String source = null;
      String version = null;
      long installedSize = -1L;
      String maintainer = null;
      String homepage = null;
      String description = null;
      String license = null;
      String token = "name";
      while ((line = reader.readLine()) != null) {

        if (line.startsWith("%"))
          token = line;
        else if (line != null && !line.isEmpty()) {
          switch (token) {
            case "%NAME%":
              if (pkg != null) {
                try {
                  packages.add(new Package(source, PackageType.PACMAN, operatingSystem, pkg, version, description, installedSize, maintainer, homepage, license));
                } catch (PackageValidationException pve) {
                  LOGGER.warn(pve.getMessage());
                }
              }

              pkg = line;
              break;
            case "%VERSION%":
              version = line;
              break;
            case "%DESC%":
              description = line;
              break;
            case "%URL%":
              homepage = line;
              break;
            case "%PACKAGER%":
              maintainer = line;
              break;
            case "%SIZE%":
              installedSize = Long.parseLong(line);
              break;
            case "%LICENSE%":
              license = line;
              break;
            default:
              break;
          }
        }
      }

      if (pkg != null) {
        try {
          packages.add(new Package(source, PackageType.PACMAN, operatingSystem, pkg, version, description, installedSize, maintainer, homepage, license));
        } catch (PackageValidationException pve) {
          LOGGER.warn(pve.getMessage());
        }
      }
    }

    return packages;
  }

  public static interface PackageKeys {

    public String getPackageKey();

    public String getSourceKey();

    public String getVersionKey();

    public String getDescriptionKey();

    public String getMaintainerKey();

    public String getHomePageKey();

    public String getSizeKey();

    public default String getLicenseKey() {
      return "License";
    }

    public default int getSizeScale() {
      return 1;
    }
  }
}
