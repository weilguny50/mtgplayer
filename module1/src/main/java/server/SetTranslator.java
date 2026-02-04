package server;

import java.io.*;
import java.util.*;

public class SetTranslator {
    Map<String, String> map = new HashMap<>();

    public Map createMap() {

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("textfiles/fullname.txt");
             InputStreamReader isr = new InputStreamReader(is, "UTF-8");
             BufferedReader br = new BufferedReader(isr);
             InputStream is1 = getClass().getClassLoader().getResourceAsStream("textfiles/shortname.txt");
             InputStreamReader isr1 = new InputStreamReader(is1, "UTF-8");
             BufferedReader br1 = new BufferedReader(isr1)) {

            String one = null;
            String two = null;
            do {
                one = br1.readLine();
                two = br.readLine();
                if (one != null && two != null) {
                    map.put(one, two);
                }
            } while (one != null && two != null);

            //HIER STEHEN GEBLIEBEN. EINE MAP ERSTELLEN AUS VOLLER SET NAME UND KURZNAME VON DEN ZWEI TEXTFILES, DAMIT ICH SAGEN KANN IM CARDHANDLER,
            //ich bekomme von archidekt da in den strings das set kürzel, und dann key value von der map den dazugehörigen vollen namen rausfinden.

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
}
