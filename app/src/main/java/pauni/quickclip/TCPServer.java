package pauni.quickclip;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by Roni on 04.10.2016.
 */
public class TCPServer  {
    private ServerSocket server;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String inputLine;
    private int portNumb;



    TCPServer (int portNumb) {
        //init the portNumb of the class with the param
        //create Handler...
        this.portNumb = portNumb;
    }



    void start() {
        try {
            server = new ServerSocket(portNumb);
        } catch (Exception e) { e.printStackTrace(); }
    }
    void stop() {
        try {
            server.close();

        } catch (Exception e) { e.printStackTrace(); }
    }


    void waitForClient() {
        //firstly: get a client
        client = null;
        while (client == null && WaitForPcClip.run) {
            SystemClock.sleep(100);
            try {
                client = server.accept();
            } catch (IOException e) { e.printStackTrace(); }
        }

        //after client has connected, IP is given  to the ClipboardSender class, so that
        //the client knows under which IP the server will be available
        ClipboardSender.setServerAddress(client.getInetAddress().toString());

        //secondly: put a Reader and Writer onto the newly connected client
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e) { e.printStackTrace(); }

        readStream();
    }
    String readStream() {
        inputLine = null;
        try {
             inputLine = in.readLine();
        } catch (Exception e) { e.printStackTrace(); }

        if (inputLine == null) {
            close();
            return "no input";
        }
        else {
            close();
            return inputLine;
        }

    }



    //not ready yet, have to go to toilet!!!
    String getInputLine() {
        return inputLine;
    }
    void send (String outputLine) {
        out.println(outputLine);
    }


    private void close() {
        try {
            client.close();
            in.close();
            out.close();
        } catch (IOException e) { e.printStackTrace();}
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

