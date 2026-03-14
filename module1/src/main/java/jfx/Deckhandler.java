package jfx;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import jfx.makeDecklist.CardObject;

import java.util.ArrayList;
import java.util.Collections;

public class Deckhandler {

    AnchorPane controller;

    ArrayList<String> library = new ArrayList<>();
    ArrayList<String> hand = new ArrayList<>();
    ArrayList<String> grave = new ArrayList<>();
    ArrayList<String> exile = new ArrayList<>();

    public Deckhandler(ArrayList<CardObject> decklist,AnchorPane n){
        controller = n;
        for (CardObject c : decklist){
            library.add(c.getSetName()+" Nr."+c.getSetNumber()+"_front_.jpg");
        }
        Collections.shuffle(library);
    }
    public String drawACard(){
        String card = library.getFirst();
        library.removeFirst();
        return card;
    }
    public void putCardInLibraryOnTOP(Node n){
        String[] split = n.getId().split("§");
        library.addFirst(split[1]);
        controller.getChildren().remove(n);
    }
    public void putCardInLibraryOnTBOTTOM(Node n){
        String[] split = n.getId().split("§");
        library.addLast(split[1]);
        controller.getChildren().remove(n);
    }
    public void shuffleLibrary(){
        Collections.shuffle(library);
    }
}



