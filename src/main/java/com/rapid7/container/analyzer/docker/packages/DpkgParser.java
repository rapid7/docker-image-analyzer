package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.docker.model.image.PackageType;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public class DpkgParser extends PatternPackageParser {

  private static final Pattern DPKG_PATTERN = Pattern.compile("(?<name>.*): (?<value>.*)");

  public DpkgParser() {
    super(DPKG_PATTERN, PackageType.DPKG, new PackageKeys() {

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

  @Override
  public boolean supports(String name, TarArchiveEntry entry) {
    return !entry.isSymbolicLink() && name.endsWith("var/lib/dpkg/status");
  }
}
