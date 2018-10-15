package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.ImageHandler;
import com.rapid7.container.analyzer.docker.model.image.File;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.image.LayerFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.rapid7.container.analyzer.docker.model.image.FileState.REMOVED;
import static com.rapid7.container.analyzer.docker.model.image.FileState.UPDATED;
import static com.rapid7.container.analyzer.docker.model.image.FileType.WHITEOUT;
import static com.rapid7.container.analyzer.docker.model.image.FileType.WHITEOUT_OPAQUE;

public class WhiteoutImageHandler implements ImageHandler {

  @Override
  public void handle(Image image) throws IOException {

    // the prefixes of files/directories that have been white-out'ed
    Set<String> whiteoutPrefixes = new HashSet<>();

    List<Layer> layers = new ArrayList<>(image.getLayers());
    if (layers.isEmpty())
      return;

    // process layers in reverse order to apply removes
    Collections.reverse(layers);
    for (Layer layer : layers) {

      Set<String> whiteoutsInLayer = new HashSet<>();

      // process layer files in reverse order
      List<LayerFile> layerFiles = new ArrayList<>(layer.getFiles());
      Collections.reverse(layerFiles);

      for (LayerFile layerFile : layerFiles) {
        File file = layerFile.getFile();

        // white-out file
        if (file.getType() == WHITEOUT || file.getType() == WHITEOUT_OPAQUE) {
          whiteoutsInLayer.add(file.getLinkTarget());
        } else {
          // perform exact matching against any whiteouts in the same layer (this
          // handles the opaque whiteout of a directory, when the parent directory
          // is enumerated because of the presence of the whiteout file itself)
          if (whiteoutsInLayer.contains(file.getName()))
            layerFile.setState(REMOVED);

          // perform prefix matching on all whiteouts from any previous layers
          for (String whiteoutPrefix : whiteoutPrefixes) {
            if (file.getName().startsWith(whiteoutPrefix))
              layerFile.setState(REMOVED);
          }
        }
      }

      // only when the layer is fully processed will white-outs take affect (on children layers)
      whiteoutPrefixes.addAll(whiteoutsInLayer);
    }

    // process layers in normal order to apply updates
    Set<String> files = new HashSet<>();
    for (Layer layer : image.getLayers()) {
      for (LayerFile layerFile : layer.getFiles()) {
        File file = layerFile.getFile();

        if (layerFile.getState() == REMOVED)
          files.remove(file.getName());
        else {
          if (files.contains(file.getName()))
            layerFile.setState(UPDATED);

          // track all normal non-whiteout files
          if (file.getType() != WHITEOUT && file.getType() != WHITEOUT_OPAQUE)
            files.add(file.getName());
        }
      }
    }
  }
}
