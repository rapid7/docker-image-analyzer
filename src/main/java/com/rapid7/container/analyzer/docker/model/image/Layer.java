package com.rapid7.container.analyzer.docker.model.image;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

/**
 * A layer within an image representing some state change within the image, which can comprise of
 * file-system changes. Each layer tracks the commands that were used to construct it, and
 * fingerprints of packages/etc.
 */
public class Layer {

  private LayerId id;
  private Set<Package> packages;
  private String command;
  private String comment;
  private String author;
  private OperatingSystem operatingSystem;
  private LayerId parentId;
  private Instant created;
  private long size;
  private boolean empty;
  private List<LayerFile> files;

  protected Layer() {
    // deserialization
    packages = new HashSet<>();
    files = new ArrayList<>();
  }

  public Layer(LayerId id) {
    this.id = requireNonNull(id);
    packages = new HashSet<>();
    files = new ArrayList<>();
  }

  public LayerId getId() {
    return id;
  }

  public Layer setCommand(String command) {
    this.command = command;
    return this;
  }

  public String getCommand() {
    return command;
  }

  public Layer setComment(String comment) {
    this.comment = comment;
    return this;
  }

  public String getComment() {
    return comment;
  }

  public Layer setAuthor(String author) {
    this.author = author;
    return this;
  }

  public String getAuthor() {
    return author;
  }

  public OperatingSystem getOperatingSystem() {
    return operatingSystem;
  }

  public Layer setOperatingSystem(OperatingSystem operatingSystem) {
    this.operatingSystem = operatingSystem;
    return this;
  }

  public Layer addPackage(Package pkg) {
    packages.add(pkg);
    return this;
  }

  public Layer addPackages(Iterable<? extends Package> packages) {
    if (packages != null)
      packages.forEach(this::addPackage);

    return this;
  }

  public Set<Package> getPackages() {
    return unmodifiableSet(packages);
  }

  public Package getPackage(PackageId id) {
    for (Package pkg : packages)
      if (pkg instanceof IdentifiablePackage)
        if (((IdentifiablePackage) pkg).getId().equals(id))
          return pkg;

    return null;
  }

  public Layer addFile(LayerFile pkg) {
    files.add(pkg);
    return this;
  }

  public Layer addFiles(Iterable<? extends LayerFile> files) {
    if (files != null)
      files.forEach(this::addFile);

    return this;
  }

  public List<LayerFile> getFiles() {
    return unmodifiableList(files);
  }

  public LayerId getParentId() {
    return parentId;
  }

  public Layer setParentId(LayerId parentId) {
    this.parentId = parentId;
    return this;
  }

  public Instant getCreated() {
    return created;
  }

  public Layer setCreated(Instant created) {
    this.created = created;
    return this;
  }

  public long getSize() {
    return size;
  }

  public Layer setSize(long size) {
    this.size = size;
    return this;
  }

  public boolean isPackageLayer() {
    return !packages.isEmpty();
  }

  public boolean isEmpty() {
    return empty;
  }

  public Layer setEmpty(boolean empty) {
    this.empty = empty;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    else if (!(obj instanceof Layer))
      return false;
    else {
      Layer other = (Layer) obj;
      return Objects.equals(id, other.id);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Layer.class.getSimpleName() + "[", "]")
        .add("Id=" + id)
        .add("Parent Id=" + parentId)
        .add("Files=" + (files.size() > 3 ? files.size() : files))
        .add("Packages=" + (packages.size() > 3 ? packages.size() : packages))
        .add("Command=" + command)
        .add("Comment=" + comment)
        .add("Author=" + author)
        .add("Operating System=" + operatingSystem)
        .add("Created=" + created)
        .add("Size=" + size)
        .add("Empty=" + empty)
        .toString();
  }
}
