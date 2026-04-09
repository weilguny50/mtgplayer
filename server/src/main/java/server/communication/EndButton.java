package server.communication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EndButton extends Application {
    public static byte stop = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Button bt = new Button();
        AnchorPane ap = new AnchorPane(bt);

        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(ap));
        primaryStage.setHeight(100);
        primaryStage.setWidth(100);
        primaryStage.show();



    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}

