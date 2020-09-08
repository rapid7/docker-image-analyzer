package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

public abstract class PatternPackageParser implements PackageParser<InputStream> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PatternPackageParser.class);
  private static final Set<String> statusBlacklist = Stream.of("deinstall ok config-files", "purge ok not-installed").collect(toSet());
  private Pattern pattern;
  private PackageType type;
  private PackageKeys keys;

  public PatternPackageParser(Pattern pattern, PackageType type, PackageKeys keys) {
    this.pattern = requireNonNull(pattern, "pattern");
    this.type = requireNonNull(type, "type");
    this.keys = requireNonNull(keys, "keys");
  }

  public abstract boolean supports(String name, TarArchiveEntry entry);

  public Set<Package> parse(InputStream input, OperatingSystem operatingSystem) throws FileNotFoundException, IOException {

    LOGGER.info("Parsing packages using {} with operating system {}.", getClass().getSimpleName(), operatingSystem);

    Set<Package> packages = new HashSet<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String line = null;
      String pkg = null;
      String source = null;
      String version = null;
      Long installedSize = null;
      String maintainer = null;
      String homepage = null;
      String description = null;
      String license = null;
      String epoch = null;
      String release = null;
      String status = null;
      while ((line = reader.readLine()) != null) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
          String name = matcher.group("name");
          String value = matcher.group("value");

          if (name.equals(keys.getPackageKey())) {
            if (pkg != null) {
              if (!isBlacklisted(status))
                packages.add(new Package(source, type, operatingSystem, pkg, version, description, installedSize, maintainer, homepage, license, epoch, release));
              pkg = null;
              source = null;
              version = null;
              installedSize = null;
              maintainer = null;
              homepage = null;
              description = null;
              license = null;
              epoch = null;
              release = null;
              status = null;
            }

            pkg = value;
          } else if (name.equals(keys.getSourceKey()))
            source = value;
          else if (name.equals(keys.getVersionKey()))
            version = value;
          else if (name.equals(keys.getLicenseKey()))
            license = value;
          else if (name.equals(keys.getDescriptionKey()))
            description = value;
          else if (name.equals(keys.getMaintainerKey()))
            maintainer = value;
          else if (name.equals(keys.getHomePageKey()))
            homepage = value;
          else if (name.equals(keys.getEpochKey()))
            epoch = value;
          else if (name.equals(keys.getReleaseKey()))
            release = value;
          else if (name.equals(keys.getSizeKey()))
            installedSize = Long.parseLong(value) * keys.getSizeScale();
          else if (name.equals(keys.getStatusKey()))
            status = value;
        } else {
          // TODO: wrapped content from previous section
        }
      }

      if (pkg != null) {
        if (!isBlacklisted(status))
          packages.add(new Package(source, type, operatingSystem, pkg, version, description, installedSize, maintainer, homepage, license, epoch, release));

        pkg = null;
        source = null;
        version = null;
        installedSize = null;
        maintainer = null;
        homepage = null;
        description = null;
        license = null;
        epoch = null;
        release = null;
      }
    }

    return packages;
  }

  private boolean isBlacklisted(String value) {
    return value != null && statusBlacklist.contains(value.toLowerCase());
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

    public default String getEpochKey() {
      return "Epoch";
    }

    public default String getReleaseKey() {
      return "Release";
    }

    public default int getSizeScale() {
      return 1;
    }

    public default String getStatusKey() {
      return "Status";
    }
  }
}
