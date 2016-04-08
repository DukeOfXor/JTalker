package lanchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Platform;
import lanchat.common.message.clienttoserver.LoginMessageClient;
import lanchat.common.message.clienttoserver.LogoutMessageClient;
import lanchat.common.message.clienttoserver.TextMessageClient;
import lanchat.common.message.clienttoserver.WhoisinMessageClient;
import lanchat.common.message.servertoclient.ClientlistMessageServer;
import lanchat.common.message.servertoclient.TextMessageServer;
import lanchat.gui.ServerGUI;

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
      if(receivedObject.getClass().equals(LoginMessageClient.class)){
        LoginMessageClient loginMessage = (LoginMessageClient) receivedObject;
        
        this.username = loginMessage.getUsername();
        isLoggedIn = true;
        displayGuiMessage("Logged in");
        continue;
      }
      
      //The following messages from the client only get handled if the client is logged in
      //If a not logged in client sends such a message, he will be disconnected
      if(isLoggedIn){
        //LogoutMessage
        if(receivedObject.getClass().equals(LogoutMessageClient.class)){
            isLoggedIn = false;
            displayGuiMessage("Logged out");
            running = false;
            displayGuiMessage("Disconnected");
            continue;
          }
        
        //TextMessage
        if(receivedObject.getClass().equals(TextMessageClient.class)){
          TextMessageClient textMessage = (TextMessageClient) receivedObject;
          
          server.broadcast(new TextMessageServer(username, textMessage.getText()));
          displayGuiMessage("Sent TextMessage: " + textMessage.getText());
        }
        
        //WhoisinMessage
       if(receivedObject.getClass().equals(WhoisinMessageClient.class)){
         ArrayList<String> clientList = new ArrayList<>();
         for (ClientThread clientThread : server.getClientList()) {
           if(clientThread.isLoggedIn()){
             clientList.add(clientThread.getUsername());
           }
         }
         
         ClientlistMessageServer clientlistMessageServer = new ClientlistMessageServer(clientList);
         writeMessage(clientlistMessageServer);
         displayGuiMessage("Sent WhoisinMessage");
       }
      } else {
        running = false;
        displayGuiMessage("Disconnected");
        continue;
      }
//      switch (type) {
//        case MESSAGE:
//          if(isLoggedIn){
//            server.broadcast(new ServerMessage(ServerMessageType.MESSAGE, this.username, message));
//            displayGuiMessage("Sent message: " + message);
//          } else {
//            displayGuiMessage("Tried to send a message while not logged in");
//            running = false;
//            displayGuiMessage("Disconnected");
//          }
//          break;
//        case LOGIN:
//          this.username = username;
//          isLoggedIn = true;
//          displayGuiMessage("Logged in");
//          break;
//        case LOGOUT:
//          if(isLoggedIn){
//            isLoggedIn = false;
//            displayGuiMessage("Logged out");
//            running = false;
//            displayGuiMessage("Disconnected");
//          } else {
//            displayGuiMessage("Tried to logout while not logged in");
//            running = false;
//            displayGuiMessage("Disconnected");
//          }
//          break;
//         case WHOISIN:
//           if(isLoggedIn){
//             ArrayList<String> clientList = new ArrayList<>();
//             for (ClientThread clientThread : server.getClientList()) {
//               if(clientThread.isLoggedIn()){
//                 clientList.add(clientThread.getUsername());
//               }
//            }
//             ServerMessage serverMessage = new ServerMessage(ServerMessageType.CLIENTLIST, clientList);
//             writeMessage(serverMessage);
//             displayGuiMessage("Sent WHOISIN request");
//           } else {
//             displayGuiMessage("Tried to send a WHOISIN request while not logged in");
//             running = false;
//             displayGuiMessage("Disconnected");
//           }
//           break;
//      }
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
}
