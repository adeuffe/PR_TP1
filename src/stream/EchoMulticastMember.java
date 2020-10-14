package stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * UDP multi-threaded Server using multicast IP groups
 *
 * @author Loïc DUBOIS-TERMOZ
 * @author Alexandre DUFOUR
 */
public class EchoMulticastMember {

    private static MulticastSocket multSocket;
    private static InetAddress groupAddr;
    private static int groupPort;

    /**
     * Multicast socket getter.
     *
     * @return the multicast socket
     */
    public static MulticastSocket getMultSocket() {
        return multSocket;
    }

    /**
     * Group IP address getter.
     *
     * @return the IP address of the multicast group
     */
    public static InetAddress getGroupAddr() {
        return groupAddr;
    }

    /**
     * Group Port getter.
     *
     * @return the port of the multicast group
     */
    public static int getGroupPort() {
        return groupPort;
    }

    /**
     * Main method. Initializes a multicast group, runs a reception thread, then listens to the standard entry,
     * and sends its content to the multicast group.
     *
     * @param args Application arguments, must be the multicast group port.
     * @throws IOException if an I/O exception is raised while reading the standard entry
     */
    public static void main(String[] args) throws IOException {
        BufferedReader stdIn = null;
        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        groupPort = Integer.parseInt(args[0]);
        try {
            groupAddr = InetAddress.getByName("224.0.0.1");
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            multSocket = new MulticastSocket(groupPort);
            multSocket.joinGroup(groupAddr);
        } catch (UnknownHostException e) {
            System.err.println("Host unknown: " + e);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO error: " + e);
            System.exit(1);
        }

        EchoMulticastReceptionThread receptionThread = new EchoMulticastReceptionThread();
        receptionThread.start();

        String line;
        while (true) {
            line = stdIn.readLine();
            sendMessage(line);
            if (line.equals(".")){
                break;
            }
        }

        System.out.println("Closing");
        multSocket.leaveGroup(groupAddr);
        stdIn.close();
        multSocket.close();
    }

    /**
     * Sends a message to the multicast group.
     *
     * @param message the message to send
     * @throws IOException if an I/O exception is raised by sending the message
     */
    public static void sendMessage(String message) throws IOException {
        DatagramPacket p = new DatagramPacket(message.getBytes(), message.length(), groupAddr, groupPort);
        multSocket.send(p);
    }
}
