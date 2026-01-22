package jfx;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.F11;
import static jfx.Main.getStage;

public class Controller implements Initializable {
    @FXML
    public ImageView mtgcard;
    @FXML
    public Button newcardbutton;
    @FXML
    private AnchorPane controller;
    @FXML
    public ScrollPane myscrollpane;
    @FXML
    public Button newcounterbutton;

    DraggableMaker draggableMaker = new DraggableMaker();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        draggableMaker.makeDraggable(mtgcard);
    }

    public void newcardpress(MouseEvent e){//Ich kann auch checken, ob ich eine Taste dabei drücke wie strg shift oder alt, dafür MouseEvent anschauen mit StrgClick
            Image img = new Image(getClass().getResource("/mbm.jpg").toExternalForm());
            ImageView iv = new ImageView(img);
            iv.setFitWidth(200);
            iv.setFitHeight(280);
            iv.setPreserveRatio(true);
            iv.setLayoutX(e.getSceneX() - iv.getFitWidth() / 2);
            iv.setLayoutY(e.getSceneY() - iv.getFitHeight() / 2);
            controller.getChildren().add(iv);
            draggableMaker.makeDraggable(iv);//make draggable
            iv.setId("productImage_42");//id setzen
    }

    public void scrollkeypress(KeyEvent e){//Sinn: mit wasd scrollen zu können, damit man nicht immer komisch mit der Maus den Scrollbalken jagen muss
    switch(e.getText()){//KeyEvent get Text holt die gedrückte Taste, je nach Taste im H oder V Value rauf oder runter, H/V Value ist 0-1, wenns nicht ganz
        case "w": myscrollpane.setVvalue(myscrollpane.getVvalue()-0.05);//0.1 plus oder minus rechnen kann, weil es zu nah an 0 bzw. 1 dran ist, dann wird auf
        if(myscrollpane.getVvalue()<0.05){myscrollpane.setVvalue(0);}break;//0 bzw. 1 gesetzt
        case "a": myscrollpane.setHvalue(myscrollpane.getHvalue()-0.05);
        if(myscrollpane.getHvalue()<0.05){myscrollpane.setHvalue(0);}break;
        case "s": myscrollpane.setVvalue(myscrollpane.getVvalue()+0.05);
        if(myscrollpane.getVvalue()>0.95){myscrollpane.setVvalue(1);}break;
        case "d": myscrollpane.setHvalue(myscrollpane.getHvalue()+0.05);
        if(myscrollpane.getHvalue()>0.95){myscrollpane.setHvalue(1);}break;
        }
        if(e.getCode().equals(F11)){//F11 für Fullscreen
            Main.mystage.setFullScreen(true);//Stage ganz oben als Objekt vom Main über Methode abgespeichert.
        }}
    public  void newcounterbuttonpress(){//Button für Statusfeld erzeugung
        TextField txf = new TextField();//Es ist das Selbe was die Kartenbilder erzeugt.
        txf.setPrefWidth(200);      //Einfach ein TextField wallah
        txf.setPrefHeight(64);
        txf.setLayoutX(1590);
        txf.setLayoutY(1873);
        txf.setFont(new Font("System Italic", 24));
        controller.getChildren().add(txf);
        draggableMaker.makeDraggable(txf);
        txf.setId("gesetzteID");
    }


    public void cardmousereleaseclick(){}
    public void cardclicked(MouseEvent e){}
    public void cardmouseentered(){}
    public void cardmouseexited(){}
}