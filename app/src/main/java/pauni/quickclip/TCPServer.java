package pauni.quickclip;

import android.app.IntentService;
import android.app.Notification;
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
    static int language = 1; // 1=english | 2=german | 3=...
    static boolean run = true;
    static String toastConnected;
    static String toastClipChanged;
    static String notificationTitle;
    static String serverStarted;
    static ServerSocket serverSocket = null;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;
    static Handler mHandler;
    int mNotificationId = 001;

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

        setClipboard(Context mContext, String clipFromPC) {
            //initializing both variables with the given params
            this.mContext = mContext;
            this.clipFromPC = clipFromPC;
        }

        public void run() {
            //inform the user that his clipboard has been updated
            Toast.makeText(TCPServer.this, toastClipChanged, Toast.LENGTH_SHORT).show();

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


                mBuilder = new NotificationCompat.Builder(TCPServer.this)
                        .setSmallIcon(android.R.color.transparent)
                        .setContentTitle(notificationTitle)
                        .setOngoing(true)
                        .setContentText(clipFromPC);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());

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

            //connect serverSocket, toast the success/toast the failure
        try {
            serverSocket = new ServerSocket(6834);
            mHandler.post(new DisplayToast(this, serverStarted));
        } catch (IOException e) {
            mHandler.post(new DisplayToast(this,
                    "failed to listen on port 6834"));
        }

            Socket client = null;

        while (run) {
            //Create a test connection every minute to inform user when QuickClip lost connection
            //wait for a client (.accept())
            if (!run) { break; }
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!run) { break; }
            //create reader & writer(for further purposes), read from client
            //and write the input into clipboard
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                if (!run) { break; }
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                if (!run) { break; }
                String input = in.readLine();
                if (!run) { break; }
                mHandler.post(new setClipboard(this, input));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!run) { break; }
            try {
                serverSocket.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static void initStrings(String lang , Context context) {
        switch (lang) {
            case "english": //english
                serverStarted = context.getString(R.string.toastServerStarted_english);
                toastConnected = context.getString(R.string.toastConnected_english);
                toastClipChanged = context.getString(R.string.toastClipChanged_english);
                notificationTitle = context.getString(R.string.notificationTitle_english);
                break;
            case "deutsch": //german
                serverStarted = context.getString(R.string.toastServerStarted_german);
                toastConnected = context.getString(R.string.toastConnected_german);
                toastClipChanged = context.getString(R.string.toastClipChanged_german);
                notificationTitle = context. getString(R.string.notificationTitle_german);
                break;
            case "french": //coming soon
                break;
        }
    }
    static void stopServer(Context cont) {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Toast.makeText(cont, "server stopped", Toast.LENGTH_SHORT).show();
        }
    }

    //method for changing bool from ext. class
    static void setRun(boolean bool) {
        run = bool;
    }
    static void setLanguage(int lang) {
        language = lang;
    }
}