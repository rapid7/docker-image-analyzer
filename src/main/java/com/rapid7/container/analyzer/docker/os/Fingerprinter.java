
/**
 *
 */
package com.rapid7.container.analyzer.docker.os;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.recog.Recog;
import com.rapid7.recog.RecogMatchResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * OS release file parser that parses the contents of "os-release" to fingerprint the host system.
 */
public class Fingerprinter {

  private static final Pattern PATTERN = Pattern.compile("(?<name>.*)=(?<value>.*)");
  private static final Map<String, String> OS_ID_TO_VENDOR = Arrays.stream(new String[][]{
      {"alpine", "Alpine"},
      {"amzn", "Amazon"},
      {"arch", "Arch"},
      {"centos", "CentOS"},
      {"debian", "Debian"},
      {"fedora", "Fedora"},
      {"gentoo", "Gentoo"},
      {"ol", "Oracle"},
      {"opensuse", "OpenSUSE"},
      {"photon", "VMWare"},
      {"rhel", "Red Hat"},
      {"ubuntu", "Ubuntu"},
  }).collect(toMap(kv -> kv[0], kv -> kv[1]));

  private Recog recog;

  public Fingerprinter(Recog recogClient) {
    recog = requireNonNull(recogClient, "recog client");
  }

  /**
   * Parses the contents of an os-release file to ascertain the fingerprint of an operating system.
   *
   * @param input The os-release file to parse. Must not be {@code null}.
   * @return The fingerprint of the operating system, or {@code null} if one could not be detected.
   */
  public OperatingSystem parse(InputStream input, String fileName, String architecture) throws IOException {
    if (fileName.endsWith("/os-release") || fileName.endsWith("/lsb-release"))
      return parseOsRelease(input, architecture);
    else if (fileName.endsWith("/alpine-release"))
      return parseAlpineRelease(input, architecture);
    else if (fileName.endsWith("/photon-release"))
      return parsePhotonRelease(input, architecture);
    else // catch-all for rhel-like release files and anything else we missed
      return parseRhelFamilyRelease(input, architecture);
  }

  /**
   * Parse os-release and lsb-release formats.
   */
  private OperatingSystem parseOsRelease(InputStream input, String architecture) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String line = null;
      String id = null;
      String product = null;
      String description = null;
      String version = null;
      while ((line = reader.readLine()) != null) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
          String name = matcher.group("name");
          String value = matcher.group("value");
          switch (name) {
            case "ID":
              id = value.replaceAll("\"", "");
              break;
            case "NAME":
            case "DISTRIB_ID":
              product = value.replaceAll("\"", "");
              break;
            case "VERSION_ID":
            case "DISTRIB_RELEASE":
              version = value.replaceAll("(?:^v|\\\")", "");
              break;
            case "PRETTY_NAME":
            case "DISTRIB_DESCRIPTION":
              description = value.replaceAll("\"", "");
              break;
            default:
              break;
          }
        }
      }

      String vendor = OS_ID_TO_VENDOR.get(id);
      OperatingSystem operatingSystem = null;

      // attempt to run recog against the name and version, which should be accurate already
      if (product != null && !product.isEmpty() && version != null && !version.isEmpty())
        operatingSystem = fingerprintOperatingSystem(product + " " + version, version, vendor, architecture);

      // try with description if first try didn't match
      if (operatingSystem == null && description != null)
        operatingSystem = fingerprintOperatingSystem(description, version, vendor, architecture);

      // try with product name if the description was null or didn't match
      if (operatingSystem == null && product != null)
        operatingSystem = fingerprintOperatingSystem(product, version, vendor, architecture);

      // default to building a fingerprint with the distribution ID and release version
      if (operatingSystem == null && product != null && version != null)
        operatingSystem = new OperatingSystem(vendor == null ? trimProductName(product) : vendor, "Linux", "Linux", architecture, version, description);

      return operatingSystem;
    }
  }

  private OperatingSystem parseRhelFamilyRelease(InputStream input, String architecture) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String line = null;
      String description = "";
      while ((line = reader.readLine()) != null) {
        description += line;
      }

      return fingerprintOperatingSystem(description, null, null, architecture);
    }
  }

  private OperatingSystem parseAlpineRelease(InputStream input, String architecture) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String line = null;
      String version = null;
      while ((line = reader.readLine()) != null) {
        if (version == null && line.matches("(?:\\d+(?:\\.)?)+"))
          version = line;
      }

      return fingerprintOperatingSystem("Alpine Linux", version, "Alpine", architecture);
    }
  }

  private OperatingSystem parsePhotonRelease(InputStream input, String architecture) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String line = null;
      String description = null;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("VMWare"))
          description = line;
      }

      return fingerprintOperatingSystem(description, null, "VMWare", architecture);
    }
  }

  private String trimProductName(String productName) {
    return productName.replaceAll("(?i:\\s+(?:Enterprise|(?:GNU/)?Linux).*)", "");
  }

  public Package fingerprintPackage(OperatingSystem operatingSystem, String pkg) {

    Pattern pattern = Pattern.compile("(?<name>.*) (?<version>.*).*");
    Matcher matcher = pattern.matcher(pkg);
    if (matcher.matches()) {
      String name = matcher.group("name");
      String version = matcher.group("version");
      return new Package("linux", PackageType.UNKNOWN, operatingSystem, name, version, pkg, 0L, null, null, null);
    } else
      return null;
  }

  public OperatingSystem fingerprintOperatingSystem(String productDescription, String productArchitecture) {
    return fingerprintOperatingSystem(productDescription, null, null, productArchitecture);
  }

  public OperatingSystem fingerprintOperatingSystem(String productDescription, String productVersion, String productVendor, String productArchitecture) {
    OperatingSystem operatingSystem = null;
    if (productDescription == null || productDescription.isEmpty())
      return null;

    List<RecogMatchResult> matchResults = recog.fingerprint(productDescription);
    if (!matchResults.isEmpty()) {
      RecogMatchResult matchResult = matchResults.get(0);
      Map<String, String> matches = matchResult.getMatches();
      String vendor = (matches.get("os.vendor") == null ? "" : matches.get("os.vendor"));
      if (productVendor != null && !productVendor.isEmpty() && !vendor.toLowerCase().contains(productVendor.toLowerCase()))
        return operatingSystem; // probably a bad match from recog

      String description = Stream.of(matches.get("os.vendor"), matches.get("os.product"), matches.get("os.version") != null ? matches.get("os.version") : productVersion).filter(Objects::nonNull).collect(joining(" "));
      String architecture = matches.get("os.arch") == null ? productArchitecture : matches.get("os.arch");
      if (matches.get("os.product") != null)
        operatingSystem = new OperatingSystem(matches.get("os.vendor"), matches.get("os.family"), matches.get("os.product"), architecture, matches.get("os.version") != null ? matches.get("os.version") : productVersion, description);
    }

    return operatingSystem;
  }
}
