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
    ServerSocket server;
    Socket client;
    Handler mHandler;
    BufferedReader in;
    PrintWriter out;
    int portNumb;

    public TCPServer(int portNumb) {
        //init the portNumb of the class with the param
        //create Handler...
        this.portNumb = portNumb;
        mHandler = new Handler();
    }

    void start() {
        try {
            server = new ServerSocket(portNumb);
        } catch (Exception e) { e.printStackTrace(); }
    }
    void waitForClient() {
        //firstly: get a client
        client = null;
        while (client == null) {
            SystemClock.sleep(100);
            try {
                client = server.accept();
            } catch (IOException e) { e.printStackTrace(); }
        }

        //secondly: put a Reader and Writer onto the newly connected client
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    //not ready yet, have to go to toilet!!!
    String getInput() {
        String input = null;
        try {
            input = in.readLine();
        } catch (IOException e) { e.printStackTrace(); }

        return input;
    }

    void stop() {
        try {
            server.close();
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

