package jfx;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;

public class DraggableMaker {

    private double nodeOffsetX;
    private double nodeOffsetY;
    double viewOrder = 9.0;     //double 9 bis 0, niedrigerer Wert = höhere Priorität.

    public DraggableMaker(ScrollPane myscrollpane) {
    }

    public void makeDraggable(Node node){

        node.setOnMousePressed(mouseEvent -> {
            System.out.println(node.getId());
            //Jedes Mal, wenn auf eine Karte geklickt wird, wird von der viewOrder Variable
            // 0.00001 abgezogen, somit kann man insgesamt 999999 Mal auf eine Karte klicken bis es abschmiert.
            node.setViewOrder(viewOrder = viewOrder - 0.00001);
            /*
             newPosition = nodeOldPos + (mouseNewPos - mouseOrigin)
              = (nodeOldPos - mouseOrigin) + mouseNewPos  
             nodeOffset = (nodeOldPos - mouseOrigin)
             => newPosition = nodeOffset + mouseNewPos
             */
            nodeOffsetX = node.getLayoutX() - mouseEvent.getScreenX();
            nodeOffsetY = node.getLayoutY() - mouseEvent.getScreenY();
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {//Auf Rechtsclick auf Karte checken
                if (node.getRotate() == 0) {  //wenn nicht getappt ist, dann tappen,
                    node.setRotate(270);
                } else if (node.getRotate()==270){ //wenn getappt ist, dann untappen
                    node.setRotate(0);
                }
            }
        });

        node.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {//check ob linke Maustaste gedrückt wurde, nur dann gehts
                // use relocate instead of setLayoutXY as recommended in Javadoc
                // newPosition = nodeOffset + mouseNewPos
                node.relocate(nodeOffsetX + mouseEvent.getScreenX(), nodeOffsetY + mouseEvent.getScreenY());
            }
        });
    }
}