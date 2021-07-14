package com.rapid7.container.analyzer.docker.analyzer;

import com.rapid7.docker.model.image.Image;
import java.io.IOException;

public interface ImageHandler {

  void handle(Image image) throws IOException;
}
