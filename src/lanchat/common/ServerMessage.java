package lanchat.common;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerMessage implements Serializable{

  private static final long serialVersionUID = -1887936946410166492L;
  private ServerMessageType type;
  private String username;
  private String message;
  private ArrayList<String> usernames;

  public ServerMessage(ServerMessageType type, String username, String message) {
    this.setType(type);
    this.setUsername(username);
    this.setMessage(message);
    this.setUsernames(new ArrayList<String>());
  }
  
  public ServerMessage(ServerMessageType type, ArrayList<String> usernames){
    this.setType(type);
    this.setUsernames(usernames);
    this.setMessage("");
    this.setUsername("");
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

  public void setMessage(String message) {
    this.message = message;
  }

  public ServerMessageType getType() {
    return type;
  }

  private void setType(ServerMessageType type) {
    this.type = type;
  }

  public ArrayList<String> getUsernames() {
    return usernames;
  }

  private void setUsernames(ArrayList<String> usernames) {
    this.usernames = usernames;
  }
}
