package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.container.analyzer.docker.model.image.PackageValidationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public class M2RepositoryParser implements PackageParser<File> {

  private static final String M2_REPOSITORY_SUBSTRING = ".m2/repository/";
  private static final Pattern M2_REPOSITORY_PATTERN = Pattern.compile(".*(?i)(\\.(m2/repository)).*(.jar|.pom)$");
  private static final String GROUP_ID_DELIMITER = ".";
  private static final String PACKAGE_NAME_FORMAT = "%s:%s";
  private static final Logger LOGGER = LoggerFactory.getLogger(M2RepositoryParser.class);

  @Override
  public boolean supports(String name, TarArchiveEntry entry) {
    return !entry.isSymbolicLink() && M2_REPOSITORY_PATTERN.matcher(name).matches();
  }

  @Override
  public Set<Package> parse(File input, OperatingSystem operatingSystem) throws IOException {
    String relativePath = input.getPath().toLowerCase().substring(input.getPath().toLowerCase().indexOf(M2_REPOSITORY_SUBSTRING) + M2_REPOSITORY_SUBSTRING.length());
    String[] pathParts = relativePath.split("/");
    if (pathParts.length < 4) {
      return emptySet();
    }
    String fileName = pathParts[pathParts.length - 1];
    String versionNumber = pathParts[pathParts.length - 2];
    String artifactId = pathParts[pathParts.length - 3];
    String groupId = extractGroupId(pathParts);

    try {
      return singleton(new Package(fileName, PackageType.JAVA, null, format(PACKAGE_NAME_FORMAT, groupId, artifactId), versionNumber, null, 0L, null,
          null, null));
    } catch (PackageValidationException pve) {
      LOGGER.info(pve.getMessage());
      return null;
    }
  }

  private String extractGroupId(String[] pathParts) {
    ArrayDeque<String> groupIdParts = new ArrayDeque<>();
    for (int i = pathParts.length - 4; i >= 0; --i) {
      groupIdParts.push(pathParts[i]);
    }
    return join(GROUP_ID_DELIMITER, groupIdParts);
  }
}
