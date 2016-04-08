package lanchat.client;

import java.io.IOException;
import java.util.ArrayList;

import lanchat.common.ServerMessage;
import lanchat.common.ServerMessageType;

public class MessageListener extends Thread{

  private Client client;
  private boolean running;

  public MessageListener(Client client) {
    this.client = client;
  }
  
  public void run() {
    running = true;
    while(running ){
      try {
        ServerMessage serverMessage = (ServerMessage) client.getInputStream().readObject();
        
        ServerMessageType type = serverMessage.getType();
        String message = serverMessage.getMessage();
        String username = serverMessage.getUsername();
        ArrayList<String> usernames = serverMessage.getUsernames();
        
        switch (type) {
          case MESSAGE:
            //TODO update gui (display message)
            break;
          case CLIENTLIST:
            //TODO update gui (update clientlist)
            break;
        }
      } catch (ClassNotFoundException e) {
        //can't do anything if class is not found
      } catch (IOException e) {
       //server has closed the connection
        //TODO update gui (switch back to login view)
      }
    }
  }

  public void shutdown() {
    running = false;
  }
}
