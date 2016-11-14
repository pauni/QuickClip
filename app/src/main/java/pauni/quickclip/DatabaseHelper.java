package pauni.quickclip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Roni on 12.11.2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME= "clips_table";
    private static final String DATABASE_NAME = "clips.db";
    private static final String COL1 = "ID";
    private static final String COL2 = "CLIPBOARD_CONTENT";
    private static final String COL3 = "TIME_STAMP";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CLIPBOARD_CONTENT TEXT, TIME_STAMP TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String CLIPBOARD_CONTENT, String TIME_STAMP) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, CLIPBOARD_CONTENT);
        contentValues.put(COL3, TIME_STAMP);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        Log.d("InsertData", "HINZUGEFÜGT");
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);

    }

    public long getQueryNumEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }
}
