/***
 * EchoServerMultiThreaded
 * TCP Server multi-threaded
 * Date: 09/23/2020
 * Authors:
 * - DUBOIS-TERMOZ Loïc
 * - DUFFOUR Alexandre
 */

package stream;

import org.omg.PortableInterceptor.Interceptor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class EchoServerMultiThreaded{

    private static final List<Socket> clientsSockets = new ArrayList<>();
    private static final List<String> historical = new ArrayList<>();
    private static final String historicalFileDir = "./";
    private static final String historicalFileName = historicalFileDir + "historical.txt";

    public static synchronized List<String> getHistorical() {
        return historical;
    }

    public static synchronized void addHistoricalMessage(String message) {
        getHistorical().add(message);
        try {
            saveHistoricalMessage(message);
        } catch (IOException ex) {
            System.out.println("Error in saveHistoricalMessage : " + ex.getMessage());
            ex.printStackTrace();
        }
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
        // Application stop interception
        /*Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    saveHistorical();
                } catch (IOException ex) {
                    System.out.println("Error in saveHistorical : " + ex.getMessage());
                    ex.printStackTrace();
                }
                System.out.println("Server stopped");
            }
        });*/

        ServerSocket listenSocket;
        CommunicationThread communicationThread = new CommunicationThread();

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            initHistorical();
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

    public static void initHistorical() {
        // Import clubs
        File file = new File(historicalFileName);
        int car;
        StringBuilder buffer = new StringBuilder("");
        FileInputStream ftemp;

        try {
            ftemp = new FileInputStream(file);
            while( (car = ftemp.read()) != -1) {
                // Each line is treated after another, we don't want the ending "\r\n" of each line in the messages
                if ((char)car == '\r') {}//do nothing
                else if ((char)car == '\n') {
                    getHistorical().add(buffer.toString());
                    buffer.delete(0, buffer.length());
                }
                else
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