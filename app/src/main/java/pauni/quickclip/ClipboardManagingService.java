package pauni.quickclip;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * Created by Roni on 27.10.2016.
 * This class consists of a PrimaryClipChangedListener and a tcp-client. When the clipboard
 * changes, the onPrimaryClipChanged method will forward the phones clip-content to the computer
 * regarding computer via a TCPClient instance.
 */

public class ClipboardManagingService extends Service {
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
    private Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        handler = new Handler();
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        clipboardManager = (android.content.ClipboardManager) getSystemService(
                Context.CLIPBOARD_SERVICE);
        context = getApplicationContext();
        //start both changedListener and pcClipListener in separate threads
        Thread thread_clipChangedListener =  new Thread (new ClipChangedListener());
        thread_clipChangedListener.start();
        Thread thread_computerClipListener =  new Thread (new ComputerClipListener());
        thread_computerClipListener.start();

        //a sticky notification which opens clipboard-history-dialog
        createHistoryNotification();

        //create a runnable here, which verifies the connection every 10 secs.
        Runnable testConnectivity = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 10000);
            }
        };
    }
    @Override
    public IBinder onBind(Intent intent) {
        //We don't want to bind
        return null;
    }
    @Override
    public void onDestroy() {
        clipboardManager.removePrimaryClipChangedListener(mPrimaryClipChangedListener);
        Toast.makeText(context, "Service destroyed", Toast.LENGTH_SHORT).show();
        Log.d("Listener", "closed");
    }


    private void createHistoryNotification() {
        //Intent and pendingintent for notification action (which opens the clip-history dialog)
        Intent notificationIntent = new Intent(this, ClipboardHistoryActivity.class);
        //actnewtsk to run as an own act.
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0 ,notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT); //one_shot pending intent would only work once
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Clipboard history")
                .setContentText("click to open clipboard history")
                .setSmallIcon(R.color.transparent)
                .setContentIntent(contentIntent);

        Notification n = builder.build(); //make it sticky!
        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        mNotifyMgr.notify(33, n);
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
                .setContentTitle(getString(R.string.new_clipboard))
                .setContentText(clipComputer)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(null)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.drawable.ic_content_copy_black_24dp,
                        getString(R.string.copy), setClip_pendingIntent);
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
                    getString(R.string.open), openUrl_pendingIntent);
            FLAG++;
        }
        else if (isWebAddress(url)) {
            url = "http://" + url;
            Intent openURL_intent = new Intent(context, OpenURLService.class);
            PendingIntent openUrl_pendingIntent = PendingIntent.getService(
                    context, 222, openURL_intent, PendingIntent.FLAG_ONE_SHOT);

            mBuilder.addAction(R.drawable.ic_open_in_browser_black_24dp,
                    getString(R.string.open), openUrl_pendingIntent);
            FLAG++;
        }
    }

    //change the Runnable extending classes into "new runnable..."
    class ClipChangedListener implements Runnable {
        String clip;
        DatabaseHelper myDb = new DatabaseHelper(getApplicationContext());

        public void run() {

            mPrimaryClipChangedListener =  new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    if(clipboardManager.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                        boolean isInserted = myDb.insertData((clipboardManager.getPrimaryClip().getItemAt(0).getText().toString()), getTimeStamp());
                        if (isInserted) Log.d("isInserted", "SUCCESS");
                    }
                    //if clip is not latestPCClip etc, send it to PC
                    if(clipIsOk()) {
                        /*tcpClient.connect(TCPServer.getIpAddress(), SERVER_PORT);
                        sendClip(clip);
                        tcpClient.close();
                    }*/
                        Log.d("ClipListener", "Clipboard changed");
                        Toast.makeText(ClipboardManagingService.this, "dispatched", Toast.LENGTH_SHORT).show();
                    }

                }
            };
            clipboardManager.addPrimaryClipChangedListener(mPrimaryClipChangedListener);
        }
        void sendClip(String clip) {
            QuickClipProtocol quickClipProtocol = new QuickClipProtocol(getApplicationContext());
            String outputLine = quickClipProtocol.sendClip(clip);
            tcpClient.send(outputLine);
        }
        private boolean clipIsOk() {
            CharSequence buffer;
            //clip shall be plain text, not be null and not be the clip the PC just send to us
            return clipboardManager.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN) &&
                    (buffer = clipboardManager.getPrimaryClip().getItemAt(0).getText()) != null &&
                    !Objects.equals((clip = buffer.toString()), QuickClipProtocol.computerClip);

        }
        private String getTimeStamp() {
            Calendar calender = Calendar.getInstance();
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            return dateFormat.format(calender.getTime());
        }
    }
    class ComputerClipListener implements Runnable {
        TCPServer tcpServer = new TCPServer(SERVER_PORT);
        QuickClipProtocol quickClipProtocol = new QuickClipProtocol(getApplicationContext());
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
                        startService(intent);
                    }
                }
            }
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
    static String getUrl() {
        return  url;
    }
}