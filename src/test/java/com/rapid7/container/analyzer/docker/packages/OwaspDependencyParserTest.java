package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.packages.settings.OwaspDependencyParserSettingsBuilder;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OwaspDependencyParserTest {

  private OwaspDependencyParser owaspDependencyParser;

  @Mock
  private TarArchiveEntry tarArchiveEntry;

  @BeforeEach
  public void setup() {
    owaspDependencyParser = new OwaspDependencyParser(OwaspDependencyParserSettingsBuilder.EXPERIMENTAL);
    when(tarArchiveEntry.isSymbolicLink()).thenReturn(false);
  }

  @Test
  public void doesNotSupportSymbolicEntries() {
    when(tarArchiveEntry.isSymbolicLink()).thenReturn(true);
    String fileName = "app.jar";
    assertFalse(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.jar", "app.jar", "directory0/directory1/app.jar"})
  public void supportsJarFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.war", "app.war", "directory0/directory1/app.war"})
  public void supportsWarFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.aar", "app.aar", "directory0/directory1/app.aar"})
  public void supportsAarFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.gemspec", "app.gemspec", "directory0/directory1/app.gemspec"})
  public void supportsGemspecFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.py", "app.py", "directory0/directory1/app.py"})
  public void supportsPyFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.egg", "app.egg", "directory0/directory1/app.egg"})
  public void supportsEggFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "directory/app.zip",
      "app.zip",
      "directory0/directory1/app.zip",
      "app.ear",
      "app.sar",
      "app.apk",
      "app.nupkg",
      "app.tar",
      "app.tar.gz",
      "app.tgz",
      "app.bz2",
      "app.tbz2"
  })
  public void supportsCompressedFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.whl", "app.whl", "directory0/directory1/app.whl"})
  public void supportsWhlFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));

  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.nuspec", "app.nuspec", "directory0/directory1/app.nuspec"})
  public void supportsNuspecFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/Gopkg.lock", "Gopkg.lock", "directory0/directory1/Gopkg.lock"})
  public void supportsGopkgLockFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/Gemfile.lock", "Gemfile.lock", "directory0/directory1/Gemfile.lock"})
  public void supportsGemfileLockFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/packages.config", "packages.config", "directory0/directory1/packages.config"})
  public void supportsPackagesConfigFiles(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/app.WAR", "APP.WAR", "DIRECTORY0/directory1/app.WAR"})
  public void isCaseInsensitiveForExtensions(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"directory/GEMFILE.lock", "GEMFILE.LOCK", "DIRECTORY0/directory1/gemfile.LOCK"})
  public void isCaseInsensitiveForFileNames(String fileName) {
    assertTrue(owaspDependencyParser.supports(fileName, tarArchiveEntry));
  }
}
