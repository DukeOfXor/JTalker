package lanchat.client;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import lanchat.common.message.servertoclient.ClientListServerMessage;
import lanchat.common.message.servertoclient.TextServerMessage;
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
        Object receivedObject = client.getInputStream().readObject();
        
        //TextMessage
        if(receivedObject.getClass().equals(TextServerMessage.class)){
          TextServerMessage textMessage = (TextServerMessage) receivedObject;
          
          displayGuiMessage(textMessage.getUsername(), textMessage.getText());
          continue;
        }
        
        //ClientlistMessage
        if(receivedObject.getClass().equals(ClientListServerMessage.class)){
          ClientListServerMessage clientListMessage = (ClientListServerMessage) receivedObject;
          
          displayGuiClientlist(clientListMessage.getClientlist());
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
