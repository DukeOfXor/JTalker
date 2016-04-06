package lanchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javafx.application.Platform;
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
      serverSocket = new ServerSocket(PORT);
    
      running = true;
      changeGuiState();
    
      while(running){
        //server loop
        Socket socket = serverSocket.accept(); 
        if(!running){
          break;
        }
        ClientThread clientThread = new ClientThread(socket);
        clientList.add(clientThread);
        clientThread.start();
      }
    } catch (SocketException e1) {
      //serverSocket.accept() will throw an exception because the socket gets closed, while its waiting for connections
      //this can't be prevented, therefore the exception is ignored
     if(!e1.getMessage().equals("socket closed")){
       e1.printStackTrace();
     }
    }
    catch (IOException e2) {
      e2.printStackTrace();
    }
  }
  
  public void shutdown(){
    try {
      serverSocket.close();
      for (ClientThread clientThread : clientList) {
        clientThread.shutdown();
      }
      running = false;
      changeGuiState();
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
}
