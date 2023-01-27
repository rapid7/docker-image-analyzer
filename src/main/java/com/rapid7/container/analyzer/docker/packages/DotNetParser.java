package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.container.analyzer.docker.model.image.PackageValidationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DotNetParser implements PackageParser<File> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DotNetParser.class);
  private static final Pattern DOT_NET_PATTERN = Pattern.compile(".*(?i)(\\.nuspec)$");

  @Override
  public boolean supports(String name, TarArchiveEntry entry) {
    return !entry.isSymbolicLink() && DOT_NET_PATTERN.matcher(name).matches();
  }

  @Override
  public Set<Package> parse(File input, OperatingSystem operatingSystem) throws IOException {

    Set<Package> packages = new HashSet<>();
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbFactory.newDocumentBuilder();
      Document document = db.parse(input);
      document.getDocumentElement().normalize();

      NodeList nodeList = document.getElementsByTagName("package");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);

        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;

          String source = input.getName();
          String name = getValueForAttribute(element, "id");
          String version = getValueForAttribute(element, "version");
          String description = getValueForAttribute(element, "description");
          try {
            packages.add(new Package(source, PackageType.DOTNET, operatingSystem, name, version, description, null, null, null, null));
          } catch (PackageValidationException pve) {
            LOGGER.warn(pve.getMessage());
          }
        }
      }
    } catch (ParserConfigurationException | SAXException exception) {
      LOGGER.error("Could not parse .nuspec file", exception);
    }

    return packages;
  }


  private String getValueForAttribute(Element element, String attribute) {
    NodeList nodeList = element.getElementsByTagName(attribute);
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    } else {
      return null;
    }
  }
}
