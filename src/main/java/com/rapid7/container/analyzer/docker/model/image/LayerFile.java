package com.rapid7.container.analyzer.docker.model.image;

import static java.util.Objects.requireNonNull;

public class LayerFile {

  private FileState state;
  private File file;

  public LayerFile(File file, FileState state) {
    this.state = state;
    this.file = file;
  }

  public FileState getState() {
    return state;
  }

  public File getFile() {
    return file;
  }

  public void setState(FileState state) {
    this.state = requireNonNull(state);
  }
}
