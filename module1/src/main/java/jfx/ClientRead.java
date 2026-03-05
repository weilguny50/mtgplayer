package jfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientRead implements Runnable {

    Socket server;
    Controller contr;

    public ClientRead(Socket server,Controller c) {
        this.server = server;
        this.contr = c;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()))) {

            String line;

            while ((line = in.readLine()) != null) {

                System.out.println(line);

                contr.adjustNodesFromServer(line);
            }


        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
