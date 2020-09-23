package stream;

import java.io.*;
import java.net.Socket;

public class EchoClientReceptionThread extends Thread {
    private BufferedReader socIn;

    EchoClientReceptionThread(BufferedReader s) {this.socIn = s;}

    /**
     * receives a message from server then print it on the client standard output
     **/
    public void run() {
        try {
            String message = null;
            while (!this.isInterrupted()) {
                if (message != null)
                    System.out.println("Server : " + message);
                message = socIn.readLine();
            }
            //System.out.println("Thread Stop");
        } catch (Exception e) {
            System.err.println("Error in EchoClientReceptionThread:" + e);
        }
    }
}
