package pauni.quickclip;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv_debug = null;
    EditText eT_password;

    DoBeforeStarting dbs;
    int mNotificationId = 001;
    NotificationManager mNotifyMgr;
    NotificationCompat.Builder mBuilder;

    //onCreate is called at the start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout-xml-file which should be displayed
        setContentView(R.layout.activity_main);

        //init stuff here for class-wide access
        tv_debug = (TextView) findViewById(R.id.tV_IPaddress);
        eT_password = (EditText) findViewById(R.id.eT_code);

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        dbs = new DoBeforeStarting(this);
        print(dbs.getLocalIpAddress());
    }

    public void print(String string) {
        tv_debug.setText(string);
    }
    public void startServer(View v){
        //starting the background process (intentservice) the usual way #google
        Intent intent = new Intent(this, TCPServer.class);
        startService(intent);
        createNotification("Neue Zwischenablage", "testy notification");
        //set run false to enable the while loop of onHandleIntent
        TCPServer.setRun(true);
    }
    void stop (View v) {
        //set run false to break the while loop of onHandleIntent
        //wait until while(run) has finished
        //pass the application-context to toasting
        TCPServer.setRun(false);
        TCPServer.stopServer(getApplication());
    }
    void createNotification(String title, String text) {

        mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.top)
                        .setContentTitle(title)
                        .setContentText(text);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
    void updateNotification() {
        mBuilder.setContentText("something");
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
    void changeLanguage(View v) {
        Context mContext = getApplicationContext();
        Button button = (Button) findViewById(R.id.bt_setLanguage);
        if (button.getText() == "DEUTSCH" ) {
            TCPServer.initStrings("deutsch", mContext);
            button.setText("ENGLISH");
        }
        else {
            TCPServer.initStrings("english", mContext);
            button.setText("DEUTSCH");
        }
    }
}