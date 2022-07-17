package com.martinatanasov.colornotebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

class MyDatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private final static String DATABASE_NAME = "ColorEventsLibrary.db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "my_events";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_TITLE = "event_title";
    private final static String COLUMN_LOCATION = "event_location";
    private final static String COLUMN_EVENT = "event_node";
    private final static String COLUMN_START_DATE = "start_date";
    private final static String COLUMN_END_DATE = "end_date";
    private final static String COLUMN_DAY_EVENT = "day_event";
    private final static String COLUMN_SOUND_NOTIFICATION = "sound_notification";
    private final static String COLUMN_SILENT_NOTIFICATIONS = "silent_notification";
    private final static String COLUMN_PICKED_COLOR = "picked_color";
    private final static String COLUMN_PICKED_AVATAR = "picked_avatar";

    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_LOCATION + " TEXT, " +
                        COLUMN_EVENT + " TEXT, " +
                        COLUMN_PICKED_COLOR + " INTEGER, " +
                        COLUMN_PICKED_AVATAR + " INTEGER, " +
                        COLUMN_START_DATE + " TEXT, " +
                        COLUMN_END_DATE + " TEXT, " +
                        COLUMN_DAY_EVENT + " INTEGER, " +
                        COLUMN_SOUND_NOTIFICATION + " INTEGER, " +
                        COLUMN_SILENT_NOTIFICATIONS + " INTEGER);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addEvent(String title, String location, String input, int color, int avatar, String startTime, String endTime,
                  int allDay , int soundNotifications, int silentNotifications){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_EVENT, input);
        cv.put(COLUMN_PICKED_COLOR, color);
        cv.put(COLUMN_PICKED_AVATAR, avatar);
        cv.put(COLUMN_START_DATE, startTime);
        cv.put(COLUMN_END_DATE, endTime);
        cv.put(COLUMN_DAY_EVENT, allDay);
        cv.put(COLUMN_SOUND_NOTIFICATION, soundNotifications);
        cv.put(COLUMN_SILENT_NOTIFICATIONS, silentNotifications);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1){
            Toast.makeText(context, R.string.toast_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.toast_added, Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }

    void updateData(String row_id, String title, String location, String node){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_LOCATION, location);
        cv.put(COLUMN_EVENT, node);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String []{row_id});
        if (result == -1){
            Toast.makeText(context, R.string.toast_failed_to_update, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.toast_successfully_updated, Toast.LENGTH_SHORT).show();
        }
    }

    void deleteDataOnOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        
        if (result == -1){
            Toast.makeText(context, R.string.toast_fail_to_delete, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.toast_successfully_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        //Delete DB file from the phone
        //db.deleteDatabase(new File(db.getPath()));
    }

//    public boolean swipeDeleteItem(String delItem){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String queryString = ("DELETE FROM " + TABLE_NAME + "WHERE " + COLUMN_ID + " = " + delItem);
//        Cursor cursor = db.rawQuery(queryString, null);
//
//        if(cursor.moveToFirst()){
//            return true;
//        }else{
//            return false;
//        }
//    }

}
