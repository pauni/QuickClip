package pauni.quickclip;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
    private String clipboard;
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
        String clip;
        public setClipboard(Context mContext, String clip) {
            this.mContext = mContext;
            this.clip = clip;
        }

        public void run() {
            clipboard = clip;
            //only for test purpose
            Toast.makeText(TCPServer.this, "Zwischenablage wurde geändert", Toast.LENGTH_LONG).show();
            writeInClipboard(clipboard);
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

        while (true) {
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

    public void writeInClipboard(String cliptext) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //"Copied Text" is not part of the cliptext. Just ignore it
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", cliptext);
        clipboard.setPrimaryClip(clip);
    }
}
