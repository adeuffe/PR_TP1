/***
 * CommunicationThread
 * Communication thread for interactions with clients sockets
 * Date: 09/23/2020
 * Authors:
 * - DUBOIS-TERMOZ Loïc
 * - DUFFOUR Alexandre
 */

package stream;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationThread extends Thread {

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    private static synchronized ConcurrentLinkedQueue<String> getQueue() {
        return queue;
    }

    public static synchronized void offerQueue(String message) {
        getQueue().offer(message);
    }

    public void run() {
        while (true) {
            String message = getQueue().poll();
            if (message != null) {
                String[] decodedMessage = message.split("\\|");
                String finalMessage = decodedMessage[0] + " : " + decodedMessage[1];
                System.out.println(finalMessage);
                EchoServerMultiThreaded.getClientsSockets().stream().forEach((Socket socketClient) -> {
                    try {
                        PrintStream socOut = new PrintStream(socketClient.getOutputStream());
                        socOut.println(finalMessage);
                    } catch (IOException e) {
                        System.err.println("Error in CommunicationThread: " + e);
                    }
                });
            }
        }
    }
}
