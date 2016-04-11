package org.dukeofxor.jtalker.common.message.clienttoserver;

import java.io.Serializable;

public class LoginClientMessage implements Serializable{

  private static final long serialVersionUID = -5570709010741836768L;
  private String username;

  public LoginClientMessage(String username) {
    setUsername(username);
  }

  public String getUsername() {
    return username;
  }

  private void setUsername(String username) {
    this.username = username;
  }
}
