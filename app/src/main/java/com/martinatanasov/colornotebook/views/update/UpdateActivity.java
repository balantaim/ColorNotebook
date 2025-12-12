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

package com.martinatanasov.colornotebook.views.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controllers.UpdateActivityController;
import com.martinatanasov.colornotebook.dialog_views.ApplyColor;
import com.martinatanasov.colornotebook.dialog_views.PriorityDialog;
import com.martinatanasov.colornotebook.dialog_views.SelectColor;
import com.martinatanasov.colornotebook.dto.UpdateEvent;
import com.martinatanasov.colornotebook.dto.UserPermission;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.utils.ActionBarIconSetter;
import com.martinatanasov.colornotebook.utils.AppSettings;
import com.martinatanasov.colornotebook.utils.ConvertTimeToTxt;
import com.martinatanasov.colornotebook.utils.EventValidator;
import com.martinatanasov.colornotebook.utils.ScreenManager;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.VibrationEvent;
import com.martinatanasov.colornotebook.views.main.MainActivity;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UpdateActivity extends AppCompatActivity implements ApplyColor, AppSettings, EventValidator, EasyPermissions.PermissionCallbacks {

    EditText eventTitle, eventLocation, eventInput;
    Button btnUpdate, btnDelete;
    TextView advOptions, dateStart, dateEnd, timeStart, timeEnd, eventColor, priority, createdDate, modifiedDate;
    LinearLayout expandableLayout;
    CardView cardView;
    DatePickerDialog datePickerDialog;
    SwitchCompat allDaySw, soundNotSw, silentNotSw;
    private String id;
    //private AlarmManager alarmManager;
    //private PendingIntent pendingIntent;
    private boolean isExpanded = false;
    private Calendar calendar, calendar1;
    private final ConvertTimeToTxt timeToString = new ConvertTimeToTxt();
    private UpdateActivityController controller;
    private int YEAR = 0, MONTH = 0, DAY = 0, HOUR = 0, MINUTES = 0;
    private int YEAR2 = 0, MONTH2 = 0, DAY2 = 0, HOUR2 = 0, MINUTES2 = 0;
    private int dayEventBool = 0, soundNotificationBool = 0, silentNotificationBool = 0, colorPicker = 0, priorityPicker = 0;
    private long eventCreatedDate = 0, eventModifiedDate = 0;
    private SelectColor selectColor = new SelectColor();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load skin resource
        updateAppSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        //hide Status Bar
        initScreenManager();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_update);
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);// show back arrow
//            Drawable arrow = getResources().getDrawable(R.drawable.ic_custom_arrow);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                arrow.setTint(getResources().getColor(R.color.white, getTheme())); // set color
//            }
//            getSupportActionBar().setHomeAsUpIndicator(arrow);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_custom_arrow);
        }

        //Change Back arrow button
//        changeArrowBackBtn();

        //Find view resources
        initViews();

        calendar = Calendar.getInstance();
        calendar1 = Calendar.getInstance();
        //Seconds is set to 0
        calendar.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.SECOND, 0);
        //getAndSetIntentData();

        initClickListeners();


        //Click event for edit text's icon
        eventLocation.setOnTouchListener((view, motionEvent) -> locationEvent(motionEvent));
    }

    private void initScreenManager() {
        new ScreenManager(findViewById(R.id.root_layout_update),
                getWindow(),
                false);
    }

    private void initClickListeners() {
        btnUpdate.setOnClickListener(v -> onUpdateBtn());
        btnDelete.setOnClickListener(v -> confirmDialog());
        advOptions.setOnClickListener(view -> expandView());
        dateStart.setOnClickListener(view -> setStartDate());
        timeStart.setOnClickListener(view -> setStartTime());
        dateEnd.setOnClickListener(view -> setEndDate());
        timeEnd.setOnClickListener(view -> setEndTime());
        eventColor.setOnClickListener(view -> selectColor());
        priority.setOnClickListener(v -> managePriority());
        allDaySw.setOnClickListener(view -> dayEventBool = allDaySw.isChecked() ? 1 : 0);
        soundNotSw.setOnClickListener(view -> {
            soundNotificationBool = soundNotSw.isChecked() ? 1 : 0;
            if (soundNotificationBool == 1) {
                //Check for alarm permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    managePermissionForNotifications();
                }
            }
        });
        silentNotSw.setOnClickListener(view -> {
            silentNotificationBool = silentNotSw.isChecked() ? 1 : 0;
            if (silentNotificationBool == 1) {
                //Check for alarm permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    managePermissionForNotifications();
                }
            }
        });
    }

    private boolean locationEvent(MotionEvent motionEvent) {
        final int DRAWABLE_RIGHT = 2;

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (motionEvent.getRawX() >= (eventLocation.getRight() - eventLocation.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    //Set actionbar title after getAndSetIntentData method
    private void setActionBarTitle() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(eventTitle.getText().toString());
    }

    private void changeArrowBackBtn() {
        ActionBarIconSetter actionBarIconSetter = new ActionBarIconSetter();
        actionBarIconSetter.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));
    }

    private void managePriority() {
        PriorityDialog priorityDialog = new PriorityDialog(getApplicationContext());
        priorityDialog.setPriority(this, priorityPicker);
        priorityDialog.setDialogResult(status -> {
            priorityPicker = status;
            getPriorityString();
        });
    }
