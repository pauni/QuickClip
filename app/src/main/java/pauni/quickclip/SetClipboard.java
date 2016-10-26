package pauni.quickclip;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Roni on 26.10.2016.
 */

public class SetClipboard extends Service{

    @Override
    public void onCreate() {
        ;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String clipFromPC = QuickClipProtocol.getClip();

        //creating a clipboardManager
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE);
        //creating a new ClipData object using clipFromPC as it's text
        android.content.ClipData newClipboard =
                android.content.ClipData.newPlainText(
                        "Copied Text", clipFromPC);
        //write ClipData object into clipboard
        clipboard.setPrimaryClip(newClipboard );

        Toast.makeText(this, getString(R.string.clipboard_refreshed), Toast.LENGTH_SHORT).show();

        NotificationManager mNotifyMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(BackgroundService.NEWCLIP_ID);
        stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service gestoppt", Toast.LENGTH_SHORT).show();
    }
}
