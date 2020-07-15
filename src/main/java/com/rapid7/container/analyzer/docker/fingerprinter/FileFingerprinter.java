package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.HashId;
import com.rapid7.container.analyzer.docker.model.HashType;
import com.rapid7.container.analyzer.docker.model.LayerPathWrapper;
import com.rapid7.container.analyzer.docker.model.image.File;
import com.rapid7.container.analyzer.docker.model.image.FileState;
import com.rapid7.container.analyzer.docker.model.image.FileType;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.image.LayerFile;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import static com.rapid7.container.analyzer.docker.model.image.FileType.BLOCK_DEVICE;
import static com.rapid7.container.analyzer.docker.model.image.FileType.CHARACTER_DEVICE;
import static com.rapid7.container.analyzer.docker.model.image.FileType.DIRECTORY;
import static com.rapid7.container.analyzer.docker.model.image.FileType.REGULAR_FILE;
import static com.rapid7.container.analyzer.docker.model.image.FileType.SYMBOLIC_LINK;
import static com.rapid7.container.analyzer.docker.model.image.FileType.WHITEOUT;
import static com.rapid7.container.analyzer.docker.model.image.FileType.WHITEOUT_OPAQUE;


public class FileFingerprinter implements LayerFileHandler {

  private static final Pattern WHITEOUT_AUFS_PREFIX = Pattern.compile("^(?<prefix>.*/)?\\.wh\\.(?!.wh\\.)(?<suffix>.*)$");
  private static final Pattern WHITEOUT_OPAQUE_AUFS_PREFIX = Pattern.compile("^(?<prefix>.*/)?\\.wh\\.\\.wh\\.\\.opq$");

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, LayerPathWrapper layerPathWrapper) throws IOException {

    // prefix with / to indicate its an absolute path
    name = "/" + name;

    // remove trailing slashes (for dir style)
    if (name.endsWith("/"))
      name = name.substring(0, name.length() - 1);

    FileType fileType = fileTypeOf(entry);
    String linkTarget = null;

    // handle whiteout files
    Matcher matcher = WHITEOUT_AUFS_PREFIX.matcher(name);
    if (matcher.matches()) {
      linkTarget = matcher.group("prefix") + matcher.group("suffix");
      fileType = WHITEOUT;
    }

    // handle whiteout opaque files
    matcher = WHITEOUT_OPAQUE_AUFS_PREFIX.matcher(name);
    if (matcher.matches()) {
      linkTarget = matcher.group("prefix");
      fileType = WHITEOUT_OPAQUE;
    }

    File file = new File(fileType, name, entry.getSize()).setPermissions(entry.getMode());
    if (entry.isSymbolicLink())
      file.setLinkTarget(entry.getLinkName());
    else if (entry.isFile()) {
      file.setLinkTarget(linkTarget);
      file.setChecksum(checksum(contents));
    }

    layerPathWrapper.getLayer().addFile(new LayerFile(file, FileState.ADDED));
  }

  private HashId checksum(InputStream contents) throws IOException {
    return new HashId(DigestUtils.sha256Hex(contents), HashType.SHA256);
  }

  private FileType fileTypeOf(TarArchiveEntry entry) {
    if (entry.isDirectory())
      return DIRECTORY;
    else if (entry.isSymbolicLink())
      return SYMBOLIC_LINK;
    else if (entry.isCharacterDevice())
      return CHARACTER_DEVICE;
    else if (entry.isBlockDevice())
      return BLOCK_DEVICE;
    else
      return REGULAR_FILE;
  }
}
