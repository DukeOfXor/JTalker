package org.dukeofxor.jtalker.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

import org.dukeofxor.jtalker.client.Client;

import javafx.application.Application;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientGUI extends Application{
  
  private BorderPane rootPaneLogin;
  private static final Pattern IP_PATTERN = Pattern.compile(
      "^\\s*(.*?):(\\d+)\\s*$");
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
  private static final String IP_PLACEHOLDER = "192.168.1.102:2347";
  private static final String USERNAME_PLACEHOLDER = "CoolUser99";
  private Label labelAddress;
  private TextField textFieldAddress;
  private Label labelUsername;
  private TextField textFieldUsername;
  private Client client;
  private Label labelError;
  private Button buttonConnect;
  private StackPane stackPaneButtonWrapper;
  private VBox vBoxLoginForm;
  private TextArea textAreaChatOutput;
  private TextField textFieldChatInput;
  private BorderPane borderPaneMainChatWrapper;
  private Label labelServerIp;
  private Scene scene;
  private BorderPane rootPaneChat;
  private BorderPane borderPaneLeftSideWrapper;
  private ListView<String> listViewClients;
  private ListProperty<String> listPropertyClients;

  @Override
  public void start(Stage primaryStage) throws Exception {
    listPropertyClients = new SimpleListProperty<>();
    
    rootPaneLogin = new BorderPane();
    rootPaneChat = new BorderPane();
    
    scene = new Scene(rootPaneLogin, 600, 500);
    scene.getStylesheets().add(getClass().getResource("/org/dukeofxor/jtalker/gui/styles.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.setTitle("JTalker-Client");
    primaryStage.setMinWidth(600);
    primaryStage.setMinHeight(500);
    primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/org/dukeofxor/jtalker/gui/icon.png")));
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

      @Override
      public void handle(WindowEvent event) {
        if(client != null){
          client.logout();
          client.disconnect();
          try {
            client.join();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
    
    labelAddress = new Label("Address");
    labelAddress.getStyleClass().add("label-login");
    
    textFieldAddress = new TextField();
    textFieldAddress.getStyleClass().add("text-field-login");
    textFieldAddress.setPromptText(IP_PLACEHOLDER);
    textFieldAddress.setOnKeyPressed(new EventHandler<KeyEvent>() {

      @Override
      public void handle(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)){
          validateInputs();
        }
      }
    });
    
    //login form
    labelUsername = new Label("Username");
    labelUsername.getStyleClass().add("label-login");
    
    textFieldUsername = new TextField();
    textFieldUsername.getStyleClass().add("text-field-login");
    textFieldUsername.setPromptText(USERNAME_PLACEHOLDER);
    textFieldUsername.setOnKeyPressed(new EventHandler<KeyEvent>() {

      @Override
      public void handle(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)){
          validateInputs();
        }
      }
    });
    
    buttonConnect = new Button("Connect");
    buttonConnect.getStyleClass().add("button-login");
    buttonConnect.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent event) {
        validateInputs();
      }
    });
    
    stackPaneButtonWrapper = new StackPane(buttonConnect);
    stackPaneButtonWrapper.getStyleClass().add("stack-pane-login");
    
    labelError = new Label();
    labelError.getStyleClass().add("label-login-error");
    
    vBoxLoginForm = new VBox();
    vBoxLoginForm.getStyleClass().add("vbox-login");
    
    vBoxLoginForm.getChildren().addAll(labelAddress, textFieldAddress,labelUsername, textFieldUsername, stackPaneButtonWrapper, labelError);
    vBoxLoginForm.setAlignment(Pos.CENTER);

    rootPaneLogin.setCenter(vBoxLoginForm);
    
    primaryStage.show();
    
    textAreaChatOutput = new TextArea();
    textAreaChatOutput.setEditable(false);
    textAreaChatOutput.setWrapText(true);
    textAreaChatOutput.getStyleClass().add("text-area-chat-output");
    
    ContextMenu contextMenu = new ContextMenu();
    textAreaChatOutput.setContextMenu(contextMenu);
    MenuItem menuItemClear = new MenuItem("clear");
    contextMenu.getItems().addAll(menuItemClear);
    menuItemClear.setOnAction(new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			textAreaChatOutput.clear();
		}
    	
    });
    
    textFieldChatInput = new TextField();
    textFieldChatInput.getStyleClass().add("text-field-chat-input");
    textFieldChatInput.setOnKeyPressed(new EventHandler<KeyEvent>() {

      @Override
      public void handle(KeyEvent event) {
         if(event.getCode().equals(KeyCode.ENTER)){
           sendMessage();
         }
      }
    });
    
    borderPaneMainChatWrapper = new BorderPane();
    borderPaneMainChatWrapper.setCenter(textAreaChatOutput);
    borderPaneMainChatWrapper.setBottom(textFieldChatInput);
    borderPaneMainChatWrapper.getStyleClass().add("border-pane-chat-main");
    
    labelServerIp = new Label();
    labelServerIp.getStyleClass().add("label-chat-server-ip");
    
    StackPane stackPaneLabelIpWrapper = new StackPane(labelServerIp);
    stackPaneLabelIpWrapper.getStyleClass().add("stack-pane-label-ip-wrapper");
    
    listViewClients = new ListView<String>();
    listViewClients.itemsProperty().bind(listPropertyClients);    
    
    borderPaneLeftSideWrapper = new BorderPane();
    borderPaneLeftSideWrapper.setPrefWidth(150);
    borderPaneLeftSideWrapper.setTop(stackPaneLabelIpWrapper);
    borderPaneLeftSideWrapper.setCenter(listViewClients);
    borderPaneLeftSideWrapper.getStyleClass().add("border-pane-chat-left-side-wrapper");
    
    rootPaneChat.setCenter(borderPaneMainChatWrapper);
    rootPaneChat.setLeft(borderPaneLeftSideWrapper);
  }

  public void startChatView(){
    labelServerIp.setText(client.getServerIp());
    scene.setRoot(rootPaneChat);
  }
  
  public void startLoginView(){
    scene.setRoot(rootPaneLogin);
  }
  
  public void setClientList(ArrayList<String> clientList){
    clientList.sort(new Comparator<String>() {

      @Override
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    });
    
    listPropertyClients.set(FXCollections.observableArrayList(clientList));
  }
  
  public void displayMessage(String username, String message){
    textAreaChatOutput.appendText("[" + username + "] ");
    textAreaChatOutput.appendText(message);
    textAreaChatOutput.appendText("\n");
  }
  
  protected void connect(String ip, String username) {
    String[] split = ip.split(":");
    String address = split[0];
    int port = 8954;
    if(split.length == 2){
      Integer.parseInt(split[1]);
    }
    
    client = new Client(address, port, username, this);
    client.start();
  }

  protected boolean isValidUsername(String username) {
    return USERNAME_PATTERN.matcher(username).matches();
  }

  protected boolean isValidIp(String ip) {
    return IP_PATTERN.matcher(ip).matches();
  }

  private void validateInputs() {
    String ip = textFieldAddress.getText().trim();
    String username = textFieldUsername.getText().trim();
    
    setLoginErrorText("");
    
    if(isValidIp(ip)){
      textFieldAddress.getStyleClass().removeAll("text-field-login-invalid");
    } else {
      textFieldAddress.getStyleClass().add("text-field-login-invalid");
    }
    
    if(isValidUsername(username)){
      textFieldUsername.getStyleClass().removeAll("text-field-login-invalid");
    } else {
      textFieldUsername.getStyleClass().add("text-field-login-invalid");
    }
    
    if(isValidIp(ip) && isValidUsername(username)){
      connect(ip, username);
    }
  }
  
  public void setLoginErrorText(String errorMessage){
    labelError.setText(errorMessage);
  }

  private void sendMessage() {
    String message = textFieldChatInput.getText();
    if(message.isEmpty()){
      return;
    }
    if(client == null){
      return;
    }
    client.sendTextMessage(textFieldChatInput.getText());
    textFieldChatInput.clear();
  }
}