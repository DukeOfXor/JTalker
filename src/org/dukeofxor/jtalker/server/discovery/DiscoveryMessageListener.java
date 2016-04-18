package org.dukeofxor.jtalker.server.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javafx.application.Platform;

import org.dukeofxor.jtalker.common.discovery.DiscoveryMessage;
import org.dukeofxor.jtalker.gui.ServerGUI;

public class DiscoveryMessageListener extends Thread{

  private DatagramSocket socket;
  private boolean running;
  private ServerGUI gui;

  public DiscoveryMessageListener(ServerGUI gui) {
    this.gui = gui;
  }
  
  @Override
  public void run() {
    try {
      socket = new DatagramSocket(8955, InetAddress.getByName("0.0.0.0"));
      socket.setBroadcast(true);
      
      displayGuiMessage("DiscoveryMessageListener started");
      while (running) {
        byte[] buffer = new byte[15000];
        
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        //Blocks until a datagram is received
        socket.receive(packet);
        
        displayGuiMessage("Received DiscoveryMessage from " + packet.getAddress().getHostAddress());
        
        //Check if the packet contains the right message
        String message = new String(packet.getData()).trim();
        if(message.equals(DiscoveryMessage.DISCOVERY_REQUEST)){
          //TODO remove syso
          System.out.println(DiscoveryMessage.DISCOVERY_RESPONSE.toString());
          byte[] dataToSend = DiscoveryMessage.DISCOVERY_RESPONSE.toString().getBytes();
          
          //Send response
          DatagramPacket datagramPacketToSend = new DatagramPacket(dataToSend, dataToSend.length, packet.getAddress(), packet.getPort());
          socket.send(datagramPacketToSend);
          
          displayGuiMessage("Sent DiscoveryResponse to " + packet.getAddress().getHostAddress());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void displayGuiMessage(String message){
    Platform.runLater(new Runnable() {
      
      @Override
      public void run() {
        gui.displayMessage(message);
      }
    });
  }
}
