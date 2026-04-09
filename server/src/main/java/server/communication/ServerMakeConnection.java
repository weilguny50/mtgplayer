package server.communication;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMakeConnection {

    static int port = 5544;
    static ServerSocket serverSocket;

    public static Socket makeConnection(){

        try  {//Hier stand die initialisierung des Sockets in den try() Klammern, und der wurde deshalb beim Verlassen der geschwungenen Klammern geschlossen.

            Socket clientSocket = serverSocket.accept();

        return clientSocket;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static{//Konstruktor für static Klassen, habe ich gemacht, weil sonst für jedes mal ausführen ein neuer Server Socket gemacht wurde -> falsch
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
