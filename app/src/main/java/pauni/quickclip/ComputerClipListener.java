package pauni.quickclip;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.webkit.URLUtil;

/**
 * Created by Roni on 23.11.2016.
 */

class ComputerClipListener implements Runnable {
    public static int NEWCLIP_ID = 111;

    //Creating clipboardManager and a clipChangedListener
    public ClipboardManager clipboardManager;
    ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener;
    static String SERVER_ADDRESS = null;
    private int SERVER_PORT = 6834; //port of choice for quickclip
    TCPClient tcpClient;
    boolean run = true;
    public NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyMgr;
    public Context context;
    public int FLAG = 0;
    static String url;
    QuickClipProtocol quickClipProtocol;

    ComputerClipListener(Context c) {
        context = c;
        quickClipProtocol = new QuickClipProtocol(context);
    }

    TCPServer tcpServer = new TCPServer(SERVER_PORT);

    String inputLine;
    String outputLine;


    public void run() {
        while (run) {
            SystemClock.sleep(100);

            tcpServer.waitForClient();//waitForClient loops until client connects
            inputLine = tcpServer.getInputLine(); //loops until client wrote smth.

            outputLine = quickClipProtocol.processInput(inputLine);
            tcpServer.send(outputLine);

            String computerClip = QuickClipProtocol.getComputerClip();
            if (computerClip != null) {
                //if user enabled notification, create one to accept clipboard manually
                if (MainActivity.notificationsEnabled) {
                    createClipNotification(computerClip);
                } else { //otherwise, just apply the PCclip to the phone's clipboard
                    Intent intent = new Intent(context, SetClipboard.class);
                    context.startService(intent);
                }
            }
        }
    }



    private void createClipNotification(String clipComputer) {
        //create bitmap as required for large icon
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_content_paste_white_36dp);

        //need to do SetClipbord.ClipboardString (instead of extras..) need to rework
        Intent setClip_intent = new Intent(context, SetClipboard.class);
        PendingIntent setClip_pendingIntent = PendingIntent.getService(
                context, 222, setClip_intent, PendingIntent.FLAG_ONE_SHOT);

        mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.new_clipboard))
                .setContentText(clipComputer)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(null)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.drawable.ic_content_copy_black_24dp,
                        context.getString(R.string.copy), setClip_pendingIntent);
        addOpenActionIfNeeded(clipComputer);

        mNotifyMgr.notify(NEWCLIP_ID, mBuilder.build());
    }
    private void addOpenActionIfNeeded(String clip) {
        url = clip;

        if ( URLUtil.isValidUrl(url) ) {
            Intent openUrl_intent = new Intent(context, OpenURLService.class);
            PendingIntent openUrl_pendingIntent = PendingIntent.getService(
                    context, 333, openUrl_intent, PendingIntent.FLAG_ONE_SHOT);

            mBuilder.addAction(R.drawable.ic_open_in_browser_black_24dp,
                    context.getString(R.string.open), openUrl_pendingIntent);
            FLAG++;
        }
        else if (isWebAddress(url)) {
            url = "http://" + url;
            Intent openURL_intent = new Intent(context, OpenURLService.class);
            PendingIntent openUrl_pendingIntent = PendingIntent.getService(
                    context, 222, openURL_intent, PendingIntent.FLAG_ONE_SHOT);

            mBuilder.addAction(R.drawable.ic_open_in_browser_black_24dp,
                    context.getString(R.string.open), openUrl_pendingIntent);
            FLAG++;
        }
    }




    private boolean isWebAddress(String string) {
        if (string.length() < 7) {
            return false;
        } else if( string.substring(0, 4).equals("www.")) {
            return true;
        }
        return false;
    }


    //for the OpenURLService to grab the Url which is supposed to be visited
    static String getUrl() {
        return  url;
    }
}
