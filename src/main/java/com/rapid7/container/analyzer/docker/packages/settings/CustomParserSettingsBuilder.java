package com.rapid7.container.analyzer.docker.packages.settings;

import com.google.common.collect.ImmutableMap;
import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.fingerprinter.DotNetFingerprinter;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.container.analyzer.docker.packages.DotNetParser;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomParserSettingsBuilder {

  // Mappings for customer parsers
  private static final ImmutableMap<PackageType, LayerFileHandler> FINGERPRINTER_MAPPINGS = ImmutableMap.of(
      PackageType.DOTNET, new DotNetFingerprinter(new DotNetParser())
  );

  public static final CustomParserSettingsBuilder ALL = CustomParserSettingsBuilder.builder()
      .addFingerprinters(FINGERPRINTER_MAPPINGS.keySet());

  private final Set<LayerFileHandler> enabledFingerprinters = new HashSet<>();

  private CustomParserSettingsBuilder() {
  }

  public static CustomParserSettingsBuilder builder() {
    return new CustomParserSettingsBuilder();
  }

  public CustomParserSettingsBuilder addFingerprinter(PackageType packageType) {
    LayerFileHandler handler = FINGERPRINTER_MAPPINGS.get(packageType);
    if (handler != null) {
      enabledFingerprinters.add(handler);
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
    return enabledFingerprinters;
  }
}
