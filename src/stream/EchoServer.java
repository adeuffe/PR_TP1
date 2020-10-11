package stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP Server with only one thread
 *
 * @author Loïc DUBOIS-TERMOZ
 * @author Alexandre DUFOUR
 */
public class EchoServer {

    /**
     * Listens to the client socket, receives requests from the client then sends an echo to the client.
     *
     * @param clientSocket the client socket
     **/
    static void doService(Socket clientSocket) {
        try {
            BufferedReader socIn;
            socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            while (true) {
                String line = socIn.readLine();
                socOut.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    /**
     * Main method for the server, waits for the client connection, then calls the service method.
     *
     * @param args Application arguments, must be the server port.
     */
    public static void main(String[] args) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("connexion from: " + clientSocket.getInetAddress());
                doService(clientSocket);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer: " + e);
        }
    }
}