package server;

import java.awt.*;
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

                for (int j = 0; j < Integer.valueOf(String.valueOf(cardcount)); j++) {//Karten so oft hinzufügen wie sie in Deckliste vorkommen, mit cardcount Variable
                    refinedDecklist.add(newCard);
                }

            if (cardcount.length() == 1) {     //die Cardcount Varaible wieder auf null setzen
                cardcount.deleteCharAt(0);
            } else if (cardcount.length() == 2) {
                cardcount.deleteCharAt(0);
                cardcount.deleteCharAt(1);
            }
        }

        return new ImagePackage(refinedDecklist).createImagePackage();


                                //Dann muss ich, wenn ich das habe, eine uploaddecklist klasse machen beim gui, die über http, in meinem testfall localhost,
                                //die Deckliste an den Server schickt. Mit socket und new thread und so.
                                //die dual faced cards machen mit im karten objekt einfach zwei neue namen und nummer, und das genauso machen wie hier auch
                                //mit shortname und fullname, chatgpt nach 2 listen fragen, eine mit vorderseite und eine mit rückseite von allen karten die
                                //existieren, problem ist, dass ich nicht die namen da stehen haben will, sondern die set kürzel und nummer, in beiden textdokumenten
                                //dann habe ich ein kartenobjekt, was auch ne zweite Karte gespeichert haben kann.
    }
}
