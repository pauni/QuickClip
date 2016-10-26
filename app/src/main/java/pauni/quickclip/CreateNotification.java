package pauni.quickclip;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Roni on 26.10.2016.
 * simplified way to create notifications...
 * fewer lines to write and imo quicker
 */

class CreateNotification {
    private NotificationCompat.Builder mBuilder;
    private Context mContext;
    private int ID = 0;
    private NotificationManager mNotifyMgr;
    CreateNotification(Context context, String title, String text) {
        mContext = context;
        mBuilder = new NotificationCompat.Builder(mContext)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(Color.TRANSPARENT); //transparent by default
    }


    //pass the icon id you want (R.drawable.xxx)
    void setSmallIcon(int icon) {
        mBuilder.setSmallIcon(icon);
    }
    void setLargeIcon(Bitmap bitmap) {
        mBuilder.setLargeIcon(bitmap);
    }
    void setColor (int color) {
        mBuilder.setColor(color);
    }
    //adding an action, here called Button
    //Action needs to be a class extending Broadcast, Service or Activity
    void addButton(int buttonIcon, String buttonText, Class mClass) {
        //Creating an intent and pendingintent, just as required by android standards...
        Intent intent = new Intent(mContext, mClass);
        PendingIntent pendingIntent = PendingIntent.getService(
                mContext, BackgroundService.FLAG, intent, PendingIntent.FLAG_ONE_SHOT);

        BackgroundService.FLAG++;
        mBuilder.addAction(buttonIcon, buttonText, pendingIntent);
    }


    void publish(int ID) {
        this.ID = ID;
        mNotifyMgr = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(ID, mBuilder.build());

    }

    //don't call this before publishing
    void destroy() {
        if (ID != 0) {
            mNotifyMgr.cancel(ID);
        }
    }
}