//    private boolean initiateAlarm(){
//        boolean checker = false;
//
//        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        intent.putExtra("id", id);
//        intent.putExtra("title", eventTitle.getText().toString());
//        intent.putExtra("node", eventInput.getText().toString());
//
//        int requestCode = Integer.parseInt(id);
//        pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        Calendar calendarNow = Calendar.getInstance();

    /// /        if (pendingIntent != null && alarmManager != null) {
    /// /            alarmManager.cancel(pendingIntent);
    /// /            Log.d("ALARM", "The alarm is canceled");
    /// /        }
//        if (calendarNow.compareTo(calendar) < 0){
//            //Toast.makeText(this, "Time is valid", Toast.LENGTH_SHORT).show();
//            Log.d("ALARM", "Time is valid");
//            checker = true;
//        }
//
//        boolean exactRequired = false;
//        //Set alarm repeating
//        if(exactRequired) {
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//                    calendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
//                    pendingIntent);
//        } else {
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                    calendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
//                    pendingIntent);
//        }
//
//        //Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
//        Log.d("ALARM", "The alarm is set to " + checker);
//        return checker;
//    }
    private boolean initiateAlarm() {
        boolean checker = false;

        Calendar calendarNow = Calendar.getInstance();
        if (calendarNow.compareTo(calendar) < 0) {
            //Toast.makeText(this, "Time is valid", Toast.LENGTH_SHORT).show();
            Log.d("ALARM", "Time is valid");
            checker = true;
            AlarmEvent alarm = new AlarmEvent(this);
            alarm.setUpAlarm(id,
                    eventTitle.getText().toString(),
                    eventInput.getText().toString(),
                    calendar,
                    priorityPicker);
        }

        //Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
        Log.d("ALARM", "The alarm is set to " + checker);
        return checker;
    }

    private void onUpdateBtn() {
        boolean checkAlarmState = true;
        if (isEventTitleValid(eventTitle.getText().toString())) {
            if (soundNotificationBool == 1) {
                checkAlarmState = initiateAlarm();
            }
            if (!checkAlarmState) {
                //soundNotSw.setChecked(false);
                soundNotificationBool = 0;
                //return;
            }
            //Update DB
            long timestamp = Calendar.getInstance().getTimeInMillis();
            controller.updateUserEvent(new UpdateEvent(
                    id,
                    eventTitle.getText().toString(),
                    eventLocation.getText().toString(),
                    eventInput.getText().toString(),
                    colorPicker,
                    priorityPicker,
                    YEAR, MONTH, DAY, HOUR, MINUTES,
                    YEAR2, MONTH2, DAY2, HOUR2, MINUTES2,
                    eventCreatedDate,
                    timestamp,
                    dayEventBool,
                    soundNotificationBool,
                    silentNotificationBool
            ));
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.event_title_empty, Toast.LENGTH_SHORT).show();
        }
    }

    public void getAndSetIntentData() {
        String title, location, input;
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")) {
            // Getting data from Intent
            if (id == null) {
                //Log.d("update", "getAndSetIntentData: ID = null");
                id = getIntent().getStringExtra("id");
                title = getIntent().getStringExtra("title");
                location = getIntent().getStringExtra("location");
                input = getIntent().getStringExtra("input");
                eventTitle.setText(title);
                eventLocation.setText(location);
                eventInput.setText(input);

                setActionBarTitle();

                //colorPicker = Integer.parseInt(getIntent().getStringExtra("color"));
                colorPicker = getIntent().getIntExtra("color", 0);
                priorityPicker = getIntent().getIntExtra("avatar", 0);
                YEAR = getIntent().getIntExtra("start_year", 0);
                MONTH = getIntent().getIntExtra("start_mouth", 0);
                DAY = getIntent().getIntExtra("start_day", 0);
                HOUR = getIntent().getIntExtra("start_hour", 0);
                MINUTES = getIntent().getIntExtra("start_minutes", 0);
                YEAR2 = getIntent().getIntExtra("end_year", 0);
                MONTH2 = getIntent().getIntExtra("end_month", 0);
                DAY2 = getIntent().getIntExtra("end_day", 0);
                HOUR2 = getIntent().getIntExtra("end_hour", 0);
                MINUTES2 = getIntent().getIntExtra("end_minutes", 0);

                eventCreatedDate = getIntent().getLongExtra("created_date", 0);
                eventModifiedDate = getIntent().getLongExtra("modified_date", 0);

                dayEventBool = getIntent().getIntExtra("all_day", 0);
                soundNotificationBool = getIntent().getIntExtra("sound_notifications", 0);
                silentNotificationBool = getIntent().getIntExtra("silent_notifications", 0);
                //Setting Intent data
                allDaySw.setChecked(dayEventBool == 1);
                soundNotSw.setChecked(soundNotificationBool == 1);
                silentNotSw.setChecked(silentNotificationBool == 1);
            }
            //Log.d("update", "getAndSetIntentData: ID is valid");
            //Update date and time data
            manageDataAndTime();
        } else {
            Toast.makeText(this, R.string.toast_noData, Toast.LENGTH_SHORT).show();
        }
        getPriorityString();
    }

    private void manageDataAndTime() {
        //Import data to calendar 1 and 2
        boolean is24format = DateFormat.is24HourFormat(this);
        calendar.set(Calendar.YEAR, YEAR);
        calendar.set(Calendar.MONTH, MONTH);
        calendar.set(Calendar.DATE, DAY);
        calendar.set(Calendar.HOUR_OF_DAY, HOUR);
        calendar.set(Calendar.MINUTE, MINUTES);
        calendar1.set(Calendar.YEAR, YEAR2);
        calendar1.set(Calendar.MONTH, MONTH2);
        calendar1.set(Calendar.DATE, DAY2);
        calendar1.set(Calendar.HOUR_OF_DAY, HOUR2);
        calendar1.set(Calendar.MINUTE, MINUTES2);

        CharSequence charSequence = DateFormat.format("MMM d, yyyy", calendar);
        CharSequence charSequence1 = DateFormat.format("MMM d, yyyy", calendar1);
        dateStart.setText(charSequence);
        dateEnd.setText(charSequence1);
        //Set color picker text
        updateColorText(colorPicker);
        //Update create/modified date
        createdDate.setText(timestampToDate(eventCreatedDate, is24format));
        modifiedDate.setText(timestampToDate(eventModifiedDate, is24format));
        if (is24format) {
            timeStart.setText(timeToString.intToTxtTime(HOUR, MINUTES));
            timeEnd.setText(timeToString.intToTxtTime(HOUR2, MINUTES2));
        } else {
            CharSequence timeSequence = DateFormat.format("hh:mm aa", calendar);
            CharSequence timeSequence1 = DateFormat.format("hh:mm aa", calendar1);
            timeStart.setText(timeSequence);
            timeEnd.setText(timeSequence1);
        }
    }

    private void selectColor() {
        if (selectColor == null) {
            selectColor = new SelectColor();
        }
//        SelectColor selectColor = new SelectColor();
        selectColor.colorInit(colorPicker);
        selectColor.show(getSupportFragmentManager(), String.valueOf(R.string.pickColor));
    }

    private void getPriorityString() {
        switch (priorityPicker) {
            case 1 -> priority.setText(R.string.set_regular);
            case 2 -> priority.setText(R.string.set_unimportant);
            default -> priority.setText(R.string.set_important);
        }
    }

    private void setEndDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            calendar1.set(Calendar.YEAR, year);
            calendar1.set(Calendar.MONTH, month);
            calendar1.set(Calendar.DATE, day);
            CharSequence charSequence = DateFormat.format("MMM d, yyyy", calendar1);
            dateEnd.setText(charSequence);
            //save data for next reuse
            YEAR2 = year;
            MONTH2 = month;
            DAY2 = day;
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener, YEAR2, MONTH2, DAY2);
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setStartDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DATE, day);
            CharSequence charSequence = DateFormat.format("MMM d, yyyy", calendar);
            dateStart.setText(charSequence);
            //save data for next reuse
            YEAR = year;
            MONTH = month;
            DAY = day;
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener, YEAR, MONTH, DAY);
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setEndTime() {
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, Hour, Minutes) -> {
            calendar1.set(Calendar.HOUR_OF_DAY, Hour);
            calendar1.set(Calendar.MINUTE, Minutes);
            CharSequence charSequence = DateFormat.format("hh:mm aa", calendar1);
            if (is24format) {
                timeEnd.setText(timeToString.intToTxtTime(Hour, Minutes));
            } else {
                timeEnd.setText(charSequence);
            }
            HOUR2 = Hour;
            MINUTES2 = Minutes;
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, HOUR2, MINUTES2, is24format);
        //timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void setStartTime() {
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, Hour, Minutes) -> {
            calendar.set(Calendar.HOUR_OF_DAY, Hour);
            calendar.set(Calendar.MINUTE, Minutes);
            CharSequence charSequence = DateFormat.format("hh:mm aa", calendar);
            if (is24format) {
                timeStart.setText(timeToString.intToTxtTime(Hour, Minutes));
            } else {
                timeStart.setText(charSequence);
            }
            HOUR = Hour;
            MINUTES = Minutes;
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, HOUR, MINUTES, is24format);
        //timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    void confirmDialog() {
        //Add vibration effect
        VibrationEvent vibration = new VibrationEvent();
        vibration.startEffect(this);
        //Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Format text character max size
        String displayTitle = eventTitle.getText().toString();
        if (displayTitle.length() > 9) {
            displayTitle = getString(R.string.delete) + " " + displayTitle.substring(0, 9) + "..";
        } else {
            displayTitle = getString(R.string.delete) + " " + displayTitle;
        }
        builder.setTitle(displayTitle);
        builder.setMessage(getString(R.string.alert_dialog_message_sure_to_dell));
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            controller.deleteCurrentEvent(id);
            finish();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {

        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuUpdate) {
            onUpdateBtn();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void expandView() {
        if (expandableLayout.getVisibility() == View.GONE) {
            isExpanded = true;
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            expandableLayout.setVisibility(View.VISIBLE);
            advOptions.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
        } else {
            isExpanded = false;
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            expandableLayout.setVisibility(View.GONE);
            advOptions.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String timestampToDate(long timestamp, boolean is24format) {
        Date date = new Date(timestamp);
        Format formatter;
        if (is24format) {
            formatter = new SimpleDateFormat("MMM d, yyyy  HH:mm");
        } else {
            //formatter = new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss aa");
            formatter = new SimpleDateFormat("MMM d, yyyy  hh:mm aa");
        }
        return formatter.format(date);
    }

    public void checkIfCardIsExpanded() {
        if (isExpanded) {
            expandView();
        }
    }

    public void updateOnConfigurationChanges() {
//        Log.d("ADD", "update On ConfigurationChanges Color: " + colorPicker);
//        Log.d("ADD", "update On ConfigurationChanges Priority: " + priorityPicker);
        updateColorText(colorPicker);
        updatePriorityText(priorityPicker);
    }

    private void updatePriorityText(int value) {
        if (value == 0) {
            priority.setText(R.string.set_important);
        } else if (value == 1) {
            priority.setText(R.string.set_regular);
        } else {
            priority.setText(R.string.set_unimportant);
        }
    }

    public void updateSwValues() {
        dayEventBool = allDaySw.isChecked() ? 1 : 0;
        soundNotificationBool = soundNotSw.isChecked() ? 1 : 0;
        silentNotificationBool = silentNotSw.isChecked() ? 1 : 0;
    }

    private void updateColorText(int color) {
        String[] stringArray = getResources().getStringArray(R.array.color_picker_array);
        eventColor.setText(stringArray[color]);
    }

    @Override
    public void setColor(int color) {
        colorPicker = color;
        updateColorText(color);
    }

    @Override
    public void updateAppSettings() {
        PreferencesManager preferencesManager = new PreferencesManager(this);
        int theme = preferencesManager.getCurrentTheme();
        switch (theme) {
            case 1 -> setTheme(R.style.Theme_BlueColorNotebook);
            case 2 -> setTheme(R.style.Theme_DarkColorNotebook);
            default -> setTheme(R.style.Theme_DefaultColorNotebook);
        }
    }

    //Permission management
    @AfterPermissionGranted(101)
    private void managePermissionForNotifications() {
        String[] permissionList;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionList = new String[]{Manifest.permission.POST_NOTIFICATIONS
                    //, Manifest.permission.READ_EXTERNAL_STORAGE //Example for multiple permissions
            };
            if (EasyPermissions.hasPermissions(this, permissionList)) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.permission_reason),
                        UserPermission.NOTIFICATION_PERMISSION.getValue(),
                        permissionList);
                //Reset switchers
                soundNotSw.setChecked(false);
                silentNotSw.setChecked(false);
                soundNotificationBool = 0;
                silentNotificationBool = 0;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == UserPermission.NOTIFICATION_PERMISSION.getValue()) {
            //We have permission code: 101
            Toast.makeText(this, getString(R.string.permission_reason), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Log.d("Permission", "onActivityResult: returned from settings");
        }
    }

    private void initViews() {
        advOptions = findViewById(R.id.advOptions2);
        dateStart = findViewById(R.id.startDate2);
        dateEnd = findViewById(R.id.endDate2);
        timeStart = findViewById(R.id.startTime2);
        timeEnd = findViewById(R.id.endTime2);
        cardView = findViewById(R.id.cardView2);
        expandableLayout = findViewById(R.id.expandableLayout2);
        eventTitle = findViewById(R.id.eventTitle2);
        eventLocation = findViewById(R.id.eventLocation2);
        eventInput = findViewById(R.id.eventInput2);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        allDaySw = findViewById(R.id.allDaySw2);
        soundNotSw = findViewById(R.id.soundNotSw2);
        silentNotSw = findViewById(R.id.silentNotificationSw2);
        eventColor = findViewById(R.id.eventColor2);
        priority = findViewById(R.id.priority2);
        createdDate = findViewById(R.id.created);
        modifiedDate = findViewById(R.id.modified);
        controller = new UpdateActivityController(this);
    }

    //Save Instance when you rotate the device or use recreate
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //Set up Start/End time
        outState.putInt("key_year", YEAR);
        outState.putInt("key_year2", YEAR2);
        outState.putInt("key_month", MONTH);
        outState.putInt("key_month2", MONTH2);
        outState.putInt("key_day", DAY);
        outState.putInt("key_day2", DAY2);
        outState.putInt("key_hour", HOUR);
        outState.putInt("key_hour2", HOUR2);
        outState.putInt("key_minutes", MINUTES);
        outState.putInt("key_minutes2", MINUTES2);

        outState.putBoolean("key_expanded_card", isExpanded);
        //outState.putInt("key_day_event", dayEventBool);
        //outState.putInt("key_sound_notification", soundNotificationBool);
        //outState.putInt("key_silent_notification", silentNotificationBool);
        outState.putInt("key_color", colorPicker);
        outState.putInt("key_priority", priorityPicker);
        outState.putLong("key_created", eventCreatedDate);
        outState.putLong("key_modified", eventModifiedDate);
        outState.putString("key_id", id);

        super.onSaveInstanceState(outState);
    }

    //Restore the instance settings
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        //Set up Start/End time
        YEAR = savedInstanceState.getInt("key_year", 0);
        YEAR2 = savedInstanceState.getInt("key_year2", 0);
        MONTH = savedInstanceState.getInt("key_month", 0);
        MONTH2 = savedInstanceState.getInt("key_month2", 0);
        DAY = savedInstanceState.getInt("key_day", 0);
        DAY2 = savedInstanceState.getInt("key_day2", 0);
        HOUR = savedInstanceState.getInt("key_hour", 0);
        HOUR2 = savedInstanceState.getInt("key_hour2", 0);
        MINUTES = savedInstanceState.getInt("key_minutes", 0);
        MINUTES2 = savedInstanceState.getInt("key_minutes2", 0);

        isExpanded = savedInstanceState.getBoolean("key_expanded_card", false);
        //dayEventBool = savedInstanceState.getInt("key_day_event", 0);
        //soundNotificationBool = savedInstanceState.getInt("key_sound_notification", 0);
        //silentNotificationBool = savedInstanceState.getInt("key_silent_notification", 0);
        colorPicker = savedInstanceState.getInt("key_color");
        priorityPicker = savedInstanceState.getInt("key_priority");
        eventCreatedDate = savedInstanceState.getLong("key_created");
        eventModifiedDate = savedInstanceState.getLong("key_modified");
        id = savedInstanceState.getString("key_id");

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(selectColor.is){
//            selectColor.dismiss();
//            selectColor = null;
//        }
    }

}