package lanchat.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LauncherGUI extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		SplitPane rootPane = new SplitPane();

	    Scene scene = new Scene(rootPane, 500, 300);
	    scene.getStylesheets().add(getClass().getResource("/lanchat/gui/styles.css").toExternalForm());
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("LanChat");
	    primaryStage.setMinWidth(500);
	    primaryStage.setMinHeight(300);
	    
        Label clientLabel = new Label("Client");
        clientLabel.getStyleClass().add("title");
        Label serverLabel = new Label("Server");
        serverLabel.getStyleClass().add("title");
	    
	    BorderPane leftPane = new BorderPane();
	    leftPane.getStyleClass().add("border-pane");
	    leftPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

        @Override
          public void handle(MouseEvent event) {
            //open Client GUI
          System.out.println("client");
          }
        });
	    leftPane.setOnMouseEntered(new EventHandler<MouseEvent>() {

          @Override
          public void handle(MouseEvent event) {
            leftPane.getStyleClass().add("highlight");
            clientLabel.getStyleClass().add("highlight");
          }
        });
	    leftPane.setOnMouseExited(new EventHandler<MouseEvent>() {

          @Override
          public void handle(MouseEvent event) {
            leftPane.getStyleClass().removeAll("highlight");
            clientLabel.getStyleClass().removeAll("highlight");
          }
	    });
        
        BorderPane rightPane = new BorderPane();
        rightPane.getStyleClass().add("border-pane");
        rightPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

          @Override
          public void handle(MouseEvent event) {
            ServerGUI serverGUI = new ServerGUI();
            try {
              serverGUI.start(new Stage());
            } catch (Exception e) {
              e.printStackTrace();
            };
            primaryStage.close();
          }
        });
        rightPane.setOnMouseEntered(new EventHandler<MouseEvent>() {

          @Override
          public void handle(MouseEvent event) {
            rightPane.getStyleClass().add("highlight");
            serverLabel.getStyleClass().add("highlight");
          }
        });
        rightPane.setOnMouseExited(new EventHandler<MouseEvent>() {

          @Override
          public void handle(MouseEvent event) {
            rightPane.getStyleClass().removeAll("highlight");
            serverLabel.getStyleClass().removeAll("highlight");
          }
        });
        
        rootPane.getItems().add(leftPane);
        rootPane.getItems().add(rightPane);
        leftPane.setCenter(clientLabel);
        rightPane.setCenter(serverLabel);
	    

	    primaryStage.show();
	  }
	
	  public static void main(String[] args) {
	    launch(args);
	  }
}
