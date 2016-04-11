package org.dukeofxor.lanchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.dukeofxor.lanchat.common.message.clienttoserver.LoginClientMessage;
import org.dukeofxor.lanchat.common.message.clienttoserver.LogoutClientMessage;
import org.dukeofxor.lanchat.common.message.clienttoserver.TextClientMessage;
import org.dukeofxor.lanchat.common.message.clienttoserver.WhoisinClientMessage;
import org.dukeofxor.lanchat.common.message.servertoclient.ClientListServerMessage;
import org.dukeofxor.lanchat.common.message.servertoclient.TextServerMessage;
import org.dukeofxor.lanchat.gui.ServerGUI;

import javafx.application.Platform;

public class ClientThread extends Thread{
  
  private Socket socket;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private ServerGUI gui;
  private Server server;
  private String username;
  private boolean isLoggedIn;
  private Object receivedObject;

  public ClientThread(Socket socket, ServerGUI gui, Server server) {
    this.socket = socket;
    this.gui = gui;
    this.server = server;
    
    try {
      outputStream = new ObjectOutputStream(socket.getOutputStream());
      inputStream = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    displayGuiMessage("Connected");
  }
  
  public void run() {
    boolean running = true;
    while(running){
      try {
        receivedObject = inputStream.readObject();
      } catch (ClassNotFoundException e) {
        server.removeClient(this);
        shutdown();
        break;
      } catch (IOException e) {
        //this will throw if a client disconnects
        displayGuiMessage("Disconnected without logging out");
        server.removeClient(this);
        shutdown();
        break;
      }
      
      //The following messages from the client get handled, even if the client is not logged in
      
      //LoginMessage
      if(receivedObject.getClass().equals(LoginClientMessage.class)){
        LoginClientMessage loginMessage = (LoginClientMessage) receivedObject;
        
        this.username = loginMessage.getUsername();
        isLoggedIn = true;
        server.addClient(this);
        displayGuiMessage("Logged in");
        continue;
      }
      
      //The following messages from the client only get handled if the client is logged in
      //If a not logged in client sends such a message, he will be disconnected
      if(isLoggedIn){
        //LogoutMessage
        if(receivedObject.getClass().equals(LogoutClientMessage.class)){
            isLoggedIn = false;
            server.removeClient(this);
            displayGuiMessage("Logged out");
            running = false;
            displayGuiMessage("Disconnected");
            continue;
          }
        
        //TextMessage
        if(receivedObject.getClass().equals(TextClientMessage.class)){
          TextClientMessage textMessage = (TextClientMessage) receivedObject;
          
          server.broadcast(new TextServerMessage(username, textMessage.getText()));
          displayGuiMessage("Sent TextMessage: " + textMessage.getText());
        }
        
        //WhoisinMessage
       if(receivedObject.getClass().equals(WhoisinClientMessage.class)){
         ArrayList<String> usernameList = new ArrayList<>();
         for (ClientThread client : server.getConnectedClients()) {
          usernameList.add(client.getUsername());
         }
         
         ClientListServerMessage clientListMessage = new ClientListServerMessage(usernameList);
         writeMessage(clientListMessage);
         displayGuiMessage("Sent WhoisinMessage");
       }
      } else {
        running = false;
        displayGuiMessage("Disconnected");
        continue;
      }
    }
    server.removeClient(this);
    shutdown();
  }
  
  public void shutdown() {
      try {
        if(inputStream != null){
        inputStream.close();
        }
        if(outputStream != null){
          outputStream.close();
        }
        if(socket != null){
          socket.close();
        }
      } catch (IOException e) {
      }
  }
  
  private void displayGuiMessage(String message){
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        String inetAddress = socket.getInetAddress().toString().replace("/", "");
        if(getUsername() != null){
          if(!getUsername().isEmpty()){
            gui.displayMessage(inetAddress + "][" + getUsername(), message);
          } else {
            gui.displayMessage(inetAddress, message);
          }
        } else {
          gui.displayMessage(inetAddress, message);
        }
      }
    });
  }

  public boolean isConnected() {
    if(socket.isConnected()){
      return true;
    } else {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return false;
    }
  }

  public void writeMessage(Object message) {
    try {
      outputStream.writeObject(message);
    } catch (IOException e) {
      displayGuiMessage("Error sending message");
    }
  }

  public boolean isLoggedIn() {
    return isLoggedIn;
  }

  public String getUsername() {
    return username;
  }

  public String getIp() {
    return socket.getInetAddress().getHostAddress();
  }

  public void logout() {
	server.getConnectedClients().remove(this);
  }
}
