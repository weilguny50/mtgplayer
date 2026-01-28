package jfx;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;

public class DraggableMaker {

    private double mouseAnchorX;
    private double mouseAnchorY;
    double viewOrder = 9.0;     //double 9 bis 0, niedrigerer Wert = höhere Priorität.
    ScrollPane scrollpane;
    public DraggableMaker(ScrollPane myscrollpane) {
        this.scrollpane=myscrollpane;
    }

    public void makeDraggable(Node node){

        node.setOnMousePressed(mouseEvent -> {
            node.setViewOrder(viewOrder=viewOrder-0.00001);//Jedes Mal, wenn auf eine Karte geklickt wird, wird von der viewOrder Variable
            mouseAnchorX = mouseEvent.getX();              // 0.00001 abgezogen, somit kann man insgesamt 999999 Mal auf eine Karte klicken bis es abschmiert.
            mouseAnchorY = mouseEvent.getY();
            if(mouseEvent.getButton() == MouseButton.SECONDARY){//Auf Rechtsclick auf Karte checken
                if(node.getRotate()==0){  //wenn nicht getappt ist, dann tappen,
                    node.setRotate(270);
                } else if (node.getRotate()==270){ //wenn getappt ist, dann untappen
                    node.setRotate(0);
                }
            }
        });

        node.setOnMouseDragged(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){//check ob linke Maustaste gedrückt wurde, nur dann gehts
                    node.relocate(mouseEvent.getX() - mouseAnchorX + node.getLayoutX(), mouseEvent.getY() - mouseAnchorY + node.getLayoutY());
            }
        });
    }
}