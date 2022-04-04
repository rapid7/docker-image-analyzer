/***************************************************************************
 * COPYRIGHT (C) 2022, Rapid7 LLC, Boston, MA, USA.
 * All rights reserved. This material contains unpublished, copyrighted
 * work including confidential and proprietary information of Rapid7.
 **************************************************************************/

package com.rapid7.container.analyzer.docker.model;

public class MavenArtifactInfo {

  private String artifactId;
  private String groupId;
  private String version;

  public MavenArtifactInfo(String artifactId, String groupId, String version) {
    this.artifactId = artifactId;
    this.setGroupId(groupId);
    this.setVersion(version);
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

}
