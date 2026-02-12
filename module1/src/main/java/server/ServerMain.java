package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static server.FileGetter.removePathFromFile;

public class ServerMain {
    static void main() {

        CardHandler ch = new CardHandler();

        ArrayList<CardObject> decklist = ch.makeDecklist();

        System.out.printf("mulm");

        try {
            temporarySaveDeck(decklist);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void temporarySaveDeck(ArrayList<CardObject> l) throws IOException {
        ArrayList<File> fileList = new ArrayList<>();//Diese Methoe exisitert nur, um ein Deck in den Ordner ClientCards abzuspeichern
                                                    //damit ich das herzeigen kann, und nicht 110000 Bilder auf Git laden muss.
        for (CardObject c : l) {
            if(c.getFrontFace()!=null){
                fileList.add(c.getFrontFace());
            }
            if(c.getBackFace()!=null){
                fileList.add(c.getBackFace());
            }
        }

        for (File f:fileList){

            Path original = f.toPath();
            String onlyFilename = removePathFromFile(f.toString());
            Path copied = Paths.get("ClientCards", onlyFilename);
         Files.copy(original,copied,StandardCopyOption.REPLACE_EXISTING);//Das Files.copy ist der ärgste Mist.
        }
    }
}
