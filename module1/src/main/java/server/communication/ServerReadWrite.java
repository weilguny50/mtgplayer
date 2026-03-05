package server.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerReadWrite implements Runnable{

    ArrayList<Socket> clientlist;
    Socket ownClient;
    public ServerReadWrite(ArrayList<Socket> i,Socket ownClient) {
        clientlist = i;//Constructor
        this.ownClient=ownClient;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ownClient.getInputStream()));
             PrintWriter out0 = new PrintWriter(clientlist.get(0).getOutputStream(), true);
             PrintWriter out1 = new PrintWriter(clientlist.get(1).getOutputStream(), true);
             PrintWriter out2 = new PrintWriter(clientlist.get(2).getOutputStream(), true)) {

            String line;

            while ((line = in.readLine()) != null) {
                out0.println(line);
                out1.println(line);
                out2.println(line);
                System.out.println(line);
            }


        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}