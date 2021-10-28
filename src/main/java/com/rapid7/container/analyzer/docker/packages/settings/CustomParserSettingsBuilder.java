package com.rapid7.container.analyzer.docker.packages.settings;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.fingerprinter.DotNetFingerprinter;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.container.analyzer.docker.packages.DotNetParser;
import com.rapid7.container.analyzer.docker.util.Pair;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

public class CustomParserSettingsBuilder {


  private static final ImmutableList<Pair<PackageType, LayerFileHandler>> FINGERPRINTER_MAPPINGS = ImmutableList.of(
      new Pair<>(PackageType.DOTNET, new DotNetFingerprinter(new DotNetParser()))
  );

  public static final CustomParserSettingsBuilder ALL = CustomParserSettingsBuilder.builder()
      .addFingerprinters(FINGERPRINTER_MAPPINGS.stream().map(pair -> pair.getFirst()).collect(Collectors.toSet()));

  private final Set<LayerFileHandler> enabledFingerprinter = new HashSet<>();


  private CustomParserSettingsBuilder() {
  }

  public static CustomParserSettingsBuilder builder() {
    return new CustomParserSettingsBuilder();
  }

  public CustomParserSettingsBuilder addFingerprinter(PackageType packageType) {
    for (Pair<PackageType, LayerFileHandler> pair : FINGERPRINTER_MAPPINGS) {
      if (pair.getFirst() == packageType) {
        enabledFingerprinter.add(pair.getSecond());
      }
    }
    return this;
  }

  public CustomParserSettingsBuilder addFingerprinters(Collection<PackageType> packageTypes) {
    for (PackageType packageType : packageTypes) {
      addFingerprinter(packageType);
    }
    return this;
  }

  public Set<LayerFileHandler> getFingerprinters() {
    return enabledFingerprinter;
  }
}
