package stream;

import java.io.*;
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
    private static final List<String> historical = new ArrayList<>();
    private static final String historicalFileDir = "./";
    private static final String historicalFileName = historicalFileDir + "historical.txt";

    /**
     * Synchronized historical getter.
     *
     * @return the historical, as a list of the sent messages.
     */
    public static synchronized List<String> getHistorical() {
        return historical;
    }

    /**
     * Adds synchronously a message to the historical and the historical file as well.
     *
     * @param message the message to add.
     */
    public static synchronized void addHistoricalMessage(String message) {
        getHistorical().add(message);
        try {
            saveHistoricalMessage(message);
        } catch (IOException ex) {
            System.out.println("Error in saveHistoricalMessage : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

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
     * Main server method. Initializes the server, retrieves the historical from the historical file,
     * then treats any incoming client connection.
     *
     * @param args Application arguments, must be the server port.
     **/
    public static void main(String[] args) {
        ServerSocket listenSocket;
        EchoServerCommunicationThread echoServerCommunicationThread = new EchoServerCommunicationThread();

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            initHistorical();
            listenSocket = new ServerSocket(Integer.parseInt(args[0]));
            echoServerCommunicationThread.start();
            System.out.println("Server ready...");
            int i = 1;
            String name;
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from: " + clientSocket.getInetAddress());
                name = "Client" + i;
                EchoServerClientThread ct = new EchoServerClientThread(clientSocket, name);
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

    /**
     * Sends to the communication thread a message announcing the connection of a client.
     *
     * @param name the name of the client to announce.
     */
    public static void sendConnectionMessage(String name) {
        String connectionMessage = name + " a rejoint le chat !";
        EchoServerCommunicationThread.offerQueue("System|" + connectionMessage);
    }

    /**
     * Sends to the communication thread a message announcing the disconnection of a client.
     *
     * @param name the name of the client to announce.
     */
    public static void sendDisconnectionMessage(String name) {
        String disconnectionMessage = name + " a quitté le chat !";
        EchoServerCommunicationThread.offerQueue("System|" + disconnectionMessage);
    }

    /**
     * Sends to a client the whole historical of the messages.
     *
     * @param clientSocket the socket of the client
     * @throws IOException if an I/O exception is raised by the client socket
     */
    public static void sendHistorical(Socket clientSocket) throws IOException {
        PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
        List<String> historicalClone = new ArrayList<>(getHistorical());

        for (String message : historicalClone) {
            socOut.println(message);
        }
    }

    /**
     * Initializes the historical by retrieving all the messages from the historical file.
     */
    public static void initHistorical() {
        // Import clubs
        File file = new File(historicalFileName);
        int car;
        StringBuilder buffer = new StringBuilder();
        FileInputStream ftemp;

        try {
            ftemp = new FileInputStream(file);
            while( (car = ftemp.read()) != -1) {
                // Each line is treated after another, we don't want the ending "\r\n" of each line in the messages
                if ((char)car == '\n') {
                    getHistorical().add(buffer.toString());
                    buffer.delete(0, buffer.length());
                }
                else if ((char)car != '\r')
                    buffer.append((char)car);
            }
            ftemp.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("Fichier introuvable");
        }
        catch(IOException ioe) {
            System.out.println("Exception " + ioe);
        }
    }

    /**
     * Appends a message to the historical file to save it.
     *
     * @param message the message to save
     * @throws IOException if an I/O exception is raised by the file opening or writing
     */
    public static void saveHistoricalMessage(String message) throws IOException {
        // Creation of the repertory if it doesn't exist
        if(!new File(historicalFileDir).exists())
        {
            // Créer le dossier avec tous ses parents
            new File(historicalFileDir).mkdirs();
            System.out.println("App Directory created.");
        }

        // Creating file (if it doesn't already exist) and writing the historical in it
        FileWriter fw = new FileWriter(historicalFileName, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(message);
        bw.newLine();
        bw.close();
    }

}