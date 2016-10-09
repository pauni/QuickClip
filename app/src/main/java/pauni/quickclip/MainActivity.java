package pauni.quickclip;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //create all views (don't initialize them)
    static TextView tv_debug = null;
    static String lol = null;
    int mNotificationId = 001;
    NotificationManager mNotifyMgr;
    NotificationCompat.Builder mBuilder;

    //onCreate is called at the start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout-xml-file which should be displayed
        setContentView(R.layout.activity_main);

        //here you can initialize the views to make them available
        //through the entire class

        //Create the object variable, give it's type in brackets
        //and use the findViewById method.
        //If you give a view on xml an ID, the ID is given a unique
        //integer which is saved in R.id.
        tv_debug = (TextView) findViewById(R.id.tV_debug);
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    public static void debugInfo(String string) {
        tv_debug.setText(string);
    }
    void startServer(View v){
        //create a testy notification
        Intent intent = new Intent(this, TCPServer.class);
        startService(intent);
        createNotification("Neue Zwischenablage", clip);

        debugInfo(lol);
    }

    String clip = "Ach wär das toll, wenn ich die Zwischenablage vom PC direkt" +
            " auf mein Smartphone übertragen könnte";
    //call this method with a String Title and Text to publish a Notification
    void createNotification(String title, String text) {

        mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.top)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setOngoing(true);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
    void updateNotification() {
        mBuilder.setContentText(clip);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}