package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

public class DpkgParserTest {

  private static final OperatingSystem OS = new OperatingSystem("Debian", "Linux", "Linux", "x86_64", "9", "Debian Linux 9");
  private static final Package PACKAGE = new Package(null, PackageType.DPGK, OS, "dash", "0.5.8-2.4", "POSIX-compliant shell", 204000L, "Gerrit Pape <pape@smarden.org>", "http://gondor.apana.org.au/~herbert/dash/", null);

  @Test
  public void singleParse() throws FileNotFoundException, IOException {
    // given
    DpkgParser parser = new DpkgParser();

    // when
    Set<Package> packages = parser.parse(DpkgParserTest.class.getResourceAsStream("dpkg-dash.info"), OS);

    // then
    assertThat(packages, hasItem(samePropertyValuesAs(PACKAGE)));
  }

  @Test
  public void multipleParse() throws FileNotFoundException, IOException {
    // given
    DpkgParser parser = new DpkgParser();

    // when
    Set<Package> packages = parser.parse(DpkgParserTest.class.getResourceAsStream("dpkg-multiple.info"), OS);

    // then
    assertThat(packages, hasItem(samePropertyValuesAs(PACKAGE)));
  }

  @Test
  public void fullParse() throws FileNotFoundException, IOException {
    // given
    DpkgParser parser = new DpkgParser();

    // when
    Set<Package> packages = parser.parse(DpkgParserTest.class.getResourceAsStream("dpkg.info"), OS);

    // then
    assertThat(packages.size(), is(equalTo(83)));
    assertThat(packages, hasItem(samePropertyValuesAs(PACKAGE)));
  }
}
