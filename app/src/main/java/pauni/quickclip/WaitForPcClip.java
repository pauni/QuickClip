package pauni.quickclip;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.webkit.URLUtil;
import android.widget.Toast;

/**
 * Created by Roni on 25.10.2016.
 * running until manually stopped and listening for connections to receive clips from PC.
 * Using QuickClipProtocol for authentication and parsing. After receiving clips, they are
 * offered to be written into phones clipboard by a notification ("COPY" action) or written
 * directly into clipboard if user set this in settings.
 */

public class WaitForPcClip extends IntentService{
    public static int NEWCLIP_ID = 111;
    TCPServer tcpServer;
    Handler mHandler;
    Context context;
    static int FLAG = 1;
    static boolean run = true;
    private static final int portNum = 6834;

    public WaitForPcClip() {
        super("tcp-server_intent_thread");
        mHandler = new Handler();
        tcpServer = new TCPServer(portNum);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        context = getApplicationContext();
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mHandler.post(new DisplayToast(getApplicationContext(), getString(R.string.server_is_active)));
        QuickClipProtocol quickClipProtocol = new QuickClipProtocol();
        String inputLine;
        String outputLine;

        tcpServer.start();
        mHandler.post(new DisplayToast(this, "server is running"));
        //Looping and waiting for clients, processing input and responding
        //loop can be escaped by setting run = false as "STOP SERVER" button does
        while (run) {
            tcpServer.waitForClient();
            inputLine = tcpServer.getInputLine();
            outputLine = quickClipProtocol.processInput(inputLine);
            tcpServer.send(outputLine);

            String clipComputer;
            if ( (clipComputer = QuickClipProtocol.getClip()) != null) {
                createClipNotification(clipComputer);
            }
       }
        run = true;
        tcpServer.stop();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(WaitForPcClip.this, "service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


    private void createClipNotification(String clipComputer) {
        //create bitmap as required for large icon
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_content_paste_white_36dp);

        //params are: Context, Title, Text.
        CreateNotification notification = new CreateNotification(getApplicationContext(),
                getString(R.string.new_clipboard), clipComputer);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setLargeIcon(bitmap);
        notification.setColor(R.color.colorPrimary);
        notification.addButton(R.drawable.ic_content_copy_black_24dp,
                getString(R.string.copy), SetClipboard.class);

        if (URLUtil.isValidUrl(clipComputer)) {
            notification.addButton(R.drawable.ic_open_in_browser_black_24dp,
                    getString(R.string.open), OpenURL.class);
        }

        notification.publish(NEWCLIP_ID);
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
