package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import java.io.IOException;
import java.util.Set;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public interface PackageParser<T> {
  boolean supports(String name, TarArchiveEntry entry);
  Set<Package> parse(T input, OperatingSystem operatingSystem) throws IOException;
}
