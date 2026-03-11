package jfx.makeDecklist;

import java.util.ArrayList;

public class DecklistCreator {
        public ArrayList<CardObject> create() {

        CardHandler ch = new CardHandler();

        ArrayList<CardObject> decklist = ch.makeDecklist();

        /*try {
            temporarySaveDeck(decklist);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        return decklist;
    }

    /*public static void temporarySaveDeck(ArrayList<CardObject> l) throws IOException {
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
    }*/
}
