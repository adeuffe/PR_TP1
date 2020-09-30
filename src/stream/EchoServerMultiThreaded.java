/***
 * EchoServerMultiThreaded
 * TCP Server multi-threaded
 * Date: 09/23/2020
 * Authors:
 * - DUBOIS-TERMOZ Loïc
 * - DUFFOUR Alexandre
 */

package stream;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class EchoServerMultiThreaded {

    private static final List<Socket> clientsSockets = new ArrayList<>();
    private static final List<String> historical = new ArrayList<>();

    public static synchronized List<String> getHistorical() {
        return historical;
    }

    public static synchronized void addHistoricalMessage(String message) {
        getHistorical().add(message);
    }

    public static synchronized List<Socket> getClientsSockets() {
        return clientsSockets;
    }

    private static void addClientSocket(Socket clientSocket) {
        getClientsSockets().add(clientSocket);
    }

    public static void removeClientSocket(Socket clientSocket) {
        getClientsSockets().remove(clientSocket);
    }

    /**
     * main server method
     *
     * @param args Application arguments
     **/
    public static void main(String[] args) {
        ServerSocket listenSocket;
        CommunicationThread communicationThread = new CommunicationThread();

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0]));
            communicationThread.start();
            System.out.println("Server ready...");
            int i = 1;
            String name;
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from: " + clientSocket.getInetAddress());
                name = "Client" + i;
                ClientThread ct = new ClientThread(clientSocket, name);
                addClientSocket(clientSocket);
                ct.start();
                sendHistorical(clientSocket);
                sendConnectionMessage(name);
                i++;

            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer: " + e);
        }
    }

    public static void sendConnectionMessage(String name) {
        String connectionMessage = name + " a rejoint le chat !";
        CommunicationThread.offerQueue("System|" + connectionMessage);
    }

    public static void sendDisconnectionMessage(String name) {
        String disconnectionMessage = name + " a quitté le chat !";
        CommunicationThread.offerQueue("System|" + disconnectionMessage);
    }

    public static void sendHistorical(Socket clientSocket) throws IOException {
        PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
        List<String> historicalClone = new ArrayList<>(getHistorical());

        for (String message : historicalClone) {
            socOut.println(message);
        }
    }
}