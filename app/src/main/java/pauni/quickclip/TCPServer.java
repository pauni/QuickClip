package pauni.quickclip;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by Roni on 04.10.2016.
 */
public class TCPServer extends IntentService {
    NotificationCompat.Builder mBuilder;
    int mNotificationId = 001;
    NotificationManager mNotifyMgr;
    Handler mHandler;

    public TCPServer() {
        super("tcp_intent_thread");
        mHandler = new Handler();
    }

    public class DisplayToast implements Runnable {
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
    public class setClipboard implements Runnable {
        private final Context mContext;
        String clipFromPC;

        public setClipboard(Context mContext, String clipFromPC) {
            //initializing both variables with the given params
            this.mContext = mContext;
            this.clipFromPC = clipFromPC;
        }

        public void run() {
            //inform the user that his clipboard has been updated
            Toast.makeText(TCPServer.this, "clipboard updated", Toast.LENGTH_LONG).show();

            //Create a clipboardManager
            android.content.ClipboardManager clipboard =
                    (android.content.ClipboardManager) getSystemService(
                            Context.CLIPBOARD_SERVICE);
            //create a new ClipData object using clipFromPC as it's text
            android.content.ClipData clip =
                    android.content.ClipData.newPlainText(
                            "Copied Text", clipFromPC);
            //write ClipData object into clipboard
            clipboard.setPrimaryClip(clip);


            /*mBuilder = new NotificationCompat.Builder(TCPServer.this)
                    .setSmallIcon(android.R.color.transparent)
                    .setContentTitle("New clipboard")
                    .setOngoing(true)
                    .setContentText(clipFromPC);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

            mBuilder.setContentText(clipFromPC);
            mNotifyMgr.notify(mNotificationId, mBuilder.build()); */


        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        Toast.makeText(TCPServer.this, "service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
    @Override
    protected void onHandleIntent(Intent Intent) {
        //using try because to catch exceptions (e.g. port is used)
        //create server- and client. Init client with serverSocket.accept()
        //this method waits until a client connects to server Socket and binds
        //address and port of the client to the client
        //creating a printwriter (writes to the outputstream of the client
        //creating an inputstreamreader (listens to client's inputstream

            ServerSocket serverSocket = null;
            //connect serverSocket, toast the success/toast the failure
            try {
                serverSocket = new ServerSocket(6834);
                mHandler.post(new DisplayToast(this, "server is running"));
            } catch (IOException e) {
                mHandler.post(new DisplayToast(this,
                        "Could not listen on port 6834"));
            }

            Socket client = null;

        while (true) {
            //wait for a client (.accept())
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //create reader & writer(for further purposes), read from client
            //and write the input into clipboard
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                String input = in.readLine();
                mHandler.post(new setClipboard(this, input));
            } catch (IOException e) {
                mHandler.post(new DisplayToast(this, "Read failed"));
            }
            try {
                serverSocket.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}