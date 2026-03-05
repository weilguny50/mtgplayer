package server.communication;

import java.net.Socket;
import java.util.ArrayList;

import static server.communication.ServerMakeConnection.makeConnection;

public class ServerMain {

    static ArrayList<Socket> clients = new ArrayList<>();

    public static void main(String[] args) {

        do {
            Socket returnedClient = makeConnection();
            clients.add(returnedClient);
            System.out.println("Anzahl Clients: "+clients.size());
        } while (clients.size() < 4);

        ArrayList<Socket> listForClient1 = new ArrayList<>();//4Mal eine neue Liste machen mit nur den Clients die den jeweils betroffenen
        listForClient1.add(clients.get(1));                                                                             //Client interessieren
        listForClient1.add(clients.get(2));
        listForClient1.add(clients.get(3));

        ArrayList<Socket> listForClient2 = new ArrayList<>();
        listForClient2.add(clients.get(0));
        listForClient2.add(clients.get(2));
        listForClient2.add(clients.get(3));

        ArrayList<Socket> listForClient3 = new ArrayList<>();
        listForClient3.add(clients.get(1));
        listForClient3.add(clients.get(0));
        listForClient3.add(clients.get(3));

        ArrayList<Socket> listForClient4 = new ArrayList<>();
        listForClient4.add(clients.get(1));
        listForClient4.add(clients.get(2));
        listForClient4.add(clients.get(0));

        ServerReadWrite forClient1 = new ServerReadWrite(listForClient1,clients.get(0));
        ServerReadWrite forClient2 = new ServerReadWrite(listForClient2,clients.get(1));
        ServerReadWrite forClient3 = new ServerReadWrite(listForClient3,clients.get(2));
        ServerReadWrite forClient4 = new ServerReadWrite(listForClient4,clients.get(3));

        Thread thread1 = new Thread(forClient1,"Client1");
        Thread thread2 = new Thread(forClient2,"Client2");
        Thread thread3 = new Thread(forClient3,"Client3");
        Thread thread4 = new Thread(forClient4,"Client4");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        while(true){

        }
    }
}
