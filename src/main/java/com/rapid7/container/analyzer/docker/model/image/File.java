package com.rapid7.container.analyzer.docker.model.image;

import com.rapid7.container.analyzer.docker.model.HashId;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.GROUP_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static java.util.Objects.requireNonNull;

public class File {

  private static final PosixFilePermission[] PERMISSIONS = { OTHERS_EXECUTE, OTHERS_WRITE, OTHERS_READ, GROUP_EXECUTE, GROUP_WRITE, GROUP_READ, OWNER_EXECUTE, OWNER_WRITE, OWNER_READ };

  private FileType type;
  private String name;
  private long size;
  private HashId checksum;
  private String linkTarget;
  private Set<PosixFilePermission> permissions;

  public File(FileType type, String name, long size) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.size = size;
    permissions = new HashSet<>();
  }

  public FileType getType() {
    return type;
  }

  public File setType(FileType type) {
    this.type = type;
    return this;
  }

  public String getName() {
    return name;
  }

  public long getSize() {
    return size;
  }

  public Set<PosixFilePermission> getPermissions() {
    return permissions;
  }

  public File setPermissions(Set<PosixFilePermission> permissions) {
    this.permissions = requireNonNull(permissions);
    return this;
  }

  public File setPermissions(int mode) {
    permissions = posixFilePermissions(mode);
    return this;
  }

  public HashId getChecksum() {
    return checksum;
  }

  public File setChecksum(HashId checksum) {
    this.checksum = checksum;
    return this;
  }

  public String getLinkTarget() {
    return linkTarget;
  }

  public File setLinkTarget(String linkTarget) {
    this.linkTarget = linkTarget;
    return this;
  }

  private static Set<PosixFilePermission> posixFilePermissions(int mode) {
    int mask = 1;
    Set<PosixFilePermission> perms = new HashSet<>();
    for (PosixFilePermission flag : PERMISSIONS) {
      if (flag != null && (mask & mode) != 0)
        perms.add(flag);

      mask = mask << 1;
    }
    return perms;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, size, linkTarget, permissions, checksum);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    else if (!(obj instanceof File))
      return false;
    else {
      File other = (File)obj;
      return Objects.equals(type, other.type)
          && Objects.equals(name, other.name)
          && size == other.size
          && Objects.equals(linkTarget, other.linkTarget)
          && Objects.equals(permissions, other.permissions)
          && Objects.equals(checksum, other.checksum);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", File.class.getSimpleName() + "[", "]")
        .add("Name=" + name)
        .add("Size=" + size)
        .add("Link Target=" + linkTarget)
        .add("Type=" + type)
        .add("Permissions=" + type.getCode() + PosixFilePermissions.toString(permissions))
        .add("Checksum=" + checksum)
        .toString();
  }
}
