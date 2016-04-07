package lanchat.common;

import java.io.Serializable;

public class ClientMessage implements Serializable{

  private static final long serialVersionUID = 686816445530380638L;
  
  private ClientMessageType type;
  private String username;
  private String message;
  
  public ClientMessage(ClientMessageType type, String username, String message) {
    this.setType(type);
    this.setUsername(username);
    this.setMessage(message);
  }

  public ClientMessageType getType() {
    return type;
  }

  private void setType(ClientMessageType type) {
    this.type = type;
  }

  public String getUsername() {
    return username;
  }

  private void setUsername(String username) {
    this.username = username;
  }

  public String getMessage() {
    return message;
  }

  private void setMessage(String message) {
    this.message = message;
  }
  
}
