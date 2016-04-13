package org.dukeofxor.jtalker.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import org.dukeofxor.jtalker.common.message.clienttoserver.WhisperServerMessage;
import org.dukeofxor.jtalker.common.message.servertoclient.ClientListServerMessage;
import org.dukeofxor.jtalker.gui.ServerGUI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Server extends Thread{

  private Boolean running = false;
  private ServerGUI gui;
  private ObservableList<ClientThread> connectedClients = FXCollections.observableArrayList();
  private ServerSocket serverSocket;
  
  public static final int PORT = 8954;
  
  public Server(ServerGUI gui) {
    this.gui = gui;
    
    connectedClients.addListener(new ListChangeListener<ClientThread>() {

      @Override
      public void onChanged(javafx.collections.ListChangeListener.Change<? extends ClientThread> c) {
        if(running){
          ArrayList<String> usernames = new ArrayList<>();
          for (ClientThread clientThread : connectedClients) {
            usernames.add(clientThread.getUsername());
          }
          broadcast(new ClientListServerMessage(usernames));
        }
      }
    });
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
    for (ClientThread clientThread : getConnectedClients()) {
        clientThread.writeMessage(message);
    }
    
  }
  
  synchronized void addClient(ClientThread clientThreadToAdd){
    getConnectedClients().add(clientThreadToAdd);
  }
  
  synchronized void removeClient(ClientThread clientThreadToRemove){
        getConnectedClients().remove(clientThreadToRemove);
  }
  
  public void shutdown(){
    displayGuiMessage("Stopping server...");
    try {
      if(serverSocket != null){
        serverSocket.close();
      }
      for (ClientThread clientThread : getConnectedClients()) {
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

  public ObservableList<ClientThread> getConnectedClients() {
    return connectedClients;
  }
  
  public void kickClient(String username){
	  ClientThread clientToKick = null;
	  for (ClientThread clientThread : connectedClients) {
		if(clientThread.getUsername().equals(username)){
			clientToKick = clientThread;
		}
	}
	  if(clientToKick != null){
		  clientToKick.shutdown();
		  clientToKick.logout();
		  displayGuiMessage("Client [" + clientToKick.getUsername() + "] kicked");
	  }else{
		  displayGuiMessage("Client not Online");
	  }
  }

  public void whisper(String username, String message, String sender) {
	getClientThreadByName(username).writeMessage(new WhisperServerMessage(username, message, sender));
  }
  

/**
 * 
 * @param username
 * @return return the ClientThread with the specified username. Return null if user not online
 */
  public ClientThread getClientThreadByName(String username){
	for (ClientThread clientThread : connectedClients) {
		if(clientThread.getUsername().equals(username)){
			return clientThread;
		}
	}
	return null;
}
	  
  }
