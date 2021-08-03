package com.rapid7.container.analyzer.docker.os;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toMap;

/**
 * OS release file parser that parses the contents of "os-release" to fingerprint the host system.
 */
public class Fingerprinter {

  private static final Pattern PATTERN = Pattern.compile("(?<name>.*)=(?<value>.*)");
  private static final Pattern PHOTON_RELEASE = Pattern.compile("^(?i:VMWare Photon(?:\\s?OS)?(?:/)?(?:\\s?Linux)?\\s?(?:v)?(\\d+?(?:\\.\\d+?)*?)?)$");
  private static final Pattern CENTOS_RELEASE = Pattern.compile("^CentOS release (\\d*\\.*\\d+)?.*");
  private static final Pattern RHEL_RELEASE = Pattern.compile("^(?i:(?:Red Hat|RedHat|Red-Hat|RHEL)(?: Enterprise)?(?: Linux)?(?: Server)?(?: release)?(?: [a-z]+)?\\s?(\\d+?(?:\\.\\d+?)*?)?)(?:\\s?\\(.*\\))?$");
  private static final String OS_FAMILY = "Linux";
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

  /**
   * Parses the contents of an os-release file to ascertain the fingerprint of an operating system.
   *
   * @param input The os-release file to parse. Must not be {@code null}.
   * @param fileName The name of the os-release file to parse. Must not be {@code null}.
   * @param architecture The architecture of the operating system
   * @return The fingerprint of the operating system, or {@code null} if one could not be detected.
   * @throws IOException if there is a problem reading file data
   */
  public OperatingSystem parse(InputStream input, String fileName, String architecture) throws IOException {
    if (fileName.endsWith("/os-release") || fileName.endsWith("/lsb-release"))
      return parseOsRelease(input, architecture);
    else if (fileName.endsWith("/alpine-release"))
      return parseAlpineRelease(input, architecture);
    else if (fileName.endsWith("/photon-release"))
      return parsePhotonRelease(input, architecture);
    else if (fileName.endsWith("/centos-release"))
      return parseCentosRelease(input, architecture);
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
      String product = OS_FAMILY;
      String description = null;
      String version = "";
      while ((line = reader.readLine()) != null) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
          String name = matcher.group("name");
          String value = matcher.group("value");
          switch (name) {
            case "ID":
            case "DISTRIB_ID":
              id = value.replaceAll("\"", "").toLowerCase();
              break;
            case "NAME":
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
      if (!"VMWare".equals(vendor))
        product = OS_FAMILY;

      return fingerprintOperatingSystem(vendor != null ? vendor : OS_FAMILY, product, version, architecture);
    }
  }

  private OperatingSystem parseRhelFamilyRelease(InputStream input, String architecture) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      Matcher matcher = RHEL_RELEASE.matcher("");
      String version = "";
      String line = null;
      while ((line = reader.readLine()) != null) {
        matcher.reset(line);
        if (matcher.matches())
          version = matcher.group(1);
      }

      return fingerprintOperatingSystem("Red Hat", OS_FAMILY, version, architecture);
    }
  }

  private OperatingSystem parseAlpineRelease(InputStream input, String architecture) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String line = null;
      String version = "";
      while ((line = reader.readLine()) != null) {
        if (version.isEmpty() && line.matches("(?:\\d+(?:\\.)?)+"))
          version = line;
      }

      return fingerprintOperatingSystem("Alpine", OS_FAMILY, version, architecture);
    }
  }

  private OperatingSystem parsePhotonRelease(InputStream input, String architecture) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      Matcher matcher = PHOTON_RELEASE.matcher("");
      String product = "Photon Linux";
      String version = "";
      String line = null;
      while ((line = reader.readLine()) != null) {
        matcher.reset(line);
        if (matcher.matches())
          version = matcher.group(1);
      }

      return fingerprintOperatingSystem("VMWare", product, version, architecture);
    }
  }

  private OperatingSystem parseCentosRelease(InputStream input, String architecture) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String version = null;
      String line;
      while ((line = reader.readLine()) != null) {
        Matcher matcher = CENTOS_RELEASE.matcher(line);
        if (matcher.matches()) {
          version = matcher.group(1);
          break;
        }
      }

      return fingerprintOperatingSystem("CentOS", OS_FAMILY, version, architecture);
    }
  }

  private OperatingSystem fingerprintOperatingSystem(String vendor, String product, String version, String architecture) {
    if ("VMWare".equals(vendor))
      product = "Photon Linux";

    return new OperatingSystem(vendor, OS_FAMILY, product, architecture, version, vendor + " " + product + " " + version);
  }

}
