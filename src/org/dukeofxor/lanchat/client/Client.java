package org.dukeofxor.lanchat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.dukeofxor.lanchat.common.message.clienttoserver.LoginClientMessage;
import org.dukeofxor.lanchat.common.message.clienttoserver.LogoutClientMessage;
import org.dukeofxor.lanchat.common.message.clienttoserver.TextClientMessage;
import org.dukeofxor.lanchat.gui.ClientGUI;

import javafx.application.Platform;

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
      displayGuiLoginErrorMessage("Connection failed");
      return;
    }
    
    try {
      setInputStream(new ObjectInputStream(socket.getInputStream()));
      setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
    } catch (IOException e) {
      displayGuiLoginErrorMessage("Connection failed");
      return;
    }
    
    messageListener = new MessageListener(this, gui);
    messageListener.start();
    
    try {
      getOutputStream().writeObject(new LoginClientMessage(username));
    } catch (IOException e) {
      e.printStackTrace();
      disconnect();
      displayGuiLoginErrorMessage("Login failed");
      return;
    }
    
    startGuiChatView();
  }

  
  public void logout(){
    try {
      if(getOutputStream() != null){
        if(!socket.isClosed() && socket.isConnected()){
          getOutputStream().writeObject(new LogoutClientMessage());
        }
      }
    } catch (SocketException e){
      //This will throw if the server closes the connection
    }catch (IOException e) {
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
      
      startGuiLoginView("Connection to server lost");
  }

  private void displayGuiLoginErrorMessage(String errorMessage) {
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.setLoginErrorText(errorMessage);
      }
    });
  }
  
  private void startGuiChatView(){
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.startChatView();
      }
    });
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

  public void sendTextMessage(String text) {
    try {
      getOutputStream().writeObject(new TextClientMessage(text));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
