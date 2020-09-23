/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {

    private String name;
    private Socket clientSocket;

    ClientThread(Socket s, String name) {
        this.clientSocket = s;
        this.name = name;
    }

    /**
     * receives a request from client then sends an echo to the client
     **/
    public void run() {
        try {
            BufferedReader socIn = null;
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

    public void sendEncodedMessage(String message) {
        CommunicationThread.offerQueue(this.name + "|" + message);
    }
}

  