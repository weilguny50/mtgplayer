package server;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileGetter {
     String directory = System.getProperty("user.dir")+"/scryfall_cards";


    public ArrayList<File> returnFullCard(String set, String number) {

        ArrayList<File> myList = new ArrayList<>(2);
        myList.add(0,null);
        myList.add(1,null);
        File baseDirectory = new File(directory);

        set=set.replaceAll("[\\\\/:*?\"<>|]", "_");

        String realFilename = set+" Nr."+number;

        for (File i : baseDirectory.listFiles()) {

            if (i.toString().contains(realFilename+"_front_")) {
                myList.set(0,i);//front side auf slot 0
            }
            if (i.toString().contains(realFilename+"_back_")) {
                myList.set(1,i);//back side auf slot 1
            }
        }
        if(myList.isEmpty()) {
            System.out.printf("Karte mit Set Namen und Nummer %s %s wurde nicht gefunden.", set, number);
        }
        return myList;
    }
}
