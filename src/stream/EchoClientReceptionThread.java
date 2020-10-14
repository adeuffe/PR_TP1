package stream;

import java.io.BufferedReader;

/**
 * Reception thread of a TCP Client.
 *
 * @author Loïc DUBOIS-TERMOZ
 * @author Alexandre DUFOUR
 */
public class EchoClientReceptionThread extends Thread {
    private final BufferedReader socIn;

    /**
     * Reception thread constructor.
     *
     * @param s the input stream to listen.
     */
    EchoClientReceptionThread(BufferedReader s) {this.socIn = s;}

    /**
     * Run method of the thread. Listens the input stream. When a message is received from the server,
     * it is then sent to the standard output
     **/
    public void run() {
        try {
            String message = null;
            while (!this.isInterrupted()) {
                if (message != null)
                    System.out.println(message);
                message = socIn.readLine();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoClientReceptionThread:" + e);
        }
    }
}