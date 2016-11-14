package pauni.quickclip;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Roni on 26.10.2016.
 * This service, when launched, will grab the latest clip the PC sent and
 * apply it to the clipboard. This is only required for the notification action.
 * Otherwise use the method .setClipboard(String clip)
 *
 */

public class SetClipboard extends Service {
    //creating a clipboardManager
    ClipboardManager clipboard;

    Handler mHandler = new Handler();
    Runnable closeStatusbar = new Runnable() {
        @Override
        public void run() {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));;
        }
    };

    @Override
    public void onCreate() {
        clipboard  = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //new ClipData object, apply it to the clipboard
        ClipData clipData = ClipData.newPlainText("Copied Text", QuickClipProtocol.getComputerClip());
        setClipboard(clipData);

        //inform user that pc clip has been copied
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();

        //cancel the notification
        NotificationManager mNotifyMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(ClipboardManagingService.NEWCLIP_ID);

        //close bar and stop service
        mHandler.postDelayed(closeStatusbar, 350);
        stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    static void setClipboard(String clip, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        clipboard.setPrimaryClip(ClipData.newPlainText("Copied Text",clip));
        Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    private void setClipboard(ClipData clipData) {
        clipboard.setPrimaryClip(clipData);
    }
}