package org.dukeofxor.jtalker.gui;

import javafx.application.Platform;


public class CommandHandlerClient {
	
	public static final String TO_MANY_ARGUMENTS = "Too many arguments for command ";
	public static final String MISSING_ARGUMENT = "Missing arguments for command ";
	public static final String USAGE = "Usage: ";
	public static final String WHISPER_USAGE = "whisper username message";
	
	private String command;
	private ClientGUI gui;
	
	public CommandHandlerClient(ClientGUI gui){
		setGui(getGui());
	}
	
	public void handleCommand(String command){
		command = command.substring(1);
		command = command.trim();
		String[] cmd = command.split(" ");
		if(cmd[0].equals("whisper")){
			whisper(cmd);
			return;
		}
	}
	
	
	private void whisper(String[] cmd) {
	    if(cmd.length < 3){
	      displayGuiMessage(MISSING_ARGUMENT + cmd[0] + "\n" + USAGE + WHISPER_USAGE);
	    }
	    if(!gui.getUsernameList().contains(getUserNameFromWhisperCommand(cmd))){
	    	displayGuiMessage("The client you whispered to isn't online");
	    }
	    gui.getClient().sendWhisperMessage(getUserNameFromWhisperCommand(cmd), getMessageFromWhisperCommand(cmd));
	}
	
	private void displayGuiMessage(String message){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				gui.displayMessage(message);
			}
		});
	}


	public String getMessageFromWhisperCommand(String[] whisperCommand){
		String message = "";
		for (int counter = 2; counter < whisperCommand.length; counter++) {
			message +=  " " + whisperCommand[counter];
		}
		return message;
	}

	private String getUserNameFromWhisperCommand(String[] cmd) {
		return cmd[1];
	}

	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public ClientGUI getGui() {
		return gui;
	}
	public void setGui(ClientGUI gui) {
		this.gui = gui;
	}
}
