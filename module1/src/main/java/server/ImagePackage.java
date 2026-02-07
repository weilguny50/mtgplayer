package server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePackage {
    List<CardObject> decklist;
    List<File> deck = new ArrayList<>();

    public ImagePackage(List l){
        this.decklist = l;
    }

    public List createImagePackage(){

        for (CardObject i :decklist){
            deck.add(FileGetter.returnFile(i.getSetName(),i.getSetNumber()));
        }
        return deck;
    }
}
