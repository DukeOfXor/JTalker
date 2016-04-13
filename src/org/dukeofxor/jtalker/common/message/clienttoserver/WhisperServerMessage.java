package org.dukeofxor.jtalker.common.message.clienttoserver;

import java.io.Serializable;

public class WhisperServerMessage implements Serializable{

	private static final long serialVersionUID = -4063640636972711289L;
	  private String username;
	  private String text;
	  private String sender;

	  public WhisperServerMessage(String username, String text, String sender) {
	    this.setUsername(username);
	    this.setText(text);
	    this.setSender(sender);
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

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
