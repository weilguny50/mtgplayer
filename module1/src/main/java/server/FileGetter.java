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

            String onlyFilename = removePathFromFile(i.toString());

            if (onlyFilename.equals(realFilename+"_front_.jpg")) {
                myList.set(0,i);//front side auf slot 0

            }
            if (onlyFilename.equals(realFilename+"_back_.jpg")) {
                myList.set(1,i);//back side auf slot 1
            }
        }

        return myList;
    }
    public static String removePathFromFile(String fname){
        int pos = fname.lastIndexOf(File.separator);
        if(pos > -1)
            return fname.substring(pos + 1);
        else
            return fname;
    }
}
