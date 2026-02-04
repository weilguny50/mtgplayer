package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardHandler {

    List<String> decklist = new ArrayList<>();
    List<CardObject> refinedDecklist = new ArrayList<>();
    SetTranslator t = new SetTranslator();

    public List makeDecklist() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("textfiles/decklist.txt");
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             BufferedReader br = new BufferedReader(isr)) {

            decklist = br.lines()
                    .filter(line -> !line.isBlank())
                    .toList();

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder cardcount = new StringBuilder();
        for (String i : decklist) {

            if (String.valueOf(i.charAt(1)).equals("x")) {
                cardcount.append(i.charAt(0));

            } else if (String.valueOf(i.charAt(2)).equals("x")) {
                cardcount.append(i.charAt(0));
                cardcount.append(i.charAt(1));
            }

            String[] split = i.split(" \\(");

            String[] setAndNumber = split[1].split("\\)");

            Map<String, String> setNameMap = t.createMap();//map von setname zu setkurzname

            CardObject newCard = new CardObject(setAndNumber[0], setAndNumber[1]);

            for (Map.Entry<String, String> c : setNameMap.entrySet()){
                if(c.getKey().equalsIgnoreCase(newCard.getSetName())){
                    newCard.setSetName(c.getValue());
                    break;
                }
            }

                for (int j = 0; j < Integer.valueOf(String.valueOf(cardcount)); j++) {
                    refinedDecklist.add(newCard);
                }

            if (cardcount.length() == 1) {     //den cardcount wieder auf null setzen
                cardcount.deleteCharAt(0);
            } else if (cardcount.length() == 2) {
                cardcount.deleteCharAt(0);
                cardcount.deleteCharAt(1);
            }
        }
        return refinedDecklist;     //ich muss noch alles kommentieren was der code hier macht,
                                //dieser Code returned die Decklist. als nächstes brauch ich sowas wie eine ImageDataHolder Klasse.
                                //Die Klasse callt das hier, also nicht aus dem Main, und holt sich dann als File rein die Bilder ausm Ordner.
                                //und behält diese Files dann in sich, um sie im UI an das Deck übergeben zu können.
                                //Die richtigen Karten suchen mit unter allen Files die im ordner sind, welche haben im Namen den Setnamen UND die Nummer.
                                //Dann muss ich, wenn ich das habe, eine uploaddecklist klasse machen beim gui, die über http, in meinem testfall localhost,
                                //die Deckliste an den Server schickt. Mit socket und new thread und so.
    }
}
