package com.rapid7.container.analyzer.docker.model.image;

public class PackageValidationException extends Exception {
  public PackageValidationException(String errorMessage) {
    super(errorMessage);
  }
}
