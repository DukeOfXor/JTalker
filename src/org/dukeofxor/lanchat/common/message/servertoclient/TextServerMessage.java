package org.dukeofxor.lanchat.common.message.servertoclient;

import java.io.Serializable;

public class TextServerMessage implements Serializable{

  private static final long serialVersionUID = -4528092934828619618L;
  private String username;
  private String text;

  public TextServerMessage(String username, String text) {
    this.setUsername(username);
    this.setText(text);
  }

  public String getUsername() {
    return username;
  }

  private void setUsername(String username) {
    this.username = username;
  }

  public String getText() {
    return text;
  }

  private void setText(String text) {
    this.text = text;
  }
}
