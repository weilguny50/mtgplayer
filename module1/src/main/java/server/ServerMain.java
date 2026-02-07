package server;

import java.util.List;

public class ServerMain {
    static void main() {

        CardHandler ch = new CardHandler();

        List decklist = ch.makeDecklist();  //Es spuckt jetzt noch eine Liste mit falschen Karten aus, das liegt daran,
                                            //Dass die gespeicherten Bilder hinten so lange random Zeichen haben.
                                            //Dies versuche ich zu beheben, indem ich das Donwload Script feat. matthias von Java
                                            //ändere, und beim download schon beim namen diese random Zeichenfolge weglasse.

        System.out.printf("mulm");





    }
}
