package stream;

import java.net.DatagramPacket;

/**
 * Reception thread for a multicast group member.
 *
 * @author Loïc DUBOIS-TERMOZ
 * @author Alexandre DUFOUR
 */
public class EchoMulticastReceptionThread extends Thread {

    /**
     * Run method of the thread. When it receives a message, it sends it to the standard output.
     **/
    public void run() {
        try {
            while (!this.isInterrupted()) {
                byte[] buf = new byte[1000];
                DatagramPacket p = new DatagramPacket(buf, buf.length);
                EchoServerMulticast.getMultSocket().receive(p);
                System.out.println(new String(p.getData()));
            }
        } catch (Exception e) {
            System.err.println("Error in EchoMulticastReceptionThread: " + e);
        }
    }
}
