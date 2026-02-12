package server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePackage {
    List<CardObject> decklist;
    ArrayList<CardObject> deck = new ArrayList<>();
    FileGetter myFileGetter = new FileGetter();
    public ImagePackage(List<CardObject> l){
        this.decklist = l;
    }

    public ArrayList<CardObject> createImagePackage(){

        for (CardObject i :decklist){

            ArrayList<File> list = myFileGetter.returnFullCard(i.getSetName(), i.getSetNumber());
            CardObject co = new CardObject();
            co.setFrontAndBack(list.get(0), list.get(1));
            deck.add(co);


        }
        return deck;
    }
}
