package server;

import javax.smartcardio.Card;
import java.util.List;

public class ServerMain {
    static void main() {

        CardHandler ch = new CardHandler();

        List<CardObject> decklist = ch.makeDecklist();

        System.out.printf("mulm");





    }
}
