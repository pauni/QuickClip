package pauni.quickclip;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Roni on 27.10.2016.
 * TCP client to send data to a TCP server running
 * on the related computer (linux, mac, win)
 * accessed when user copies something on the phone
 */

public class TCPClient {
    Socket client;
    PrintWriter out;

    TCPClient(String address, int port) {
        try {
            client = new Socket(address, port);
        } catch (IOException e) { e.printStackTrace();}

        if(client != null) {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    void send(String string) {
        out.println(string);
    }
}
