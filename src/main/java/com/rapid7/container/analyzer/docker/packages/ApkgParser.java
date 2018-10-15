package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.PackageType;
import java.util.regex.Pattern;

public class ApkgParser extends PackageParser {

  private static final Pattern APKG_PATTERN = Pattern.compile("(?<name>.*):(?<value>.*)");

  public ApkgParser() {
    super(APKG_PATTERN, PackageType.APGK, new PackageKeys() {

      @Override
      public String getPackageKey() {
        return "P";
      }

      @Override
      public String getSourceKey() {
        return "o";
      }

      @Override
      public String getVersionKey() {
        return "V";
      }

      @Override
      public String getLicenseKey() {
        return "L";
      }

      @Override
      public String getDescriptionKey() {
        return "T";
      }

      @Override
      public String getMaintainerKey() {
        return "m";
      }

      @Override
      public String getHomePageKey() {
        return "U";
      }

      @Override
      public String getSizeKey() {
        return "I";
      }
    });
  }
}
