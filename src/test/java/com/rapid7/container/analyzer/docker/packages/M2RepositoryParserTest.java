package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.packages.settings.OwaspDependencyParserSettingsBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class M2RepositoryParserTest {

  private M2RepositoryParser m2RepositoryParser;
  @Mock
  private TarArchiveEntry tarArchiveEntry;

  @BeforeEach
  public void setup() {
    m2RepositoryParser = new M2RepositoryParser();
  }

  @Test
  public void hasExpectedGroupIdWhenGroupIdIsOnePart() throws IOException {
    String expectedGroupId = "groupid";
    String path = "/.m2/repository/" + expectedGroupId + "/artifactId/1.0.0/artifactId-1.0.0.jar";
    File jarFile = Mockito.mock(File.class);
    when(jarFile.getPath()).thenReturn(path);

    Set<Package> packages = m2RepositoryParser.parse(jarFile, null);

    assertEquals(1, packages.size());
    assertEquals(expectedGroupId, packages.iterator().next().getPackage().split(":")[0]);
  }

  @Test
  public void hasExpectedGroupIdWhenGroupIdIsMultipleParts() throws IOException {
    String expectedGroupId = "a/delimited/groupid/for/a/package";
    String path = "prefix/prefix/prefix/.m2/repository/" + expectedGroupId + "/artifactId/1.0.0/artifactId-1.0.0.jar";
    File jarFile = Mockito.mock(File.class);
    when(jarFile.getPath()).thenReturn(path);

    Set<Package> packages = m2RepositoryParser.parse(jarFile, null);

    assertEquals(1, packages.size());
    assertEquals(expectedGroupId.replaceAll("/", "."), packages.iterator().next().getPackage().split(":")[0]);
  }

  @Test
  public void hasExpectedArtifactId() throws IOException {
    String expectedArtifactId = "artifactid";
    String path = "/.m2/repository/groupId/" + expectedArtifactId + "/1.0.0/artifactId-1.0.0.jar";
    File jarFile = Mockito.mock(File.class);
    when(jarFile.getPath()).thenReturn(path);

    Set<Package> packages = m2RepositoryParser.parse(jarFile, null);

    assertEquals(1, packages.size());
    assertEquals(expectedArtifactId, packages.iterator().next().getPackage().split(":")[1]);
  }

  @Test
  public void hasExpectedPackageName() throws IOException {
    String expectedGroupId = "groupid";
    String expectedArtifactId = "artifactid";
    String path = "/.m2/repository/" + expectedGroupId + "/" + expectedArtifactId + "/1.0.0/artifactId-1.0.0.jar";
    File jarFile = Mockito.mock(File.class);
    when(jarFile.getPath()).thenReturn(path);

    Set<Package> packages = m2RepositoryParser.parse(jarFile, null);

    assertEquals(1, packages.size());
    assertEquals(format("%s:%s", expectedGroupId, expectedArtifactId), packages.iterator().next().getPackage());
  }

  @Test
  public void hasExpectedVersion() throws IOException {
    String expectedVersion = "1.0.0";
    String path = "/.m2/repository/groupId/artifactId/" + expectedVersion + "/artifactId-1.0.0.jar";
    File jarFile = Mockito.mock(File.class);
    when(jarFile.getPath()).thenReturn(path);

    Set<Package> packages = m2RepositoryParser.parse(jarFile, null);

    assertEquals(1, packages.size());
    assertEquals(expectedVersion, packages.iterator().next().getVersion());
  }

  @Test
  public void hasExpectedSource() throws IOException {
    String expectedSource = "artifactid-1.0.0.jar";
    String path = "/.m2/repository/groupId/artifactId/1.0.0/" + expectedSource;
    File jarFile = Mockito.mock(File.class);
    when(jarFile.getPath()).thenReturn(path);

    Set<Package> packages = m2RepositoryParser.parse(jarFile, null);

    assertEquals(1, packages.size());
    assertEquals(expectedSource, packages.iterator().next().getSource());
  }

  @Test
  public void returnsEmptySetIfNotEnoughPartsInPath() throws IOException {
    String path = "/.m2/repository/groupId/artifactId/someJar-1.0.0.jar";
    File jarFile = Mockito.mock(File.class);
    when(jarFile.getPath()).thenReturn(path);

    Set<Package> packages = m2RepositoryParser.parse(jarFile, null);
    assertEquals(0, packages.size());
  }

  @Test
  public void doesNotSupportSymbolicEntries() {
    when(tarArchiveEntry.isSymbolicLink()).thenReturn(true);
    String fileName = "app.jar";
    assertFalse(m2RepositoryParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/.m2/repository/groupId/artifactId/versionNumber/app-versionNumber.jar", "var/file/bam/.m2/repository/groupId/groupId2/artifactId/versionNumber/app.jar"})
  public void supportsJarFiles(String fileName) {
    when(tarArchiveEntry.isSymbolicLink()).thenReturn(false);
    assertTrue(m2RepositoryParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/.m2/repository/groupId/artifactId/versionNumber/app-versionNumber.pom", "var/file/bam/.m2/repository/groupId/groupId2/artifactId/versionNumber/app.pom"})
  public void supportsPomFiles(String fileName) {
    when(tarArchiveEntry.isSymbolicLink()).thenReturn(false);
    assertTrue(m2RepositoryParser.supports(fileName, tarArchiveEntry));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/m2/repository/groupId/artifactId/versionNumber/app-versionNumber.pom", "var/file/bam/repository/groupId/groupId2/artifactId/versionNumber/app.pom", "groupId/groupId2/artifactId/versionNumber/app.pom"})
  public void doesNotSupportNonM2Repositories(String fileName) {
    when(tarArchiveEntry.isSymbolicLink()).thenReturn(false);
    assertFalse(m2RepositoryParser.supports(fileName, tarArchiveEntry));
  }


  @ParameterizedTest
  @ValueSource(strings = {"/.m2/repository/groupId/artifactId/versionNumber/app-versionNumber.JAR", "var/file/bam/.m2/repository/groupId/groupId2/artifactId/versionNumber/app.JAR"})
  public void isCaseInsensitiveForExtensions(String fileName) {
    when(tarArchiveEntry.isSymbolicLink()).thenReturn(false);
    assertTrue(m2RepositoryParser.supports(fileName, tarArchiveEntry));
  }
}
