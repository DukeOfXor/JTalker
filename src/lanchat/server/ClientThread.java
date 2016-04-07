package lanchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Platform;
import lanchat.common.ClientMessage;
import lanchat.common.ClientMessageType;
import lanchat.common.ServerMessage;
import lanchat.common.ServerMessageType;
import lanchat.gui.ServerGUI;

public class ClientThread extends Thread{
  
  private Socket socket;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private ServerGUI gui;
  private ClientMessage clientMessage;
  private LanChatServer server;
  private String username;
  private boolean isLoggedIn;

  public ClientThread(Socket socket, ServerGUI gui, LanChatServer server) {
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
        clientMessage = (ClientMessage) inputStream.readObject();
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
      
      ClientMessageType type = clientMessage.getType();
      String username = clientMessage.getUsername();
      String message = clientMessage.getMessage();
      
      switch (type) {
        case MESSAGE:
          if(isLoggedIn){
            server.broadcast(new ServerMessage(ServerMessageType.MESSAGE, this.username, message));
            displayGuiMessage("Sent message: " + message);
          } else {
            displayGuiMessage("Tried to send a message while not logged in");
            running = false;
            displayGuiMessage("Disconnected");
          }
          break;
        case LOGIN:
          this.username = username;
          isLoggedIn = true;
          displayGuiMessage("Logged in");
          break;
        case LOGOUT:
          if(isLoggedIn){
            isLoggedIn = false;
            displayGuiMessage("Logged out");
            running = false;
            displayGuiMessage("Disconnected");
          } else {
            displayGuiMessage("Tried to logout while not logged in");
            running = false;
            displayGuiMessage("Disconnected");
          }
          break;
         case WHOISIN:
           if(isLoggedIn){
             ArrayList<String> clientList = new ArrayList<>();
             for (ClientThread clientThread : server.getClientList()) {
               if(clientThread.isLoggedIn()){
                 clientList.add(clientThread.getUsername());
               }
            }
             ServerMessage serverMessage = new ServerMessage(ServerMessageType.CLIENTLIST, clientList);
             writeMessage(serverMessage);
             displayGuiMessage("Sent WHOISIN request");
           } else {
             displayGuiMessage("Tried to send a WHOISIN request while not logged in");
             running = false;
             displayGuiMessage("Disconnected");
           }
           break;
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
        String inetAddress = socket.getInetAddress().toString();
        inetAddress.replace("\\", "");
        if(getUsername() != null){
          if(!getUsername().isEmpty()){
            gui.displayMessage(inetAddress + " | " + getUsername(), message);
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

  public void writeMessage(ServerMessage message) {
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
}
