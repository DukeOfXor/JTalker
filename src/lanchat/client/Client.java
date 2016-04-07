package lanchat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.application.Platform;
import lanchat.common.ClientMessage;
import lanchat.common.ClientMessageType;
import lanchat.gui.ClientGUI;

public class Client extends Thread{

  private String ip;
  private int port;
  private String username;
  private Socket socket;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private ClientGUI gui;

  public Client(String ip, int port, String username, ClientGUI gui) {
    this.ip = ip;
    this.port = port;
    this.username = username;
    this.gui = gui;
  }
  
  public void run(){
    try {
      socket = new Socket(ip, port);
    } catch (Exception e) {
      displayLoginErrorMessage("Connection failed");
      return;
    }
    
    try {
      inputStream = new ObjectInputStream(socket.getInputStream());
      outputStream = new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      displayLoginErrorMessage("Connection failed");
      return;
    }
    
    //TODO Start MessageListener
    
    try {
      outputStream.writeObject(new ClientMessage(ClientMessageType.LOGIN, username, ""));
    } catch (IOException e) {
      e.printStackTrace();
      disconnect();
      displayLoginErrorMessage("Login failed");
      return;
    }
    
    startChatView();
  }

  private void displayLoginErrorMessage(String errorMessage) {
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
         gui.setLoginErrorText(errorMessage);
      }
    });
  }
  
  private void startChatView(){
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.startChatView();
      }
    });
  }
  public void logout(){
    try {
      outputStream.writeObject(new ClientMessage(ClientMessageType.LOGOUT, "", ""));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void disconnect() {
    //ignoring these exceptions, there is not much i can do here
      try {
        if(inputStream != null){
        inputStream.close();
        }
      } catch (IOException e) {}
      
      try {
        if(outputStream != null){
          outputStream.close();
        }
      } catch (IOException e) {}
      
      try {
        if(socket != null){
          socket.close();
        }
      } catch (IOException e) {}
      
      //TODO update gui
  }
}
