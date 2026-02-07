package server;

import java.io.File;
import java.util.stream.Stream;

public class FileGetter {
static String directory = "B:/Downloads/Pictures/mtg/scryfall_cards";
    public static File returnFile(String set,String number){

        File baseDirectory = new File(directory);

        for (File i : baseDirectory.listFiles()){

            if(i.toString().contains(set)&&i.toString().contains(number)){
                File y = i;
                if(y==null){
                    System.out.printf("Karte mit Set Namen und Nummer %s %s wurde nicht gefunden.",set,number);
                }
                return y;
            }
        }

        return null;
    }
}
