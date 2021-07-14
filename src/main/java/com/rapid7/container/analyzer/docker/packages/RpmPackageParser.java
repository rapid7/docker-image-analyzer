package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.docker.model.image.PackageType;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public class RpmPackageParser extends PatternPackageParser {

  private static final Pattern RPM_PATTERN = Pattern.compile("(?<name>.*):(?<value>.*)");

  public RpmPackageParser() {
    super(RPM_PATTERN, PackageType.RPM, new PackageKeys() {

      @Override
      public String getPackageKey() {
        return "Package";
      }

      @Override
      public String getSourceKey() {
        return "Source";
      }

      @Override
      public String getVersionKey() {
        return "Version";
      }

      @Override
      public String getDescriptionKey() {
        return "Description";
      }

      @Override
      public String getMaintainerKey() {
        return "Maintainer";
      }

      @Override
      public String getHomePageKey() {
        return "Homepage";
      }

      @Override
      public String getSizeKey() {
        return "Installed-Size";
      }

      @Override
      public String getLicenseKey() {
        return "License";
      }

      @Override
      public String getEpochKey() {
        return "Epoch";
      }

      @Override
      public String getReleaseKey() {
        return "Release";
      }
    });
  }

  @Override
  public boolean supports(String name, TarArchiveEntry entry) {
    return !entry.isSymbolicLink() && name.endsWith("lib/rpm/Packages");
  }
}
