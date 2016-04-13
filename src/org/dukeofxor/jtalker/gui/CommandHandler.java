package org.dukeofxor.jtalker.gui;

import org.dukeofxor.jtalker.server.ClientThread;
import org.dukeofxor.jtalker.server.Server;

import javafx.collections.ObservableList;

public class CommandHandler {
  
  public static final String TO_MANY_ARGUMENTS = "Too many arguments for command ";
  public static final String MISSING_ARGUMENT = "Missing arguments for command ";
  public static final String USAGE = "Usage: ";
  
  //syntax helps for each command
  public static final String START_USAGE = "start";
  public static final String STOP_USAGE = "stop";
  public static final String CLEAR_USAGE = "clear";
  public static final String EXIT_USAGE = "exit";
  public static final String LIST_USAGE = "list";
  public static final String KICK_USAGE = "kick <clientname>";
private static final String WHISPER_USAGE = "whisper username message";
private static final String SHUTDOWN_USAGE = "shutdown";
  
  private ServerGUI gui;

  public CommandHandler(ServerGUI gui) {
    this.gui = gui;
  }
  
  public void handleCommand(String command){
    String[] cmd = command.split("\\s+");
    if(cmd[0].equals("start")){
      startServer(cmd);
      return;
    }
    if(cmd[0].equals("stop")){
      stopServer(cmd);
      return;
    }
    if(cmd[0].equals("clear")){
      clearConsole(cmd);
      return;
    }
    if(cmd[0].equals("exit")){
      exit(cmd);
      return;
    }
    if(cmd[0].equals("shutdown")){
    	shutdown(cmd);
    	return;
    }
    if(cmd[0].equals("list")){
      clientList(cmd);
      return;
    }
    if(cmd[0].equals("kick")){
    	kickClient(cmd);
    	return;
    }
  }
  
private void shutdown(String[] cmd) {
	if(cmd.length > 1){
	      gui.displayMessage(TO_MANY_ARGUMENTS + cmd[0] + "\n" + USAGE + SHUTDOWN_USAGE);
	      return;
	}
	if(gui.getLanChatServer().isRunning()){
	      gui.getLanChatServer().shutdown();
	}
	gui.close();
}

private void kickClient(String[] cmd) {
	  if(cmd.length == 2){
	    gui.getLanChatServer().kickClient(cmd[1]);	    	
	    return;
	  }
	  if(cmd.length > 2){
	    gui.displayMessage(TO_MANY_ARGUMENTS + cmd[0] + "\n" + USAGE + KICK_USAGE);
	    return;
	  }
	  if(cmd.length < 2){
	    gui.displayMessage(MISSING_ARGUMENT + cmd[0] + "\n" + USAGE + KICK_USAGE);
	  }
}

private void clientList(String[] cmd) {
    if(cmd.length > 1){
      gui.displayMessage(TO_MANY_ARGUMENTS + cmd[0] + "\n" + USAGE + LIST_USAGE);
      return;
    }
    if(gui.getLanChatServer().isRunning()){
      ObservableList<ClientThread> connectedClients = gui.getLanChatServer().getConnectedClients();
      StringBuilder output = new StringBuilder("Connected clients:");
      for (ClientThread clientThread : connectedClients) {
        output.append("\n");
        output.append(clientThread.getIp() + ", ");
        output.append(clientThread.getUsername());
      }
      
      gui.displayMessage(output.toString());
    } else {
      gui.displayMessage("Please start the server before you execute this command");
    }
  }

  private void startServer(String[] cmd){
    if(cmd.length > 1){
      gui.displayMessage(TO_MANY_ARGUMENTS + cmd[0] + "\n" + USAGE + START_USAGE);
      return;
    }
    if(!gui.getLanChatServer().isRunning()){
      gui.setLanChatServer(new Server(gui));
      gui.getLanChatServer().start();
    } else {
      gui.displayMessage("Server already running");
    }
  }
  
  private void stopServer(String[] cmd) {
    if(cmd.length > 1){
      gui.displayMessage(TO_MANY_ARGUMENTS + cmd[0] + "\n" + USAGE + STOP_USAGE);
      return;
    }
    if(gui.getLanChatServer().isRunning()){
      gui.getLanChatServer().shutdown();
    } else {
      gui.displayMessage("Server not running");
    }
  }
  
  private void clearConsole(String[] cmd) {
    if(cmd.length > 1){
      gui.displayMessage(TO_MANY_ARGUMENTS + cmd[0] + "\n" + USAGE + CLEAR_USAGE);
      return;
    }
    gui.clearConsole();
  }
  
  private void exit(String[] cmd) {
    if(cmd.length > 1){
      gui.displayMessage(TO_MANY_ARGUMENTS + cmd[0] + "\n" + USAGE + EXIT_USAGE);
      return;
    }
    if(gui.getLanChatServer().isRunning()){
      gui.displayMessage("Can't exit while server is running. Plase stop the server before executing this command");
      return;
    }
    gui.close();
  }
}
