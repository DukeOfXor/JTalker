package lanchat.gui;

import java.net.Inet4Address;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    lanChatServer = new LanChatServer(gui);
    
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
        lanChatServer.shutdown();
        try {
          lanChatServer.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    
    labelAddress = new Label(Inet4Address.getLocalHost().getHostAddress() + ":" + LanChatServer.PORT);
    labelAddress.getStyleClass().add("title");
    
    wrapperTop = new StackPane();
    wrapperTop.getStyleClass().add("title-bar");
    wrapperTop.getStyleClass().add("status-red");
    wrapperTop.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent event) {
        if(lanChatServer.isRunning()){
          lanChatServer.shutdown();
        } else {
          lanChatServer = new LanChatServer(gui);
          lanChatServer.start();
        }
      }
    });
    
    wrapperTop.getChildren().add(labelAddress);
    rootPane.setTop(wrapperTop);
    
    primaryStage.show();
  }
  
  public void updateState(){
    if(lanChatServer.isRunning()){
      wrapperTop.getStyleClass().removeAll("status-red");
      wrapperTop.getStyleClass().add("status-green");
    } else {
      wrapperTop.getStyleClass().removeAll("status-green");
      wrapperTop.getStyleClass().add("status-red");
    }
  }

}
