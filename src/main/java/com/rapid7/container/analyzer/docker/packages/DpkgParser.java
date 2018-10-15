package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.PackageType;
import java.util.regex.Pattern;

public class DpkgParser extends PackageParser {

  private static final Pattern DPKG_PATTERN = Pattern.compile("(?<name>.*): (?<value>.*)");

  public DpkgParser() {
    super(DPKG_PATTERN, PackageType.DPGK, new PackageKeys() {

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
      public int getSizeScale() {
        return 1000;
      }
    });
  }
}
