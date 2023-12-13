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

package com.martinatanasov.colornotebook.tools.events;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.martinatanasov.colornotebook.R;

public class SilentNotificationWorker extends Worker {

    public SilentNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("NotificationWorker", "doWork: Start");
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "silent_notifications_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("title1 TEST")
                    .setContentText("text context1 TEST")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getApplicationContext());
            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("NotificationWorker", "doWork Error: No permission");
                return Result.failure();
            }
            notificationManager.notify(123, builder.build());

            Log.d("NotificationWorker", "doWork: Success");
            return Result.success();
        } catch (Exception e){
            Log.d("NotificationWorker", "doWork Error: " + e);
            return Result.failure();
        }
    }
}
