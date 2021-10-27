package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import static java.util.Collections.emptySet;

public class DotNetParser implements PackageParser<File>{

  private static final Pattern DOT_NET_PATTERN = Pattern.compile(".*(?i)(\\.nuspec)$");


  @Override
  public boolean supports(String name, TarArchiveEntry entry) {
    return !entry.isSymbolicLink() && DOT_NET_PATTERN.matcher(name).matches();
  }

  @Override
  public Set<Package> parse(File input, OperatingSystem operatingSystem) throws IOException {
    String source = null;
    PackageType type = PackageType.DOTNET;
    String name = null;
    String version = null;
    String description = null;
    Long size = null;
    String maintainer = null;
    String homepage = null;
    String license = null;
    String epoch = null;
    String release = null;

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    Package pkg = new Package(source, type, operatingSystem, name, version, description, size, maintainer, homepage, license, epoch, release);


    return emptySet();
  }
}
