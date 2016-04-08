package lanchat.gui;

import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lanchat.client.Client;

public class ClientGUI extends Application{
  
  private BorderPane rootPane;
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

  @Override
  public void start(Stage primaryStage) throws Exception {
    rootPane = new BorderPane();
    
    Scene scene = new Scene(rootPane, 500, 500);
    scene.getStylesheets().add(getClass().getResource("/lanchat/gui/styles.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.setTitle("LanChat-Client");
    primaryStage.setMinWidth(500);
    primaryStage.setMinHeight(500);
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

    rootPane.setCenter(vBoxLoginForm);
    
    primaryStage.show();
    
    //chat view
    //these components are created here, however they are only displayed if startChatView is called
  }

  public void startChatView(){
    rootPane.setCenter(null);
    
    
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
    String ip = textFieldAddress.getText();
    String username = textFieldUsername.getText();
    
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
}