package stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Client thread for a TCP multi-threaded Server.
 *
 * @author Loïc DUBOIS-TERMOZ
 * @author Alexandre DUFOUR
 */
public class ClientThread extends Thread {

    private final String name;
    private final  Socket clientSocket;

    /**
     * Client thread constructor.
     *
     * @param s client socket.
     * @param name name used by the client.
     */
    ClientThread(Socket s, String name) {
        this.clientSocket = s;
        this.name = name;
    }

    /**
     * Run method of the thread, receives a message request from client then send it to the communication thread,
     * or disconnects from the server if the message is the disconnection message.
     **/
    public void run() {
        try {
            BufferedReader socIn;
            socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            while (true) {
                String line = socIn.readLine();
                if (line.equals(".")) {
                    break;
                }
                this.sendEncodedMessage(line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer: " + e);
        } finally {
            EchoServerMultiThreaded.removeClientSocket(this.clientSocket);
            EchoServerMultiThreaded.sendDisconnectionMessage(this.name);
        }
    }

    /**
     * Encodes then sends the message received by the thread from the client to the communication thread.
     * @param message the message to encode and send.
     */
    public void sendEncodedMessage(String message) {
        CommunicationThread.offerQueue(this.name + "|" + message);
    }
}

  