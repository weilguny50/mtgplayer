package jfx;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection1 {

    public static Socket mainConnection() {

      String host = "localhost";

      int port = 5544;

      Socket server = null;

        try {
            Socket socket = new Socket(host, port);
            server=socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return server;
    }
}
