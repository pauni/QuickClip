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

class TCPClient {
    private Socket client;
    private PrintWriter out;

    TCPClient() {
    }

    void close() {
        try {
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void connect(String ip, int port) {
        try {
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) { e.printStackTrace(); }

        try {
            client = new Socket(ip, port);
        } catch (IOException e) { e.printStackTrace(); }
    }
    void send(String string) {
        out.println(string);
    }
}