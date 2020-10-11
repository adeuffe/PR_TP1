package stream;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/***
 * Thread used by a TCP multi-threaded Server to communicate with all clients.
 *
 * @author Loïc DUBOIS-TERMOZ
 * @author Alexandre DUFOUR
 */
public class CommunicationThread extends Thread {

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    /**
     * Synchronized message queue getter.
     *
     * @return the message queue.
     */
    private static synchronized ConcurrentLinkedQueue<String> getQueue() {
        return queue;
    }

    /**
     * Offers a new message to the queue.
     *
     * @param message the message to offer to the queue.
     */
    public static synchronized void offerQueue(String message) {
        getQueue().offer(message);
    }

    /**
     * Run method of the thread. Polls the queue for a message, decodes the message, sends it to all the clients,
     * then adds it to the historical.
     */
    public void run() {
        while (true) {
            String message = getQueue().poll();
            if (message != null) {
                String[] decodedMessage = message.split("\\|");
                String finalMessage = decodedMessage[0] + " : " + decodedMessage[1];
                System.out.println(finalMessage);
                EchoServerMultiThreaded.addHistoricalMessage(finalMessage);
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
