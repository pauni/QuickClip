package pauni.quickclip;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Roni on 25.10.2016.
 */

public class BackgroundService extends IntentService{
    TCPServer tcpServer;
    Handler mHandler;
    Context context;
    static boolean run = true;
    private static final int portNum = 6834;

    public BackgroundService() {
        super("tcp_intent_thread");
        mHandler = new Handler();
        tcpServer = new TCPServer(portNum);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        QuickClipProtocol quickClipProtocol = new QuickClipProtocol();
        String inputLine;
        String outputLine;

        /*mHandler.post(new DisplayToast(this, "server is running"));
        while (run) {
            tcpServer.start();
            tcpServer.waitForClient();
            inputLine = tcpServer.getInputLine();
            outputLine = quickClipProtocol.processInput(inputLine);
            tcpServer.send(outputLine);*/

            String clipComputer = "blablabla";
            if (clipComputer/* = quickClipProtocol.getClip())*/ != null) {
                createNotification("Clipboard from PC", clipComputer);
            }
       // }

        run = true;
        //tcpServer.stop();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        //Toast.makeText(BackgroundService.this, "service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
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

    private void createNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .addAction()

        NotificationManager mNotifyMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(111, mBuilder.build());
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
            Toast.makeText(BackgroundService.this, "clip changed", Toast.LENGTH_SHORT).show();

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
        }
    }

    static void setRun(boolean bool) {
        run = bool;
    }
}
