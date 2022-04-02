/***************************************************************************
 * COPYRIGHT (C) 2022, Rapid7 LLC, Boston, MA, USA.
 * All rights reserved. This material contains unpublished, copyrighted
 * work including confidential and proprietary information of Rapid7.
 **************************************************************************/
/**
 *
 */
package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.MavenArtifactInfo;
import java.util.HashMap;
import java.util.Map;
import org.owasp.dependencycheck.dependency.Dependency;
import static java.lang.String.format;

/*
 * Checks the sha1 checksum of a package to identify vulnerable versions of spring-beans
 *
 * More information: https://security.snyk.io/vuln/SNYK-JAVA-ORGSPRINGFRAMEWORK-2436751
 */
public class Spring4ShellParser {
  private static Map<String, MavenArtifactInfo> SPRING_BEANS_VERSION_MAP;
  private static final String PACKAGE_NAME_FORMAT = "%s:%s";

  static {
    SPRING_BEANS_VERSION_MAP = new HashMap<>();
    SPRING_BEANS_VERSION_MAP.put("8de4fe847e95a87e25acfb7fe5f21c0eddbe94c7", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.0.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("a88e2ccfe8b131bcff2e643b90d52f6d928e7369", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.15"));
  }

  public static Dependency getPackageNameandVersion(Dependency dependency) {
    String sha1Sum = dependency.getSha1sum();
    if (sha1Sum == null || sha1Sum.isEmpty()) {
      return dependency;
    }
    if (SPRING_BEANS_VERSION_MAP.containsKey(sha1Sum)) {
      MavenArtifactInfo info = SPRING_BEANS_VERSION_MAP.get(sha1Sum);
      dependency.setName(format(PACKAGE_NAME_FORMAT, info.getGroupId(), info.getArtifactId()));
      dependency.setVersion(info.getVersion());
    }
    return dependency;
  }
}
