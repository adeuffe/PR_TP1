package stream;

import java.net.DatagramPacket;

public class EchoMulticastReceptionThread extends Thread {

    /**
     * receives a message from server then print it on the client standard output
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
