package org.dukeofxor.jtalker.client;

import java.io.IOException;
import java.util.ArrayList;

import org.dukeofxor.jtalker.common.message.clienttoserver.WhisperServerMessage;
import org.dukeofxor.jtalker.common.message.servertoclient.ClientListServerMessage;
import org.dukeofxor.jtalker.common.message.servertoclient.LoginFailedMessage;
import org.dukeofxor.jtalker.common.message.servertoclient.TextServerMessage;
import org.dukeofxor.jtalker.gui.ClientGUI;

import com.sun.javafx.collections.SetListenerHelper;

import javafx.application.Platform;

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
        
        //WhisperMessage
        if(receivedObject.getClass().equals(WhisperServerMessage.class)){
        	WhisperServerMessage whisperMessage = (WhisperServerMessage) receivedObject;
        	
        	if(client.getUserName().equals(whisperMessage.getSender())){
        		displayGuiMessage(whisperMessage.getSender(), " You whispered to " + whisperMessage.getUsername() + whisperMessage.getText());
        	}else{
        		displayGuiMessage(whisperMessage.getSender(), " whispered to you: " + whisperMessage.getText());        		
        	}
        }
        
        //ClientlistMessage
        if(receivedObject.getClass().equals(ClientListServerMessage.class)){
          ClientListServerMessage clientListMessage = (ClientListServerMessage) receivedObject;
          
          displayGuiClientlist(clientListMessage.getClientlist());
        }
        
        //LoginFailedMessage
        if(receivedObject.getClass().equals(LoginFailedMessage.class)){
          LoginFailedMessage loginFailedMessage = (LoginFailedMessage) receivedObject;
          
          startGuiLoginView(loginFailedMessage.getReason());
          client.disconnect();
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
