package pauni.quickclip;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Roni on 25.10.2016.
 * running until manually stopped and listening for connections
 * to recieve clips from PC
 */

public class BackgroundService extends IntentService{
    public static int NEWCLIP_ID = 111;
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
        mHandler.post(new DisplayToast(BackgroundService.this, getString(R.string.server_is_active)));
        /*QuickClipProtocol quickClipProtocol = new QuickClipProtocol();
        String inputLine;
        String outputLine;

        mHandler.post(new DisplayToast(this, "server is running"));
        while (run) {
            tcpServer.start();
            tcpServer.waitForClient();
            inputLine = tcpServer.getInputLine();
            outputLine = quickClipProtocol.processInput(inputLine);
            tcpServer.send(outputLine);*/

            String clipComputer = QuickClipProtocol.getClip();
            if (clipComputer/* = quickClipProtocol.getClip())*/ != null) {
                //params are: Context, Title, Text.
                CreateNotification mNotification = new CreateNotification(getApplicationContext(),
                        getString(R.string.new_clipboard), clipComputer);
                mNotification.addIcon(R.mipmap.ic_launcher);
                mNotification.addButton(R.drawable.ic_content_copy_black_36dp,
                        getString(R.string.copy), SetClipboard.class);
                mNotification.publish(NEWCLIP_ID);
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
        Toast.makeText(BackgroundService.this, "service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }



    public class DisplayToast implements Runnable {
        private final Context mContext;
        String mText;

        private DisplayToast(Context mContext, String text){
            this.mContext = mContext;
            mText = text;
        }

        public void run(){
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
        }
    }

    static void setRun(boolean bool) {
        run = bool;
    }
}
