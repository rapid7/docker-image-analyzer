package com.rapid7.container.analyzer.docker.model.image;

import com.rapid7.container.analyzer.docker.model.Digest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

public class Image {

  protected ImageId id;
  protected Set<Digest> digests;
  protected ImageType type;
  protected Instant created;
  protected Long size;
  protected LinkedHashMap<LayerId, Layer> layers;

  private Image() {
    digests = new HashSet<>();
    layers = new LinkedHashMap<>();
  }

  public Image(ImageId id, ImageType type) {
    this();
    this.id = requireNonNull(id);
    this.type = requireNonNull(type);
  }

  public Image(ImageId id, ImageType type, Digest digest) {
    this(id, type);
    digests.add(digest);
  }

  public Image(ImageId id, ImageType type, Long size, Instant created) {
    this(id, type);
    this.created = created;
    this.size = size;
  }

  public Image(ImageId id, ImageType type, Digest digest, Long size, Instant created) {
    this(id, type, size, created);
    addDigest(digest);
  }

  public Image(ImageId id, ImageType type, Iterable<? extends Digest> digests, Long size, Instant created) {
    this(id, type, size, created);
    addDigests(digests);
  }

  public Image addLayer(Layer layer) {

    if (layer != null)
      layers.put(layer.getId(), layer);

    return this;
  }

  public Image addLayers(Iterable<? extends Layer> layers) {
    if (layers != null)
      for (Layer layer : layers)
        addLayer(layer);

    return this;
  }

  public Layer getLayer(LayerId id) {
    return layers.get(id);
  }

  public Layer getPackageLayer() {
    return layers.values().stream().filter(l -> !l.getPackages().isEmpty()).reduce((layer1, layer2) -> layer2).orElse(null);
  }

  public ImageId getId() {
    return id;
  }

  public ImageType getType() {
    return type;
  }

  public Image addDigest(Digest digest) {
    if (digest != null)
      digests.add(digest);

    return this;
  }

  public Image addDigests(Iterable<? extends Digest> digests) {
    if (digests != null)
      for (Digest digest : digests)
        addDigest(digest);

    return this;
  }

  public Set<Digest> getDigests() {
    return unmodifiableSet(digests);
  }

  public Image setCreated(Instant created) {
    this.created = created;
    return this;
  }

  public Instant getCreated() {
    return created;
  }

  public Long getSize() {
    return size;
  }

  public Image setSize(Long size) {
    this.size = size;
    return this;
  }

  public List<LayerFile> getFiles() {
    Layer layer = getPackageLayer();
    return layer == null ? emptyList() : layer.getFiles();
  }

  public List<Layer> getLayers() {
    return unmodifiableList(new ArrayList<>(layers.values()));
  }

  public Set<Package> getPackages() {
    Layer layer = getPackageLayer();
    return layer == null ? emptySet() : layer.getPackages();
  }

  public OperatingSystem getOperatingSystem() {
    return layers.values().stream().map(Layer::getOperatingSystem).filter(Objects::nonNull).findFirst().orElse(null);
  }

  public boolean isAnalyzed() {
    return !layers.isEmpty();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    else if (obj instanceof Image) {
      Image other = (Image)obj;
      return Objects.equals(id, other.id);
    } else
      return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Image.class.getSimpleName() + "[", "]")
        .add("Id=" + id)
        .add("Digests=" + digests)
        .add("Type=" + type)
        .add("Created=" + created)
        .add("Size=" + size)
        .add("Layers=" + layers)
        .toString();
  }
}
