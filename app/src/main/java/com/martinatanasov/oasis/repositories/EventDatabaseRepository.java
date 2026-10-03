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

package com.martinatanasov.oasis.repositories;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.martinatanasov.oasis.R;
import com.martinatanasov.oasis.models.AddEventDTO;
import com.martinatanasov.oasis.models.UpdateEventDTO;

public class EventDatabaseRepository extends SQLiteOpenHelper {

    private final Context context;
    private final static String DATABASE_NAME = "oasis_color_events.db";
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
    private final static String COLUMN_VERSION = "version";

    public EventDatabaseRepository(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_LOCATION + " TEXT NOT NULL, " +
                COLUMN_EVENT + " TEXT NOT NULL, " +
                COLUMN_PICKED_COLOR + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_PICKED_AVATAR + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_START_YEAR + " INTEGER NOT NULL, " +
                COLUMN_START_MONTH + " INTEGER NOT NULL, " +
                COLUMN_START_DAY + " INTEGER NOT NULL, " +
                COLUMN_START_HOUR + " INTEGER NOT NULL, " +
                COLUMN_START_MINUTES + " INTEGER NOT NULL, " +
                COLUMN_END_YEAR + " INTEGER NOT NULL, " +
                COLUMN_END_MONTH + " INTEGER NOT NULL, " +
                COLUMN_END_DAY + " INTEGER NOT NULL, " +
                COLUMN_END_HOUR + " INTEGER NOT NULL, " +
                COLUMN_END_MINUTES + " INTEGER NOT NULL, " +
                COLUMN_CREATED_DATE + " INTEGER NOT NULL, " +
                COLUMN_MODIFIED_DATE + " INTEGER NOT NULL, " +
                COLUMN_DAY_EVENT + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_SOUND_NOTIFICATION + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_SILENT_NOTIFICATIONS + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_VERSION + " INTEGER NOT NULL DEFAULT 0" +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @SuppressLint("NewApi")
    public long addEvent(
            AddEventDTO addEventDTO
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, addEventDTO.title());
        cv.put(COLUMN_LOCATION, addEventDTO.location());
        cv.put(COLUMN_EVENT, addEventDTO.input());
        cv.put(COLUMN_PICKED_COLOR, addEventDTO.color());
        cv.put(COLUMN_PICKED_AVATAR, addEventDTO.avatar());
        cv.put(COLUMN_START_YEAR, addEventDTO.startYear());
        cv.put(COLUMN_START_MONTH, addEventDTO.startMonth());
        cv.put(COLUMN_START_DAY, addEventDTO.startDay());
        cv.put(COLUMN_START_HOUR, addEventDTO.startHour());
        cv.put(COLUMN_START_MINUTES, addEventDTO.startMinutes());
        cv.put(COLUMN_END_YEAR, addEventDTO.endYear());
        cv.put(COLUMN_END_MONTH, addEventDTO.endMonth());
        cv.put(COLUMN_END_DAY, addEventDTO.endDay());
        cv.put(COLUMN_END_HOUR, addEventDTO.endHour());
        cv.put(COLUMN_END_MINUTES, addEventDTO.endMinutes());
        cv.put(COLUMN_CREATED_DATE, addEventDTO.createdDate().toEpochMilli());
        cv.put(COLUMN_MODIFIED_DATE, addEventDTO.modifiedDate().toEpochMilli());
        cv.put(COLUMN_DAY_EVENT, addEventDTO.allDay());
        cv.put(COLUMN_SOUND_NOTIFICATION, addEventDTO.soundNotifications());
        cv.put(COLUMN_SILENT_NOTIFICATIONS, addEventDTO.silentNotifications());
        cv.put(COLUMN_VERSION, 0);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, R.string.toast_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.toast_added, Toast.LENGTH_SHORT).show();
        }
        //close connection
        db.close();
        return result;
    }

    public Cursor readAllEvents() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    @SuppressLint("NewApi")
    public void updateEvent(UpdateEventDTO updateEventDTO) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int incrementVersion = updateEventDTO.version() + 1;

        cv.put(COLUMN_TITLE, updateEventDTO.title());
        cv.put(COLUMN_LOCATION, updateEventDTO.location());
        cv.put(COLUMN_EVENT, updateEventDTO.node());
        cv.put(COLUMN_PICKED_COLOR, updateEventDTO.color());
        cv.put(COLUMN_PICKED_AVATAR, updateEventDTO.avatar());
        cv.put(COLUMN_START_YEAR, updateEventDTO.startYear());
        cv.put(COLUMN_START_MONTH, updateEventDTO.startMonth());
        cv.put(COLUMN_START_DAY, updateEventDTO.startDay());
        cv.put(COLUMN_START_HOUR, updateEventDTO.startHour());
        cv.put(COLUMN_START_MINUTES, updateEventDTO.startMinutes());
        cv.put(COLUMN_END_YEAR, updateEventDTO.endYear());
        cv.put(COLUMN_END_MONTH, updateEventDTO.endMonth());
        cv.put(COLUMN_END_DAY, updateEventDTO.endDay());
        cv.put(COLUMN_END_HOUR, updateEventDTO.endHour());
        cv.put(COLUMN_END_MINUTES, updateEventDTO.endMinutes());
        cv.put(COLUMN_CREATED_DATE, updateEventDTO.createdDate().toEpochMilli());
        cv.put(COLUMN_MODIFIED_DATE, updateEventDTO.modifiedDate().toEpochMilli());
        cv.put(COLUMN_DAY_EVENT, updateEventDTO.allDay());
        cv.put(COLUMN_SOUND_NOTIFICATION, updateEventDTO.soundNotifications());
        cv.put(COLUMN_SILENT_NOTIFICATIONS, updateEventDTO.silentNotifications());
        cv.put(COLUMN_VERSION, incrementVersion);

        long result = db.update(TABLE_NAME, cv, "_id=? AND version=?", new String[]{updateEventDTO.row_id(), String.valueOf(updateEventDTO.version())});
        if (result == -1 || result == 0) {
            //result = 0 no row matched (perhaps caused by version conflicts)
            //result = -1 indicate failure
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
        } else {
            Log.d("SilentNotification", "removeSilentNotification: Removed");
        }
        //close connection
        db.close();
    }

}
