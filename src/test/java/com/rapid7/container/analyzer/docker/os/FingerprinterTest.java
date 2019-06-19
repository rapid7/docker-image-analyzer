package com.rapid7.container.analyzer.docker.os;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FingerprinterTest {

  @Test
  void parseAlpine() throws IOException {
    // Given
    Fingerprinter fp = new Fingerprinter();

    // When
    OperatingSystem os = fp.parse(FingerprinterTest.class.getResourceAsStream("alpine.txt"), "/etc/os-release", "x86_64");

    // Then
    assertEquals("Alpine", os.getVendor());
    assertEquals("3.8.0", os.getVersion());
    assertEquals("Alpine Linux 3.8.0", os.getDescription());
  }

  @Test
  void parseAlpineRelease() throws IOException {
    // Given
    Fingerprinter fp = new Fingerprinter();

    // When
    OperatingSystem os = fp.parse(FingerprinterTest.class.getResourceAsStream("alpine-release.txt"), "/etc/alpine-release", "x86_64");

    // Then
    assertEquals("Alpine", os.getVendor());
    assertEquals("3.8.0", os.getVersion());
    assertEquals("Alpine Linux 3.8.0", os.getDescription());
  }

  @Test
  void parseDebian() throws IOException {
    // Given
    Fingerprinter fp = new Fingerprinter();

    // When
    OperatingSystem os = fp.parse(FingerprinterTest.class.getResourceAsStream("debian.txt"), "/etc/os-release", "x86_64");

    // Then
    assertEquals("Debian", os.getVendor());
    assertEquals("8", os.getVersion());
    assertEquals("Debian Linux 8", os.getDescription());
  }

  @Test
  void parseOracle() throws IOException {
    // Given
    Fingerprinter fp = new Fingerprinter();

    // When
    OperatingSystem os = fp.parse(FingerprinterTest.class.getResourceAsStream("oracle.txt"), "/etc/os-release", "x86_64");

    // Then
    assertEquals("Oracle", os.getVendor());
    assertEquals("7.6", os.getVersion());
    assertEquals("Oracle Linux 7.6", os.getDescription());
  }

  @Test
  void parsePhoton() throws IOException {
    // Given
    Fingerprinter fp = new Fingerprinter();

    // When
    OperatingSystem os = fp.parse(FingerprinterTest.class.getResourceAsStream("photon.txt"), "/etc/os-release", "x86_64");

    // Then
    assertEquals("VMWare", os.getVendor());
    assertEquals("3.0", os.getVersion());
    assertEquals("VMWare Photon Linux 3.0", os.getDescription());
  }

  @Test
  void parsePhotonRelease() throws IOException {
    // Given
    Fingerprinter fp = new Fingerprinter();

    // When
    OperatingSystem os = fp.parse(FingerprinterTest.class.getResourceAsStream("photon-release.txt"), "/etc/photon-release", "x86_64");

    // Then
    assertEquals("VMWare", os.getVendor());
    assertEquals("3.0", os.getVersion());
    assertEquals("VMWare Photon Linux 3.0", os.getDescription());
  }

  @Test
  void parseRedhatRelease() throws IOException {
    // Given
    Fingerprinter fp = new Fingerprinter();

    // When
    OperatingSystem os = fp.parse(FingerprinterTest.class.getResourceAsStream("redhat-release.txt"), "/etc/redhat-release", "x86_64");

    // Then
    assertEquals("Red Hat", os.getVendor());
    assertEquals("7.6", os.getVersion());
    assertEquals("Red Hat Linux 7.6", os.getDescription());
  }

  @Test
  void parseUbuntu() throws IOException {
    // Given
    Fingerprinter fp = new Fingerprinter();

    // When
    OperatingSystem os = fp.parse(FingerprinterTest.class.getResourceAsStream("ubuntu.txt"), "/etc/os-release", "x86_64");

    // Then
    assertEquals("Ubuntu", os.getVendor());
    assertEquals("16.04", os.getVersion());
    assertEquals("Ubuntu Linux 16.04", os.getDescription());
  }
}
