package jfx;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ImageView mtgcard;
    @FXML
    Button newcardbutton;
    @FXML
    private AnchorPane controller;
    @FXML
    public ScrollPane myscrollpane;

    DraggableMaker draggableMaker = new DraggableMaker();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        draggableMaker.makeDraggable(mtgcard);
    }

    public void newcardpress(MouseEvent e){//Ich kann auch checken, ob ich eine Taste dabei drücke wie strg shift oder alt, dafür MouseEvent anschauen mit StrgClick
            Image img = new Image(getClass().getResource("/mbm.jpg").toExternalForm());
            ImageView iv = new ImageView(img);
            iv.setFitWidth(120);
            iv.setFitHeight(168);
            iv.setPreserveRatio(true);
            iv.setLayoutX(e.getSceneX() - iv.getFitWidth() / 2);
            iv.setLayoutY(e.getSceneY() - iv.getFitHeight() / 2);
            controller.getChildren().add(iv);
            draggableMaker.makeDraggable(iv);//make draggable
            iv.setId("productImage_42");//id setzen
    }
    public void scrollkeypress(KeyEvent e){//Sinn: mit wasd scrollen zu können, damit man nicht immer komisch mit der Maus den Scrollbalken jagen muss
    switch(e.getText()){//KeyEvent get Text holt die gedrückte Taste, je nach Taste im H oder V Value rauf oder runter, H/V Value ist 0-1, wenns nicht ganz
        case "w": myscrollpane.setVvalue(myscrollpane.getVvalue()-0.1);//0.1 plus oder minus rechnen kann, weil es zu nah an 0 bzw. 1 dran ist, dann wird auf
        if(myscrollpane.getVvalue()<0.1){myscrollpane.setVvalue(0);}break;//0 bzw. 1 gesetzt
        case "a": myscrollpane.setHvalue(myscrollpane.getHvalue()-0.1);
        if(myscrollpane.getHvalue()<0.1){myscrollpane.setHvalue(0);}break;
        case "s": myscrollpane.setVvalue(myscrollpane.getVvalue()+0.1);
        if(myscrollpane.getVvalue()>0.9){myscrollpane.setVvalue(1);}break;
        case "d": myscrollpane.setHvalue(myscrollpane.getHvalue()+0.1);
        if(myscrollpane.getHvalue()>0.9){myscrollpane.setHvalue(1);}break;
    }}
    public void cardmousereleaseclick(){}
    public void cardclicked(MouseEvent e){}
    public void cardmouseentered(){}
    public void cardmouseexited(){}
}