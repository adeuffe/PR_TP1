package stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;

public class EchoClientMulticast {
    /**
     * main method
     * accepts a connection, receives a message from client then sends an echo to the client
     **/
    public static void main(String[] args) throws IOException {

        MulticastSocket echoSocket = null;
        InetAddress groupAddr = null;
        BufferedReader stdIn = null;
        DatagramPacket sendPacket = null;

        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
            System.exit(1);
        }

        try {
            // creation socket ==> connexion
            echoSocket = new MulticastSocket(Integer.parseInt(args[1]));
            groupAddr = InetAddress.getByName(args[0]);
            echoSocket.joinGroup(groupAddr);
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + args[0]);
            System.exit(1);
        }

        //EchoClientReceptionThread receptionThread = new EchoClientReceptionThread(socIn);
        //receptionThread.start();

        String line;
        while (true) {
            line = stdIn.readLine();
            sendPacket = new DatagramPacket(line.getBytes(), line.getBytes().length);
            if (line.equals(".")){
                break;
            }
        }

        System.out.println("Closing");
        //receptionThread.interrupt();
        echoSocket.leaveGroup(groupAddr);
        stdIn.close();
        echoSocket.close();
    }
}
