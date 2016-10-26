package pauni.quickclip;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Roni on 26.10.2016.
 */

public class CreateNotification {
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyMgr;
    private Context mContext;

    //setting the attributes of a notification
    CreateNotification(Context context, String title, String text) {
        mContext = context;
        mBuilder = new NotificationCompat.Builder(mContext)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(Color.TRANSPARENT); //transparent by default
    }

    //pass the icon id you want (R.drawable.xxx)
    void addIcon(int icon) {
        mBuilder.setSmallIcon(icon);
    }

    //adding an action, I call it button..
    //Action needs to be a Broadcast, Service or Activity class(!)
    void addButton(int buttonIcon, String buttonText, Class mClass) {
        //Creating an intent and pendingintent, just as required by android standards...
        Intent intent = new Intent(mContext, mClass);
        PendingIntent pendingIntent = PendingIntent.getService(
                mContext, 0, intent, 0);

        mBuilder.addAction(buttonIcon, buttonText, pendingIntent);
    }

    void publish(int ID) {
        mNotifyMgr = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(ID, mBuilder.build());

    }
}
