package stream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCP multi-threaded Server
 *
 * @author Loïc DUBOIS-TERMOZ
 * @author Alexandre DUFOUR
 */
public class EchoServerMultiThreaded {

    private static final List<Socket> clientsSockets = new ArrayList<>();

    /**
     * Synchronized clients sockets getter.
     *
     * @return the list of the clients sockets.
     */
    public static synchronized List<Socket> getClientsSockets() {
        return clientsSockets;
    }

    /**
     * Adds a client socket to the clients sockets list.
     *
     * @param clientSocket the client socket to add.
     */
    private static void addClientSocket(Socket clientSocket) {
        getClientsSockets().add(clientSocket);
    }

    /**
     * Removes a client socket of the clients sockets list.
     *
     * @param clientSocket the client socket to remove.
     */
    public static void removeClientSocket(Socket clientSocket) {
        getClientsSockets().remove(clientSocket);
    }

    /**
     * Main server method. Initializes the server, then treats any incoming client connection.
     *
     * @param args Application arguments, must be the server port.
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
                sendConnectionMessage(name);
                i++;

            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer: " + e);
        }
    }

    /**
     * Sends to the communication thread a message announcing the connection of a client.
     *
     * @param name the name of the client to announce.
     */
    public static void sendConnectionMessage(String name) {
        String connectionMessage = name + " a rejoint le chat !";
        CommunicationThread.offerQueue("System|" + connectionMessage);
    }

    /**
     * Sends to the communication thread a message announcing the disconnection of a client.
     *
     * @param name the name of the client to announce.
     */
    public static void sendDisconnectionMessage(String name) {
        String disconnectionMessage = name + " a quitté le chat !";
        CommunicationThread.offerQueue("System|" + disconnectionMessage);
    }
}