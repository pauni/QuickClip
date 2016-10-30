package pauni.quickclip;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Roni on 27.10.2016.
 * Service to open URL.
 * Accessed by notification action via pendingintent
 */

public class OpenURL extends Service {
    @Override
    public void onCreate() {    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //open link here
        Toast.makeText(this, "klappt", Toast.LENGTH_SHORT).show();
        //closing the notification
        NotificationManager mNotifyMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(WaitForPcClip.NEWCLIP_ID);
        stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "link geöffnet", Toast.LENGTH_SHORT).show();
    }
}


