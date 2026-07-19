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

package com.martinatanasov.colornotebook.utils.events;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.martinatanasov.colornotebook.R;

import java.util.concurrent.TimeUnit;

public class SilentNotificationWorker extends Worker {

    private static final String TAG = "SilentNotificationWorker";
    private static final String CHANNEL_ID = "silent_notifications_channel";
    private static final String WORK_TAG_PREFIX = "silent_notification_work_";
    private static final String GROUP_KEY_SILENT = "com.martinatanasov.colornotebook.SILENT_GROUP";
    private static final int SUMMARY_ID = 1;
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_NOTE = "note";
    private static final String KEY_COLOR = "color";
    private static final String KEY_PRIORITY = "priority";

    public SilentNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    public static void scheduleSilentNotification(Context context, String id, String title, String note, int color, int priority, long delayMillis) {
        Data inputData = new Data.Builder()
                .putString(KEY_ID, id)
                .putString(KEY_TITLE, title)
                .putString(KEY_NOTE, note)
                .putInt(KEY_COLOR, color)
                .putInt(KEY_PRIORITY, priority)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SilentNotificationWorker.class)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG_PREFIX + id)
                .addTag(TAG)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueue(workRequest);
        Log.d(TAG, "Scheduled silent notification for id: " + id + " with delay: " + delayMillis);
    }

    public static void cancelSilentNotification(Context context, String id) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG_PREFIX + id);
        Log.d(TAG, "Cancelled silent notification for id: " + id);
    }

    public static void cancelAllSilentNotifications(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG);
        Log.d(TAG, "Cancelled all silent notifications");
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: Start");
        Context context = getApplicationContext();

        String id = getInputData().getString(KEY_ID);
        String title = getInputData().getString(KEY_TITLE);
        String note = getInputData().getString(KEY_NOTE);
        int colorIndex = getInputData().getInt(KEY_COLOR, 0);

        int notificationId = (id != null) ? Integer.parseInt(id) : 0;

        createNotificationChannel(context);

        try {
            int colorRes = getEventColor(colorIndex);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(stripNote(note))
                    .setColor(ContextCompat.getColor(context, colorRes))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY_SILENT);

            NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.silent_notifications_title))
                    .setContentText(context.getString(R.string.silent_notifications_content_text))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setGroup(GROUP_KEY_SILENT)
                    .setGroupSummary(true)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "doWork Error: No permission");
                return Result.failure();
            }
            notificationManager.notify(notificationId, builder.build());
            notificationManager.notify(SUMMARY_ID, summaryBuilder.build());

            Log.d(TAG, "doWork: Success");
            return Result.success();
        } catch (Exception e) {
            Log.d(TAG, "doWork Error: " + e);
            return Result.failure();
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.silent_notifications_name);
            String description = context.getString(R.string.silent_notifications_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private String stripNote(String note) {
        if (note == null) {
            return "";
        }
        // Basic stripping: trim and limit length for notification display if needed
        String stripped = note.trim();
        if (stripped.length() > 100) {
            stripped = stripped.substring(0, 97) + "...";
        }
        return stripped;
    }

    private int getEventColor(int colorIndex) {
        return switch (colorIndex) {
            case 1 -> R.color.pick_sky_blue;
            case 2 -> R.color.pick_green;
            case 3 -> R.color.pick_yellow;
            case 4 -> R.color.pick_orange;
            case 5 -> R.color.error;
            case 6 -> R.color.pick_blue;
            case 7 -> R.color.pick_purple;
            default -> R.color.gray_new;
        };
    }

}
