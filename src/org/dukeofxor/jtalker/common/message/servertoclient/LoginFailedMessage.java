package org.dukeofxor.jtalker.common.message.servertoclient;

import java.io.Serializable;

public class LoginFailedMessage implements Serializable{

  private static final long serialVersionUID = -478477537475280063L;
  private String reason;

  public LoginFailedMessage(String reason) {
    this.setReason(reason);
    
  }

  public String getReason() {
    return reason;
  }

  private void setReason(String reason) {
    this.reason = reason;
  }
}
