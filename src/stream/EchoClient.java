package stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/***
 * TCP client of a TCP server
 *
 * @author Loïc DUBOIS-TERMOZ
 * @author Alexandre DUFOUR
 */
public class EchoClient {

    /**
     * Main method. Connects to the server, initializes the reception thread, then listens to the standard entry
     * and sends its content to the server.
     *
     * @param args Application arguments, must be the server address and port.
     **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader socIn = null;
        BufferedReader stdIn = null;

        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
            System.exit(1);
        }

        try {
            // creation socket ==> connexion
            echoSocket = new Socket(args[0], Integer.parseInt(args[1]));
            socOut = new PrintStream(echoSocket.getOutputStream());
            socIn = new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + args[0]);
            System.exit(1);
        }

        EchoClientReceptionThread receptionThread = new EchoClientReceptionThread(socIn);
        receptionThread.start();

        String line;
        while (true) {
            line = stdIn.readLine();
            socOut.println(line);
            if (line.equals(".")){
                break;
            }
        }

        System.out.println("Closing");
        receptionThread.interrupt();
        echoSocket.shutdownInput();
        echoSocket.shutdownOutput();
        socIn.close();
        socOut.close();
        stdIn.close();
        echoSocket.close();
    }
}


