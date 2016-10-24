package pauni.quickclip;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static pauni.quickclip.TCPServer.mHandler;

/**
 * Created by Roni on 25.10.2016.
 */

public class BackgroundService extends IntentService{
    TCPServer tcpServer;
    QuickClipProtocol quickClipProtocol;

    public BackgroundService() {
        super("tcp_intent_thread");
        mHandler = new Handler();
        tcpServer = new TCPServer(portNum);
        quickClipProtocol = new QuickClipProtocol(BackgroundService.this);
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
            Toast.makeText(BackgroundService.this, toastClipChanged, Toast.LENGTH_SHORT).show();

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

            /*mBuilder =
                    new NotificationCompat.Builder(TCPServer.this)
                            .setSmallIcon(R.mipmap.ic_launcher) //android.R.color.transparent
                            .setContentTitle(notificationTitle)
                            .setOngoing(true)
                            .setContentText(clipFromPC);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());*/
        }
    }



    @Override
    protected void onHandleIntent(Intent Intent) {
        String inputLine;
        String outputLine;
        while (true) {
            tcpServer.waitForClient();

            inputLine = tcpServer.getInput();
            outputLine = quickClipProtocol.processInput(inputLine);
            tcpServer.send(outputLine);
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        Toast.makeText(BackgroundService.this, "service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
