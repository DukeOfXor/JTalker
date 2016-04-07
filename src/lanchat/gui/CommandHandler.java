package lanchat.gui;

import lanchat.server.LanChatServer;

public class CommandHandler {
  
  public static final String TO_MANY_ARGUMENTS = "Too many arguments for command ";
  public static final String USAGE = "Usage: ";
  
  //syntax helps for each command
  public static final String START_USAGE = "start";
  public static final String STOP_USAGE = "stop";
  public static final String CLEAR_USAGE = "clear";
  public static final String EXIT_USAGE = "exit";
  
  private ServerGUI gui;

  public CommandHandler(ServerGUI gui) {
    this.gui = gui;
  }
  
  public void handleCommand(String command){
    String[] cmd = command.split("\\s+");
    if(cmd[0].equals("start")){
      startServer(cmd);
    }
    if(cmd[0].equals("stop")){
      stopServer(cmd);
    }
    if(cmd[0].equals("clear")){
      clearConsole(cmd);
    }
    if(cmd[0].equals("exit")){
      exit(cmd);
    }
  }
  


  private void startServer(String[] cmd){
    if(cmd.length > 1){
      gui.displayMessage(TO_MANY_ARGUMENTS + cmd[0] + "\n" + USAGE + START_USAGE);
      return;
    }
    if(!gui.getLanChatServer().isRunning()){
      gui.setLanChatServer(new LanChatServer(gui));
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
