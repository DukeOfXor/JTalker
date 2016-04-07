package lanchat.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javafx.application.Platform;
import lanchat.common.ServerMessage;
import lanchat.gui.ServerGUI;

public class LanChatServer extends Thread{

  private Boolean running = false;
  private ServerGUI gui;
  private ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();
  private ServerSocket serverSocket;
  
  public static final int PORT = 8954;
  
  public LanChatServer(ServerGUI gui) {
    this.gui = gui;
  }
  
  @Override
  public void run() {
    try {
      displayGuiMessage("Starting server...");
      serverSocket = new ServerSocket(PORT);
    
      running = true;
      changeGuiState();
      
      displayGuiMessage("Server started");
      while(running){
        //server loop
        Socket socket = serverSocket.accept(); 
        if(!running){
          break;
        }
        ClientThread clientThread = new ClientThread(socket, gui, this);
        getClientList().add(clientThread);
        clientThread.start();
      }
    } catch (SocketException e1) {
      //serverSocket.accept() will throw an exception because the socket gets closed, while its waiting for connections
      //this can't be prevented, therefore the exception is ignored
     if(!e1.getMessage().equals("socket closed")){
     }
    }
    catch (IOException e2) {
      e2.printStackTrace();
    }
  }
  
  synchronized void broadcast(ServerMessage message){
    int removedClientsCounter = 0;
    for (ClientThread clientThread : getClientList()) {
      if(clientThread.isConnected()){
        clientThread.writeMessage(message);
      } else {
        removeClient(clientThread);
        removedClientsCounter++;
      }
    }
    
    if(removedClientsCounter > 0){
      displayGuiMessage("Removed " + removedClientsCounter + " clients, which were not properly disconnected");
    }
  }
  
  synchronized void removeClient(ClientThread clientThreadToRemove){
        getClientList().remove(clientThreadToRemove);
  }
  
  public void shutdown(){
    displayGuiMessage("Stopping server...");
    try {
      if(serverSocket != null){
        serverSocket.close();
      }
      for (ClientThread clientThread : getClientList()) {
        clientThread.shutdown();
      }
      running = false;
      changeGuiState();
      displayGuiMessage("Server stopped");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Boolean isRunning() {
    return running;
  }
  
  private void changeGuiState() {
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.updateState();        
      }
    });
    
  }
  
  private void displayGuiMessage(String message){
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.displayMessage(message);
      }
    });
  }
  
  private void displayGuiMessage(String prefix, String message){
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.displayMessage(prefix, message);
      }
    });
  }

  public ArrayList<ClientThread> getClientList() {
    return clientList;
  }

  private void setClientList(ArrayList<ClientThread> clientList) {
    this.clientList = clientList;
  }
}
