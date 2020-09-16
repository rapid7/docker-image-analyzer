package com.rapid7.container.analyzer.docker.service;

import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
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
    File tarFile = new File(getClass().getClassLoader().getResource("containers/test_image_centos.tar").getFile());
    ImageId expectedId = new ImageId("sha256:a0aa0b950b4149885be7ea08547097291f822a7e5b47f6f16ad81fa92b0a24d0");
    long expectedSize = 22976000;
    // omit OS check because this image tar was built from scratch
    long expectedLayers = 3;
    long expectedPackages = 129;

    // When
    DockerImageAnalyzerService analyzer = new DockerImageAnalyzerService(null);
    Path tmpdir = Files.createTempDirectory("r7dia");
    Image image = analyzer.analyze(tarFile, tmpdir.toString());

    // Then
    assertEquals(expectedId, image.getId());
    assertEquals(expectedSize, image.getSize());
    assertEquals(expectedLayers, image.getLayers().size());
    assertEquals(expectedPackages, image.getPackages().size());
  }
}
