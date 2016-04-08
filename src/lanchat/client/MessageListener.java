package lanchat.client;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import lanchat.common.ServerMessage;
import lanchat.common.ServerMessageType;
import lanchat.gui.ClientGUI;

public class MessageListener extends Thread{

  private Client client;
  private boolean running;
  private ClientGUI gui;

  public MessageListener(Client client, ClientGUI gui) {
    this.client = client;
    this.gui = gui;
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
            displayGuiMessage(username, message);
            break;
          case CLIENTLIST:
            displayGuiClientlist(usernames);
            break;
        }
      } catch (ClassNotFoundException e) {
        //can't do anything if class is not found
      } catch (IOException e) {
       //server has closed the connection
        startGuiLoginView("Connection to server lost");
        break;
      }
    }
  }

  public void shutdown() {
    running = false;
  }
  
  private void startGuiLoginView(String reason) {
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.startLoginView();
        gui.setLoginErrorText(reason);
      }
    });
  }
  
  private void displayGuiClientlist(ArrayList<String> clientlist){
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.setClientList(clientlist);
      }
    });
  }
  
  private void displayGuiMessage(String username, String message){
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.displayMessage(username, message);
      }
    });
  }
}
