package pauni.quickclip;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by Roni on 07.11.2016.
 * adds clipcontent and timestamp to arrays
 * display them via customadapter
 */

public class ClipboardHistoryActivity extends Activity{
    DatabaseHelper myDb;
    static String[] clipboards;
    static String[] timestamps;
    CardView cardView;
    ListView clipListView;
    BroadcastReceiver broadcast_reciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        clipListView = (ListView) findViewById(R.id.listview);
        cardView = (CardView) findViewById(R.id.card_NORECORD);
        broadcast_reciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));


        //save displays screen height and width
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float h = displayMetrics.heightPixels;
        float w = displayMetrics.widthPixels;

        //setting dialogs attributes
        getWindow().setLayout(Math.round(w*0.825F), Math.round(h*0.64F));
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(wlp);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.clipboard_history);



        //check if database is empty. If true, display info
        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllData();
        //display "no entries" view, remove list and reverse...
        if (res.getCount() == 0) {
            cardView.setVisibility(View.VISIBLE);
            clipListView.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.GONE);
            clipListView.setVisibility(View.VISIBLE);
        }


        //setting arraySize to the numbers of rows in the database
        int arraySize = safeLongToInt(myDb.getQueryNumEntries());
        clipboards = new String[arraySize];
        timestamps = new String[arraySize];

        //filling the string arrays
        int x = 0;
        while (res.moveToNext()) {
            clipboards[x] = res.getString(1);
            timestamps[x] = res.getString(2);
            x++;
        } res.close();

        //inverting the string arrays (for proper displaying in the list
        ArrayList<String> tempList1 = new ArrayList<>(Arrays.asList(clipboards));
        Collections.reverse(tempList1);
        clipboards = tempList1.toArray(new String[0]);

        ArrayList<String> tempList2 = new ArrayList<>(Arrays.asList(timestamps));
        Collections.reverse(tempList2);
        timestamps = tempList2.toArray(new String[0]);

        clipListView.setAdapter(new ClipboardHistoryAdapter(this, clipboards) );
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast_reciever);
        Log.d("cliphist on destroy", "reciever unregistered");
    }




    static void removeTime(int position) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(timestamps));
        list.remove(position);
        timestamps = list.toArray(new String[0]);
    }
    static String getTimestamps(int position) {
        return timestamps[position].substring(0,16);
    }
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
