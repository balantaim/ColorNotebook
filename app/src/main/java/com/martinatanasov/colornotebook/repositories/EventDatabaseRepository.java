/*
 * Copyright (c) 2022 Martin Atanasov. All rights reserved.
 *
 * IMPORTANT!
 * Use of .xml vector path, .svg, .png and .bmp files, as well as all brand logos,
 * is excluded from this license. Any use of these file types or logos requires
 * prior permission from the respective owner or copyright holder.
 *
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package com.martinatanasov.colornotebook.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.dto.AddEvent;
import com.martinatanasov.colornotebook.dto.UpdateEvent;

public class EventDatabaseRepository extends SQLiteOpenHelper {

    private final Context context;
    private final static String DATABASE_NAME = "ColorEventsLibrary.db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "my_events";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_TITLE = "event_title";
    private final static String COLUMN_LOCATION = "event_location";
    private final static String COLUMN_EVENT = "event_node";
    private final static String COLUMN_START_YEAR = "start_year";
    private final static String COLUMN_START_MONTH = "start_month";
    private final static String COLUMN_START_DAY = "start_day";
    private final static String COLUMN_START_HOUR = "start_hour";
    private final static String COLUMN_START_MINUTES = "start_minutes";
    private final static String COLUMN_END_YEAR = "end_year";
    private final static String COLUMN_END_MONTH = "end_month";
    private final static String COLUMN_END_DAY = "end_day";
    private final static String COLUMN_END_HOUR = "end_hour";
    private final static String COLUMN_END_MINUTES = "end_minutes";
    private final static String COLUMN_CREATED_DATE = "created_date";
    private final static String COLUMN_MODIFIED_DATE = "modified_date";
    private final static String COLUMN_DAY_EVENT = "day_event";
    private final static String COLUMN_SOUND_NOTIFICATION = "sound_notification";
    private final static String COLUMN_SILENT_NOTIFICATIONS = "silent_notification";
    private final static String COLUMN_PICKED_COLOR = "picked_color";
    private final static String COLUMN_PICKED_AVATAR = "picked_avatar";

    public EventDatabaseRepository(@Nullable Context context) {
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
                COLUMN_START_YEAR + " INTEGER, " +
                COLUMN_START_MONTH + " INTEGER, " +
                COLUMN_START_DAY + " INTEGER, " +
                COLUMN_START_HOUR + " INTEGER, " +
                COLUMN_START_MINUTES + " INTEGER, " +
                COLUMN_END_YEAR + " INTEGER, " +
                COLUMN_END_MONTH + " INTEGER, " +
                COLUMN_END_DAY + " INTEGER, " +
                COLUMN_END_HOUR + " INTEGER, " +
                COLUMN_END_MINUTES + " INTEGER, " +
                COLUMN_CREATED_DATE + " INTEGER, " +
                COLUMN_MODIFIED_DATE + " INTEGER, " +
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

    public void addEvent(
            AddEvent addEvent
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, addEvent.title());
        cv.put(COLUMN_LOCATION, addEvent.location());
        cv.put(COLUMN_EVENT, addEvent.input());
        cv.put(COLUMN_PICKED_COLOR, addEvent.color());
        cv.put(COLUMN_PICKED_AVATAR, addEvent.avatar());
        cv.put(COLUMN_START_YEAR, addEvent.startYear());
        cv.put(COLUMN_START_MONTH, addEvent.startMonth());
        cv.put(COLUMN_START_DAY, addEvent.startDay());
        cv.put(COLUMN_START_HOUR, addEvent.startHour());
        cv.put(COLUMN_START_MINUTES, addEvent.startMinutes());
        cv.put(COLUMN_END_YEAR, addEvent.endYear());
        cv.put(COLUMN_END_MONTH, addEvent.endMonth());
        cv.put(COLUMN_END_DAY, addEvent.endDay());
        cv.put(COLUMN_END_HOUR, addEvent.endHour());
        cv.put(COLUMN_END_MINUTES, addEvent.endMinutes());
        cv.put(COLUMN_CREATED_DATE, addEvent.createdDate());
        cv.put(COLUMN_MODIFIED_DATE, addEvent.modifiedDate());
        cv.put(COLUMN_DAY_EVENT, addEvent.allDay());
        cv.put(COLUMN_SOUND_NOTIFICATION, addEvent.soundNotifications());
        cv.put(COLUMN_SILENT_NOTIFICATIONS, addEvent.silentNotifications());

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, R.string.toast_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.toast_added, Toast.LENGTH_SHORT).show();
        }
        //close connection
        db.close();
    }

    public Cursor readAllEvents() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        //close connection
//        assert db != null;
//        db.close();
        return cursor;
    }

    public void updateEvent(UpdateEvent updateEvent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, updateEvent.title());
        cv.put(COLUMN_LOCATION, updateEvent.location());
        cv.put(COLUMN_EVENT, updateEvent.node());
        cv.put(COLUMN_PICKED_COLOR, updateEvent.color());
        cv.put(COLUMN_PICKED_AVATAR, updateEvent.avatar());
        cv.put(COLUMN_START_YEAR, updateEvent.startYear());
        cv.put(COLUMN_START_MONTH, updateEvent.startMonth());
        cv.put(COLUMN_START_DAY, updateEvent.startDay());
        cv.put(COLUMN_START_HOUR, updateEvent.startHour());
        cv.put(COLUMN_START_MINUTES, updateEvent.startMinutes());
        cv.put(COLUMN_END_YEAR, updateEvent.endYear());
        cv.put(COLUMN_END_MONTH, updateEvent.endMonth());
        cv.put(COLUMN_END_DAY, updateEvent.endDay());
        cv.put(COLUMN_END_HOUR, updateEvent.endHour());
        cv.put(COLUMN_END_MINUTES, updateEvent.endMinutes());
        cv.put(COLUMN_CREATED_DATE, updateEvent.createdDate());
        cv.put(COLUMN_MODIFIED_DATE, updateEvent.modifiedDate());
        cv.put(COLUMN_DAY_EVENT, updateEvent.allDay());
        cv.put(COLUMN_SOUND_NOTIFICATION, updateEvent.soundNotifications());
        cv.put(COLUMN_SILENT_NOTIFICATIONS, updateEvent.silentNotifications());

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{updateEvent.row_id()});
        if (result == -1) {
            Toast.makeText(context, R.string.toast_failed_to_update, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.toast_successfully_updated, Toast.LENGTH_SHORT).show();
        }
        //close connection
        db.close();
    }

    public void deleteEventOnOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, R.string.toast_fail_to_delete, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.toast_successfully_deleted, Toast.LENGTH_SHORT).show();
        }
        //close connection
        db.close();
    }

    public void deleteAllEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        //Delete DB file from the phone
        //db.deleteDatabase(new File(db.getPath()));
        //close connection
        db.close();
    }

    public void removeSoundNotification(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SOUND_NOTIFICATION, 0);
        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, R.string.toast_fail_to_remove_alarm, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.toast_alarm_cancel, Toast.LENGTH_SHORT).show();
        }
        //close connection
        db.close();
    }

    public void removeSilentNotification(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SILENT_NOTIFICATIONS, 0);
        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if (result == -1) {
            Log.d("SilentNotification", "removeSilentNotification: Not removed!");
            //Toast.makeText(context, R.string.toast_fail_to_remove_alarm, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("SilentNotification", "removeSilentNotification: Removed");
            //Toast.makeText(context, R.string.toast_alarm_cancel, Toast.LENGTH_SHORT).show();
        }
        //close connection
        db.close();
    }

//    public boolean swipeDeleteItem(String delItem){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String queryString = ("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + delItem);
//        Cursor cursor = db.rawQuery(queryString, null);
//
//        if(cursor.moveToFirst()){
//            return true;
//        }else{
//            return false;
//        }
//    }

}
