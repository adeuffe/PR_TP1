package stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class EchoServerMulticast {

    private static MulticastSocket multSocket;
    private static InetAddress groupAddr;
    private static int groupPort;

    public static MulticastSocket getMultSocket() {
        return multSocket;
    }

    public static InetAddress getGroupAddr() {
        return groupAddr;
    }

    public static int getGroupPort() {
        return groupPort;
    }

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

    public static void sendMessage(String message) throws IOException {
        DatagramPacket p = new DatagramPacket(message.getBytes(), message.length(), groupAddr, groupPort);
        multSocket.send(p);
    }
}
