package lanchat.gui;

import java.net.Inet4Address;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lanchat.server.LanChatServer;

public class ServerGUI extends Application{

  private LanChatServer lanChatServer;
  private final ServerGUI gui = this;
  private BorderPane rootPane;
  private Label labelAddress;
  private StackPane wrapperTop;
  private TextArea textAreaLog;
  private TextField textFieldInput;
  private CommandHandler cmdHandler;
  private Stage primaryStage;
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    setLanChatServer(new LanChatServer(gui));
    cmdHandler = new CommandHandler(gui);
    
    rootPane = new BorderPane();
    
    Scene scene = new Scene(rootPane, 500, 300);
    scene.getStylesheets().add(getClass().getResource("/lanchat/gui/styles.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.setTitle("LanChat-Server");
    primaryStage.setMinWidth(1000);
    primaryStage.setMinHeight(600);
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

      @Override
      public void handle(WindowEvent event) {
        getLanChatServer().shutdown();
        try {
          getLanChatServer().join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    textFieldInput = new TextField();
    textFieldInput.getStyleClass().add("text-field-input");
    textFieldInput.setOnKeyPressed(new EventHandler<KeyEvent>() {

      @Override
      public void handle(KeyEvent event) {
         if(event.getCode().equals(KeyCode.ENTER)){
           cmdHandler.handleCommand(textFieldInput.getText());
           textFieldInput.setText("");
         }
      }
    });
    
    textAreaLog = new TextArea();
    textAreaLog.setEditable(false);
    textAreaLog.setWrapText(true);
    textAreaLog.getStyleClass().add("text-area-log");
    
    labelAddress = new Label(Inet4Address.getLocalHost().getHostAddress() + ":" + LanChatServer.PORT);
    labelAddress.getStyleClass().add("title");
    
    wrapperTop = new StackPane();
    wrapperTop.getStyleClass().add("title-bar");
    wrapperTop.getStyleClass().add("status-red");
    
    rootPane.setBottom(textFieldInput);
    rootPane.setCenter(textAreaLog);
    wrapperTop.getChildren().add(labelAddress);
    rootPane.setTop(wrapperTop);
    
    primaryStage.show();
  }
  
  public void updateState(){
    if(getLanChatServer().isRunning()){
      wrapperTop.getStyleClass().removeAll("status-red");
      wrapperTop.getStyleClass().add("status-green");
    } else {
      wrapperTop.getStyleClass().removeAll("status-green");
      wrapperTop.getStyleClass().add("status-red");
    }
  }
  
  public void displayMessage(String prefix, String message){
    textAreaLog.appendText("[" + prefix + "] ");
    textAreaLog.appendText(message);
    textAreaLog.appendText("\n");
  }

  public void displayMessage(String message){
    textAreaLog.appendText("[" + "Server" + "] ");
    textAreaLog.appendText(message);
    textAreaLog.appendText("\n");
  }

  public LanChatServer getLanChatServer() {
    return lanChatServer;
  }

  public void setLanChatServer(LanChatServer lanChatServer) {
    this.lanChatServer = lanChatServer;
  }

  public void clearConsole() {
    textAreaLog.setText("");
  }
  
  public void close(){
    primaryStage.close();
  }
}
