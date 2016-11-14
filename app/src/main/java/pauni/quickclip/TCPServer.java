package pauni.quickclip;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;


/**
 * Created by Roni on 04.10.2016.
 */
public class TCPServer {
    static ServerSocket server;
    static Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String inputLine;
    private int portNumb;
    static  String IP_ADDRESS = null;

    TCPServer(int portNumb) {
        //init the portNumb of the class with the param
        //create Handler...
        this.portNumb = portNumb;
        start();
    }


    void start() {
        try {
            server = new ServerSocket(portNumb);
        } catch (Exception e) { e.printStackTrace(); }
    }
    void stop() {
        try {
            server.close();
            client.close();
        } catch (Exception e) { e.printStackTrace(); }
    }


    void waitForClient() {
        //firstly: get a client
        client = null;

        while ( client == null ) {
            SystemClock.sleep(200);
            try {
                client = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!client.isConnected()) {
                client = null;
            }
        }
        //after client has connected, save IP for ClipSender
        IP_ADDRESS = client.getInetAddress().toString();

        //secondly: put a Reader and Writer onto the newly connected client
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

        } catch (Exception e) { e.printStackTrace(); }

    }


    String getInputLine() {
        inputLine = null;
        try {
            while (Objects.equals(inputLine, "") || inputLine == null) {
                inputLine = in.readLine();
            }
        } catch (Exception e) { e.printStackTrace(); }

        close();

        return inputLine;
    }

    static String getIpAddress() {
        return IP_ADDRESS;
    }
    void send (String outputLine) {
        out.println(outputLine);
    }


    private void close() {
        try {
            if( client != null) {
                client.close();
            }
            in.close();
            out.close();
        } catch (IOException e) { e.printStackTrace();}
    }

    //called at onDestroy of background service for proper reuse.. (not sure if required..)
    static void closeAll() {
        try {
            client.close();
        } catch (IOException e) { e.printStackTrace(); }
        try {
            server.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private class DisplayToast implements Runnable {
        private final Context mContext;
        String mText;
        public DisplayToast(Context mContext, String text){
            this.mContext = mContext;
            mText = text;
        }
        public void run(){
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
        }
    }
}