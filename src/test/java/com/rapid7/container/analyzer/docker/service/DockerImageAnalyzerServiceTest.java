package com.rapid7.container.analyzer.docker.service;

import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.json.Manifest;
import com.rapid7.container.analyzer.docker.packages.DotNetParser;
import com.rapid7.container.analyzer.docker.packages.settings.OwaspDependencyParserSettingsBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
  public void parse() throws FileNotFoundException, IOException {
    // given
    File tarFile = new File(getClass().getClassLoader().getResource("containers/multipackage.tar").getFile());

    // when
    DockerImageAnalyzerService analyzer = new DockerImageAnalyzerService(null, OwaspDependencyParserSettingsBuilder.builder());
    Path tmpdir = Files.createTempDirectory("r7dia");
    Image image = analyzer.analyze(tarFile, tmpdir.toString());

    // then

  }
}
