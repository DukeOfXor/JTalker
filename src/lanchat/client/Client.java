package lanchat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
  private MessageListener messageListener;

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
      setInputStream(new ObjectInputStream(socket.getInputStream()));
      setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
    } catch (IOException e) {
      displayLoginErrorMessage("Connection failed");
      return;
    }
    
    messageListener = new MessageListener(this);
    messageListener.start();
    
    try {
      getOutputStream().writeObject(new ClientMessage(ClientMessageType.LOGIN, username, ""));
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
      getOutputStream().writeObject(new ClientMessage(ClientMessageType.LOGOUT, "", ""));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void disconnect() {
    //ignoring these exceptions, there is not much i can do here
      try {
        if(getInputStream() != null){
        getInputStream().close();
        }
      } catch (IOException e) {}
      
      try {
        if(getOutputStream() != null){
          getOutputStream().close();
        }
      } catch (IOException e) {}
      
      try {
        if(socket != null){
          socket.close();
        }
      } catch (IOException e) {}
      
      messageListener.shutdown();
      //TODO update gui (switch back to login view)
  }

  public ObjectInputStream getInputStream() {
    return inputStream;
  }

  private void setInputStream(ObjectInputStream inputStream) {
    this.inputStream = inputStream;
  }

  public ObjectOutputStream getOutputStream() {
    return outputStream;
  }

  private void setOutputStream(ObjectOutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public String getServerIp() {
    return socket.getInetAddress().toString().replace("/", "");
  }
}
