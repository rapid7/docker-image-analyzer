package com.rapid7.container.analyzer.docker.model.image.json.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.image.LayerFile;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageId;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public abstract class LayerMixin extends Layer {

  @JsonCreator
  public LayerMixin(@JsonProperty("id") LayerId id) {
    super(id);
  }

  @Override
  @JsonProperty("id")
  public abstract LayerId getId();

  @Override
  @JsonProperty("command")
  public abstract String getCommand();

  @Override
  @JsonProperty("command")
  public abstract Layer setCommand(String command);

  @Override
  @JsonProperty("comment")
  public abstract String getComment();

  @Override
  @JsonProperty("comment")
  public abstract Layer setComment(String comment);

  @Override
  @JsonProperty("author")
  public abstract String getAuthor();

  @Override
  @JsonProperty("author")
  public abstract Layer setAuthor(String author);

  @Override
  @JsonProperty("os")
  public abstract OperatingSystem getOperatingSystem();

  @Override
  @JsonProperty("os")
  public abstract Layer setOperatingSystem(OperatingSystem operatingSystem);

  @Override
  @JsonProperty("packages")
  public abstract Set<Package> getPackages();

  @Override
  @JsonProperty("packages")
  public abstract Layer addPackages(Iterable<? extends Package> packages);

  @Override
  @JsonProperty("parentId")
  public abstract LayerId getParentId();

  @Override
  @JsonProperty("parentId")
  public abstract Layer setParentId(LayerId parentId);

  @Override
  @JsonProperty("created")
  public abstract Instant getCreated();

  @Override
  @JsonProperty("created")
  public abstract Layer setCreated(Instant created);

  @Override
  @JsonProperty("size")
  public abstract long getSize();

  @Override
  @JsonProperty("size")
  public abstract Layer setSize(long size);

  @Override
  @JsonProperty("empty")
  public abstract boolean isEmpty();

  @Override
  @JsonProperty("empty")
  public abstract Layer setEmpty(boolean empty);

  // ignored

  @Override
  @JsonIgnore
  public abstract Layer addPackage(Package pkg);

  @Override
  @JsonIgnore
  public abstract Package getPackage(PackageId id);

  @Override
  @JsonIgnore
  public abstract Layer addFile(LayerFile pkg);

  @Override
  @JsonIgnore
  public abstract boolean isPackageLayer();

  @Override
  @JsonIgnore
  public abstract Layer addFiles(Iterable<? extends LayerFile> files);

  @Override
  @JsonIgnore
  public abstract List<LayerFile> getFiles();
}
