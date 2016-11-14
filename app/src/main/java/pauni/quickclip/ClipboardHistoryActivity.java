package pauni.quickclip;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Roni on 07.11.2016.
 * adds clipcontent and timestamp to arrays
 * display them via customadapter
 */

public class ClipboardHistoryActivity extends AppCompatActivity{
    DatabaseHelper myDb;
    static String[] clipboards;
    static String[] timestamps;
    CardView cardView;
    ListView clipListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clipboard_history);
        clipListView = (ListView) findViewById(R.id.listview);
        //display this view if list has no entries
        cardView = (CardView) findViewById(R.id.card_NORECORD);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);


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


        clipListView.setAdapter(new ClipboardHistoryAdapter(this, clipboards) );
    }

    static void removeTime(int position) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(timestamps));
        list.remove(position);
        timestamps = list.toArray(new String[0]);
    }
    static String getTimestamps(int position) {
        return timestamps[position];
    }
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
