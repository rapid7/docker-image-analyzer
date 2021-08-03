
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;

public class HistoryJson {

  private String command;
  private String created;
  private String comment;
  private String author;
  private boolean empty;

  @JsonProperty("created_by")
  public String getCommand() {
    return command;
  }

  public void setCommands(String command) {
    this.command = command;
  }

  @JsonProperty("created")
  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  @JsonProperty("comment")
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @JsonProperty("author")
  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  @JsonProperty("empty_layer")
  public boolean isEmpty() {
    return empty;
  }

  public void setEmpty(boolean empty) {
    this.empty = empty;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", HistoryJson.class.getSimpleName() + "[", "]")
        .add("Created=" + created)
        .add("Command=" + command)
        .add("Comment=" + comment)
        .add("Author=" + author)
        .add("Empty=" + empty)
        .toString();
  }
}
