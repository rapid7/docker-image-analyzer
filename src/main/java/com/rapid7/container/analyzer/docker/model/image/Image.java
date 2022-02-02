package com.rapid7.container.analyzer.docker.model.image;

import com.rapid7.container.analyzer.docker.model.Digest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Image {

  private static final Set<String> OS_PACKAGES = Stream.of("APK", "APKG", "DPKG", "PACMAN", "RPM").collect(Collectors.toSet());
  protected ImageId id;
  protected Digest digest;
  protected ImageType type;
  protected Instant created;
  protected Long size;
  protected LinkedHashMap<LayerId, Layer> layers;

  private Image() {
    layers = new LinkedHashMap<>();
  }

  public Image(ImageId id, ImageType type) {
    this();
    this.id = requireNonNull(id);
    this.type = requireNonNull(type);
  }

  public Image(ImageId id, ImageType type, Digest digest) {
    this(id, type);
    this.digest = digest;
  }

  public Image(ImageId id, ImageType type, Long size, Instant created) {
    this(id, type);
    this.created = created;
    this.size = size;
  }

  public Image(ImageId id, ImageType type, Digest digest, Long size, Instant created) {
    this(id, type, size, created);
    this.digest = digest;
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

  public List<Layer> getPackageLayer() {

    List<Layer> topLayers = new ArrayList<>();

    Layer[] values = layers.values().toArray(new Layer[layers.values().size()]);
    for (int i = values.length - 1; i >= 0; i--) {
      Layer layer = values[i];
      if (!layer.isEmpty() && layer.isPackageLayer()) {
        if (layer.getPackages().stream().anyMatch(pkg -> OS_PACKAGES.contains(pkg.getType().toString().toUpperCase()))) {
          topLayers.add(layer);
          break;
        }
        topLayers.add(layer);
      }
    }
    return topLayers;
  }

  public ImageId getId() {
    return id;
  }

  public ImageType getType() {
    return type;
  }

  public void setDigest(Digest digest) {
    this.digest = digest;
  }

  public Digest getDigest() {
    return digest;
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
    List<Layer> layers = getPackageLayer();
    return layers == null ? emptyList() : layers.stream().map(Layer::getFiles).flatMap(List::stream).collect(toList());
  }

  public List<Layer> getLayers() {
    return unmodifiableList(new ArrayList<>(layers.values()));
  }

  public Set<Package> getPackages() {
    List<Layer> layers = getPackageLayer();
    return layers == null ? emptySet() : layers.stream().map(Layer::getPackages).flatMap(Set::stream).collect(toSet());
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
      Image other = (Image) obj;
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
        .add("Digest=" + digest)
        .add("Type=" + type)
        .add("Created=" + created)
        .add("Size=" + size)
        .add("Layers=" + layers)
        .toString();
  }
}
