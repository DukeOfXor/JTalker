package lanchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javafx.application.Platform;
import lanchat.gui.ServerGUI;

public class Server extends Thread{

  private Boolean running = false;
  private ServerGUI gui;
  private ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();
  private ServerSocket serverSocket;
  
  public static final int PORT = 8954;
  
  public Server(ServerGUI gui) {
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

     if(e1.getMessage().equals("socket closed")){
       //serverSocket.accept() will throw this exception because the socket gets closed, while its waiting for connections
       //this can't be prevented, therefore the exception is ignored
     } else if(e1.getMessage().equals("Address already in use: JVM_Bind")){
       //an exception with this message gets thrown if the address is already in use, i.e. when there is already a server running on this pc
       displayGuiMessage("Can't start server. Maybe there is already a server running on this computer?");
     } else {
       e1.printStackTrace();
     }
    }
    catch (IOException e2) {
      e2.printStackTrace();
    }
  }
  
  synchronized void broadcast(Object message){
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
