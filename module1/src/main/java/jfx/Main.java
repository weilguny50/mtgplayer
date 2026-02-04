package jfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/*
<?import javafx.scene.image.Image?>
<Image url="/mbm.jpg"/>
   </ImageView>
 */
public class Main extends Application {
    static Stage mystage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/sample4.fxml"));
        primaryStage.setTitle("mtg player");
        primaryStage.setScene(new Scene(root));
        primaryStage.setHeight(500);
        primaryStage.setWidth(500);
        primaryStage.show();
        mystage = primaryStage;
        primaryStage.setFullScreenExitHint("F11 for Fullscreen  Esc to quit Fullscreen\nw-a-s-d to move around");
    }

    public static void main(String[] args) {

        launch(args);}
    public static Stage getStage(){return mystage;}
}
