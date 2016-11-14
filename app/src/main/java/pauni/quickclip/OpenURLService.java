package pauni.quickclip;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Created by Roni on 04.11.2016.
 * Opens links. Service as notification actions require that
 */

public class OpenURLService extends Service {
    String url;
    @Override
    public void onCreate() {
        url = ClipboardManagingService.getUrl();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        NotificationManager mNotifyMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(ClipboardManagingService.NEWCLIP_ID);
        SystemClock.sleep(700);

        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
    }
}
