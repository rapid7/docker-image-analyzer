package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.fingerprinter.OwaspDependencyFingerprinter;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.container.analyzer.docker.model.image.PackageValidationException;
import com.rapid7.container.analyzer.docker.packages.settings.OwaspDependencyParserSettingsBuilder;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.exception.ExceptionCollection;
import org.owasp.dependencycheck.utils.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.stream.Collectors.toSet;
import static org.owasp.dependencycheck.Engine.Mode.EVIDENCE_COLLECTION;

public class OwaspDependencyParser implements PackageParser<File> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OwaspDependencyFingerprinter.class);
  // TODO - Create this pattern from enabled analyzers? Would need changes in the OWASP DependencyCheck library
  private static final Pattern OWASP_DEPENDENCY_SUPPORTED_PATTERN = Pattern.compile(".*(?i)(\\.(jar|war|aar|gemspec|py|egg|zip|ear|sar|apk|nupkg|tar|gz|tgz|bz2|tbz2|whl|nuspec))|.*(?i)(Gopkg.lock|Gemfile.lock|packages.config|package.json|package-lock.json)$");
  private final Settings settings;

  public OwaspDependencyParser(OwaspDependencyParserSettingsBuilder owaspDependencyParserSettingsBuilder) {
    settings = owaspDependencyParserSettingsBuilder.build();
  }

  public OwaspDependencyParser(Settings settings) {
    this.settings = settings;
  }

  @Override
  public boolean supports(String name, TarArchiveEntry entry) {
    return !entry.isSymbolicLink() && OWASP_DEPENDENCY_SUPPORTED_PATTERN.matcher(name).matches();
  }

  @Override
  public Set<Package> parse(File file, OperatingSystem operatingSystem) {
    try (Engine engine = new Engine(EVIDENCE_COLLECTION, settings)) {
      engine.scan(file);
      try {
        engine.analyzeDependencies();
      } catch (ExceptionCollection exceptionCollection) {
        exceptionCollection.getExceptions().forEach(e -> LOGGER.error("Failed analyzing dependencies", e));
      }
      return Arrays.stream(engine.getDependencies())
          .filter(Objects::nonNull)
          .map(this::convertDependencyToPackage)
          .filter(Objects::nonNull)
          .collect(toSet());
    }
  }

  private Package convertDependencyToPackage(Dependency dependency) {

    if (dependency.getName() == null && "Java".equalsIgnoreCase(dependency.getEcosystem())) {
      dependency = Spring4ShellParser.getPackageNameandVersion(dependency);
    }
    if (dependency.getName() == null || dependency.getVersion() == null || dependency.getEcosystem() == null) {
      return null;
    }

    try {
      return new Package(
          dependency.getFileName(),
          PackageType.fromString(dependency.getEcosystem()),
          null,
          dependency.getName(),
          fixVersion(dependency.getVersion()),
          dependency.getDescription(),
          0L,
          null,
          null,
          dependency.getLicense());
    } catch (PackageValidationException pve) {
      LOGGER.warn(pve.getMessage());
      return null;
    }
  }

  // Fixing versions like \"0.0.1-security\" to 0.0.1-security
  private String fixVersion(String version) {
    if (version == null) {
      return null;
    }
    while (version.startsWith("\\") || version.startsWith("\"")) {
      version = version.substring(1);
    }
    while (version.endsWith("\\") || version.endsWith("\"")) {
      version = version.substring(0, version.length() - 1);
    }
    return version;
  }
}
