package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.packages.RpmPackageParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.slf4j.helpers.MessageFormatter.format;

public class RpmFingerprinter implements LayerFileHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RpmFingerprinter.class);
  private static final String RPM_QUERY_FORMAT = "Package:%{NAME}\nSource:%{SOURCEPACKAGE}\nDescription:%{SUMMARY}\nInstalled-Size:%{SIZE}\nLicense:%{LICENSE}\nHomepage:%{URL}\nMaintainer:%{VENDOR}\nVersion:%{VERSION}\nEpoch:%{EPOCH}\nRelease:%{RELEASE}\n\n";
  private static final String DEFAULT_DOCKER_IMAGE = "centos";
  private RpmPackageParser rpmPackageParser;
  private String rpmDockerImage;

  public RpmFingerprinter(RpmPackageParser rpmPackageParser, String rpmDockerImage) {
    this.rpmPackageParser = rpmPackageParser;
    this.rpmDockerImage = rpmDockerImage;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, Layer layer) throws IOException {
    if (!entry.isSymbolicLink() && name.endsWith("lib/rpm/Packages")) {
      File directory = Files.createTempDirectory("rpm").toFile();
      try {
        File packages = new File(directory, "Packages");

        LOGGER.info("Extracting RPM Packages database to {}.", packages.getAbsolutePath());

        // extract the Packages database
        try (FileOutputStream output = new FileOutputStream(packages)) {
          IOUtils.copy(contents, output);
        }

        LOGGER.info("Executing RPM command against directory '{}'.", directory.getAbsolutePath());

        if (commandExists("rpm")) {
          // only exec one RPM command at a time on the host (to prevent timeouts and other concurrency issues on host)
          synchronized (this) {
            // the --root flag sets the path which all rpm commands use as base for relative paths.
            // the --dbpath flag uses a path relative to the root path. we use "." (current directory) since the root path is the full rpmdb path.
            Process process = new ProcessBuilder("/usr/bin/env", "rpm", "--root=" + directory.getAbsolutePath(),"--dbpath=.", "-qa", "--queryformat=" + RPM_QUERY_FORMAT).start();
            LOGGER.info(format("[Image: {}] Parsing RPM output.", image.getId()).getMessage());
            layer.addPackages(rpmPackageParser.parse(process.getInputStream(), image.getOperatingSystem() == null ? layer.getOperatingSystem() : image.getOperatingSystem()));
            LOGGER.info(layer.getPackages().toString());
            if (!process.waitFor(5, TimeUnit.SECONDS))
              process.destroyForcibly();
          }
        } else if (commandExists("docker")) {
          // use a docker image to execute rpm command
          String rpmImage = (rpmDockerImage == null || rpmDockerImage.isEmpty()) ? DEFAULT_DOCKER_IMAGE : rpmDockerImage;
          LOGGER.info("Using docker image '{}' to execute RPM command.", rpmImage);
          Process process = new ProcessBuilder("/usr/bin/env", "docker", "run", "--rm","--mount", "type=bind,source=" + directory.getAbsolutePath() + ",target=/rpm",
              rpmImage, "rpm", "--root=/rpm", "--dbpath=.", "-qa", "--queryformat=" + RPM_QUERY_FORMAT).start();
          process.waitFor(300, TimeUnit.SECONDS);
          if (process.exitValue() != 0) {
            LOGGER.error("Docker exection failed with exit code {}.", process.exitValue());
            LOGGER.info("stdout: {}", IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8));
            LOGGER.info("stderr: {}", IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8));
          } else {
            LOGGER.info(format("[Image: {}] Parsing RPM output.", image.getId()).getMessage());
            layer.addPackages(rpmPackageParser.parse(process.getInputStream(), image.getOperatingSystem() == null ? layer.getOperatingSystem() : image.getOperatingSystem()));
            if (!process.waitFor(300, TimeUnit.SECONDS)) // TODO: user configurable?
              process.destroyForcibly();
            LOGGER.info("Parsed {} packages from layer.", layer.getPackages().size());
          }
        } else {
          LOGGER.warn("No suitable commands available to fingerprint RPM packages.");
        }
      } catch (Exception exception) {
        LOGGER.warn("Failed to fingerprint RPM packages.", exception);
      } finally {
        FileUtils.deleteDirectory(directory);
      }
    }
  }

  private boolean commandExists(String command) {
    String result = null;
    try {
      Process process = new ProcessBuilder("/usr/bin/env", "command", "-v", command).start();
      process.waitFor(5, TimeUnit.SECONDS);
      if (process.exitValue() == 0)
        result = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException | InterruptedException exception) {
      LOGGER.warn("Failed to find '{}' with 'command -v'.", command, exception);
    }

    try {
      if (result == null || result.isEmpty()) {
        Process process = new ProcessBuilder("/usr/bin/env", "which", command).start();
        process.waitFor(5, TimeUnit.SECONDS);
        if (process.exitValue() == 0)
          result = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
      }
    } catch (IOException | InterruptedException exception) {
      LOGGER.warn("Failed to find '{}' with 'which'.", command, exception);
    }

    return result != null && !result.isEmpty();
  }
}
