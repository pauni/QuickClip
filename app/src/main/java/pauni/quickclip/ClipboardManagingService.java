package pauni.quickclip;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by Roni on 27.10.2016.
 * This class consists of a PrimaryClipChangedListener and a tcp-client. When the clipboard
 * changes, the onPrimaryClipChanged method will forward the phones clip-content to the computer
 * regarding computer via a TCPClient instance.
 */

public class ClipboardManagingService extends Service {
    public static int NEWCLIP_ID = 111;

    public ClipboardManager clipboardManager;
    private NotificationManager mNotifyMgr;
    public Context context;
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

        context = getApplicationContext();
        //start both changedListener and pcClipListener in separate threads
        Thread thread_clipChangedListener =  new Thread (new ClipChangedListener(getApplicationContext()));
        thread_clipChangedListener.start();
        Thread thread_computerClipListener =  new Thread (new ComputerClipListener(getApplicationContext()));
        thread_computerClipListener.start();

        //a sticky notification which opens clipboard-history-dialog
        createHistoryNotification();
    }
    @Override
    public IBinder onBind(Intent intent) {
        //We don't want to bind
        return null;
    }
    @Override
    public void onDestroy() {
        ClipChangedListener.unregOnClickListener(getApplicationContext());
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


}