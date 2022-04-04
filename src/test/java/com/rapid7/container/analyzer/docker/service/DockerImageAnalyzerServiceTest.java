package com.rapid7.container.analyzer.docker.service;

import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.container.analyzer.docker.packages.settings.CustomParserSettingsBuilder;
import com.rapid7.container.analyzer.docker.packages.settings.OwaspDependencyParserSettingsBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DockerImageAnalyzerServiceTest {

  @Test
  public void testAlpine() throws IOException {
    // Given
    File tarFile = new File(getClass().getClassLoader().getResource("containers/fakealpine.tar").getFile());
    ImageId expectedId = new ImageId("sha256:7be494284b1dea6cb2012a5ef99676b4ec22868d9ee235c60e48181542d70fd5");
    OperatingSystem expectedOs = new OperatingSystem("Alpine", "Linux", "Linux", "x86_64", "3.8.0", "Alpine Linux 3.8.0");
    long expectedSize = 119296;
    long expectedLayers = 2;
    long expectedPackages = 66;

    // When
    DockerImageAnalyzerService analyzer = new DockerImageAnalyzerService(null);
    Path tmpdir = Files.createTempDirectory("r7dia");
    Image image = analyzer.analyze(tarFile, tmpdir.toString());

    // Then
    assertEquals(expectedId, image.getId());
    assertEquals(expectedOs, image.getOperatingSystem());
    assertEquals(expectedSize, image.getSize());
    assertEquals(expectedLayers, image.getLayers().size());
    assertEquals(expectedPackages, image.getPackages().size());
  }

  @Test
  public void testCentos() throws IOException {
    // Given
    File tarFile = new File(getClass().getClassLoader().getResource("containers/centos/test_image_centos.tar").getFile());
    ImageId expectedId = new ImageId("sha256:bcbdfe9aa33a028bedc671ecfe56ce628ff64a8ad1b2fa46855c03955fcdc5bf");
    long expectedSize = 72704;
    // omit OS check because this image tar was built from scratch
    long expectedLayers = 3;
    long expectedPackages = 1;

    // When
    DockerImageAnalyzerService analyzer = new DockerImageAnalyzerService(null);
    Path tmpdir = Files.createTempDirectory("r7dia");
    Image image = analyzer.analyze(tarFile, tmpdir.toString());

    // Then
    assertEquals(expectedId, image.getId());
    assertEquals(expectedSize, image.getSize());
    assertEquals(expectedLayers, image.getLayers().size());
    assertEquals(expectedPackages, image.getPackages().size());
    assertEquals("A set of system configuration and setup files", image.getPackages().stream().findFirst().get().getDescription());
  }


  @Test
  public void parseDotnet() throws FileNotFoundException, IOException {
    // given
    File tarFile = new File(getClass().getClassLoader().getResource("containers/dotnet-packages.tar").getFile());

    // when
    DockerImageAnalyzerService analyzer = new DockerImageAnalyzerService(null, OwaspDependencyParserSettingsBuilder.builder(),
        CustomParserSettingsBuilder.builder().addFingerprinter(PackageType.DOTNET));
    Path tmpdir = Files.createTempDirectory("r7dia");
    Image image = analyzer.analyze(tarFile, tmpdir.toString());

    // then
    List<Layer> layersWithDotnetPackages = image.getLayers().stream()
        .filter(layer -> layer.getPackages().stream().anyMatch(pkg -> pkg.getType() == PackageType.DOTNET))
        .collect(Collectors.toList());
    Set<Package> dotnetPackages = image.getPackages().stream()
        .filter(pkg -> pkg.getType() == PackageType.DOTNET)
        .collect(Collectors.toSet());

    assertThat(layersWithDotnetPackages.size(), is(1));
    assertThat(dotnetPackages.size(), is(5));
  }

  @Test
  public void parseSpring4Shell() throws FileNotFoundException, IOException {
    // given
    File tarFile = new File(getClass().getClassLoader().getResource("containers/jar-image.tar").getFile());

    // when
    DockerImageAnalyzerService analyzer = new DockerImageAnalyzerService(null);
    Path tmpdir = Files.createTempDirectory("r7dia");
    Image image = analyzer.analyze(tarFile, tmpdir.toString());

    // then
    Set<Package> packages = image.getPackages();
    Set<String> names = packages.stream().map(Package::getPackage).collect(toSet());
    Set<String> versions = packages.stream().map(Package::getVersion).collect(toSet());
    assertThat(names.size(), is(1));
    assertThat("org.springframework:spring-beans", isIn(names));
    assertThat(versions, containsInAnyOrder("5.0.8.RELEASE", "5.1.10.RELEASE", "5.2.12.RELEASE", "5.2.9.RELEASE", "5.2.18.RELEASE", "5.1.19.RELEASE", "5.3.16", "5.0.5.RELEASE",
        "5.3.7", "5.3.13", "5.2.3.RELEASE", "5.1.8.RELEASE", "5.1.18.RELEASE", "5.3.17", "5.3.14", "5.3.18", "5.3.6", "5.0.1.RELEASE", "5.1.9.RELEASE", "5.0.14.RELEASE", "5.3.15",
        "5.0.11.RELEASE", "5.1.3.RELEASE", "5.2.19.RELEASE"));

  }
}
