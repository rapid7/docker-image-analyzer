
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.image;

import java.util.Arrays;
import java.util.Map;
import static java.util.stream.Collectors.toMap;

public enum PackageType {

  APKG("APKG"),
  DPKG("DPKG"),
  PACMAN("PACMAN"),
  RPM("RPM"),
  RUBY("RUBY"),
  DOTNET("DOTNET"),
  IOS("IOS"),
  PHP("PHP"),
  GOLANG("GOLANG"),
  JAVA("JAVA"),
  NATIVE("NATIVE"),
  PYTHON("PYTHON"),
  JAVASCRIPT("JAVASCRIPT"),
  NODEJS("NODEJS"),
  RUST("RUST"),
  COLDFUSION("COLDFUSION"),
  PERL("PERL"),
  ELIXIR("EXLIXIR"),
  UNKNOWN("UNKNOWN");

  private final String name;

  PackageType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  private static final Map<String, PackageType> packageTypeByName = Arrays.stream(values())
      .collect(toMap(type -> type.getName().toLowerCase(), type -> type));

  public static PackageType fromString(String string) {
    return packageTypeByName.getOrDefault(string.toLowerCase(), UNKNOWN);
  }

  @Override
  public String toString() {
    return getName();
  }
}
