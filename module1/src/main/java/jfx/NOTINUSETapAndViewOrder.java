package jfx;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;

public class NOTINUSETapAndViewOrder {

    private double nodeOffsetX;
    private double nodeOffsetY;
    double viewOrder = 9.0;     //double 9 bis 0, niedrigerer Wert = höhere Priorität.

    public NOTINUSETapAndViewOrder(ScrollPane myscrollpane) {
    }

    public void makeDraggable(Node node){

        node.setOnMousePressed(mouseEvent -> {
            System.out.println(node.getId());
            //Jedes Mal, wenn auf eine Karte geklickt wird, wird von der viewOrder Variable
            // 0.00001 abgezogen, somit kann man insgesamt 999999 Mal auf eine Karte klicken bis es abschmiert.
            node.setViewOrder(viewOrder = viewOrder - 0.00001);

            if (mouseEvent.getButton() == MouseButton.SECONDARY) {//Auf Rechtsclick auf Karte checken
                if (node.getRotate() == 0) {  //wenn nicht getappt ist, dann tappen,
                    node.setRotate(270);
                } else if (node.getRotate()==270){ //wenn getappt ist, dann untappen
                    node.setRotate(0);
                }
            }
        });

        node.setOnMouseDragged(mouseEvent -> {

        });

        node.setOnMouseReleased(mouseEvent -> {
            System.out.println("mulm");
        });
    }
}