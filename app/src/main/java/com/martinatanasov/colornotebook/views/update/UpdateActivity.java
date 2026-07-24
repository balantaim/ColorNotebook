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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.dialog_views.ApplyColor;
import com.martinatanasov.colornotebook.dialog_views.ApplyPriority;
import com.martinatanasov.colornotebook.dialog_views.PriorityDialog;
import com.martinatanasov.colornotebook.dialog_views.SelectColor;
import com.martinatanasov.colornotebook.dto.UserPermission;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.utils.AppSettings;
import com.martinatanasov.colornotebook.utils.ConvertTimeToTxt;
import com.martinatanasov.colornotebook.utils.EventValidator;
import com.martinatanasov.colornotebook.utils.ScreenManager;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.SilentNotificationWorker;
import com.martinatanasov.colornotebook.utils.events.VibrationUtil;
import com.martinatanasov.colornotebook.viewmodels.UpdateViewModel;
import com.martinatanasov.colornotebook.views.main.MainActivity;
import com.martinatanasov.colornotebook.views.map.MapActivity;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UpdateActivity extends AppCompatActivity implements ApplyColor, ApplyPriority, AppSettings, EventValidator, EasyPermissions.PermissionCallbacks {

    EditText eventTitle, eventLocation, eventInput;
    Button btnUpdate, btnDelete;
    TextView advOptions, dateStart, dateEnd, timeStart, timeEnd, eventColor, priority, createdDate, modifiedDate;
    LinearLayout expandableLayout;
    CardView cardView;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    SwitchCompat allDaySw, soundNotSw, silentNotSw;
    private Calendar calendar, calendar1;
    private final ConvertTimeToTxt timeToString = new ConvertTimeToTxt();
    private UpdateViewModel viewModel;
    private SelectColor selectColor = new SelectColor();
    private VibrationUtil vibration;
    private final ActivityResultLauncher<Intent> mapActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String location = result.getData().getStringExtra("location");
                    if (location != null && !location.isEmpty()) {
                        eventLocation.setText(location);
                    }
                }
            }
    );

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load skin resource
        updateAppSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        viewModel = new ViewModelProvider(this).get(UpdateViewModel.class);

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

        //Find view resources
        initViews();

        calendar = Calendar.getInstance();
        calendar1 = Calendar.getInstance();
        //Seconds is set to 0
        calendar.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.SECOND, 0);

        initObservers();

        if (savedInstanceState == null) {
            getAndSetIntentData();
        }

        initClickListeners();

        //Click event for edit text's icon
        eventLocation.setOnTouchListener((view, motionEvent) -> locationEvent(motionEvent));
    }

    AlertDialog confirmDialog;

    private void initScreenManager() {
        new ScreenManager(findViewById(R.id.root_layout_update),
                getWindow(),
                false);
    }

    private void initObservers() {
        viewModel.title.observe(this, s -> {
            if (!eventTitle.getText().toString().equals(s)) {
                eventTitle.setText(s);
                setActionBarTitle();
            }
        });
        viewModel.location.observe(this, s -> {
            if (!eventLocation.getText().toString().equals(s)) {
                eventLocation.setText(s);
            }
        });
        viewModel.input.observe(this, s -> {
            if (!eventInput.getText().toString().equals(s)) {
                eventInput.setText(s);
            }
        });
        viewModel.colorPicker.observe(this, this::updateColorText);
        viewModel.priorityPicker.observe(this, this::updatePriorityText);
        viewModel.isAllDay.observe(this, isAllDay -> {
            allDaySw.setChecked(isAllDay);
            manageAllDaySw();
        });
        viewModel.isSoundNotification.observe(this, soundNotSw::setChecked);
        viewModel.isSilentNotification.observe(this, silentNotSw::setChecked);
        viewModel.isExpanded.observe(this, isExpanded -> {
            if (isExpanded) {
                expandableLayout.setVisibility(View.VISIBLE);
                advOptions.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_event_settings, 0, R.drawable.ic_arrow_up, 0);
            } else {
                expandableLayout.setVisibility(View.GONE);
                advOptions.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_event_settings, 0, R.drawable.ic_arrow_down, 0);
            }
        });

        viewModel.startYear.observe(this, year -> calendar.set(Calendar.YEAR, year));
        viewModel.startMonth.observe(this, month -> calendar.set(Calendar.MONTH, month));
        viewModel.startDay.observe(this, day -> calendar.set(Calendar.DATE, day));
        viewModel.startHour.observe(this, hour -> calendar.set(Calendar.HOUR_OF_DAY, hour));
        viewModel.startMinutes.observe(this, minutes -> calendar.set(Calendar.MINUTE, minutes));

        viewModel.endYear.observe(this, year -> calendar1.set(Calendar.YEAR, year));
        viewModel.endMonth.observe(this, month -> calendar1.set(Calendar.MONTH, month));
        viewModel.endDay.observe(this, day -> calendar1.set(Calendar.DATE, day));
        viewModel.endHour.observe(this, hour -> calendar1.set(Calendar.HOUR_OF_DAY, hour));
        viewModel.endMinutes.observe(this, minutes -> calendar1.set(Calendar.MINUTE, minutes));

        // Update displays
        viewModel.startYear.observe(this, v -> manageDataAndTime());
        viewModel.startMonth.observe(this, v -> manageDataAndTime());
        viewModel.startDay.observe(this, v -> manageDataAndTime());
        viewModel.startHour.observe(this, v -> manageDataAndTime());
        viewModel.startMinutes.observe(this, v -> manageDataAndTime());
        viewModel.endYear.observe(this, v -> manageDataAndTime());
        viewModel.endMonth.observe(this, v -> manageDataAndTime());
        viewModel.endDay.observe(this, v -> manageDataAndTime());
        viewModel.endHour.observe(this, v -> manageDataAndTime());
        viewModel.endMinutes.observe(this, v -> manageDataAndTime());
        viewModel.createdDate.observe(this, v -> manageDataAndTime());

        viewModel.eventUpdatedEvent.observe(this, updated -> {
            if (updated) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        viewModel.eventDeletedEvent.observe(this, deleted -> {
            if (deleted) {
                finish();
            }
        });

        viewModel.isDeleteDialogShowing.observe(this, isShowing -> {
            if (isShowing) {
                if (confirmDialog == null || !confirmDialog.isShowing()) {
                    showDeleteDialog();
                }
            } else {
                if (confirmDialog != null && confirmDialog.isShowing()) {
                    confirmDialog.dismiss();
                }
            }
        });
    }

    private void initClickListeners() {
        btnUpdate.setOnClickListener(v -> onUpdateBtn());
        btnDelete.setOnClickListener(v -> confirmDialog());
        advOptions.setOnClickListener(view -> viewModel.toggleExpanded());
        dateStart.setOnClickListener(view -> setStartDate());
        timeStart.setOnClickListener(view -> setStartTime());
        dateEnd.setOnClickListener(view -> setEndDate());
        timeEnd.setOnClickListener(view -> setEndTime());
        eventColor.setOnClickListener(view -> selectColor());
        priority.setOnClickListener(v -> managePriority());
        allDaySw.setOnClickListener(view -> {
            viewModel.isAllDay.setValue(allDaySw.isChecked());
        });
        soundNotSw.setOnClickListener(view -> {
            boolean checked = soundNotSw.isChecked();
            viewModel.isSoundNotification.setValue(checked);
            if (checked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    managePermissionForNotifications();
                }
            }
        });
        silentNotSw.setOnClickListener(view -> {
            boolean checked = silentNotSw.isChecked();
            viewModel.isSilentNotification.setValue(checked);
            if (checked) {
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

                mapActivityLauncher.launch(new Intent(this, MapActivity.class));
                return true;
            }
        }
        return false;
    }

    //Set actionbar title after getAndSetIntentData method
    private void setActionBarTitle() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(eventTitle.getText().toString());
    }

    private void managePriority() {
        String tag = "PriorityDialog";
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            PriorityDialog priorityDialog = PriorityDialog.newInstance(viewModel.priorityPicker.getValue());
            priorityDialog.show(getSupportFragmentManager(), getString(R.string.dialog_priority));
        }
    }

    @Override
    public void setPriority(int status) {
        viewModel.priorityPicker.setValue(status);
    }

    private void manageAllDaySw() {
        if (allDaySw.isChecked()) {
            soundNotSw.setChecked(false);
            silentNotSw.setChecked(false);
            soundNotSw.setEnabled(false);
            silentNotSw.setEnabled(false);
            viewModel.isSoundNotification.setValue(false);
            viewModel.isSilentNotification.setValue(false);
            timeStart.setEnabled(false);
            timeEnd.setEnabled(false);
            timeStart.setAlpha(0.5f);
            timeEnd.setAlpha(0.5f);
        } else {
            // Version below Android 8 doesn't support notifications
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                soundNotSw.setEnabled(false);
                silentNotSw.setEnabled(false);
            } else {
                soundNotSw.setEnabled(true);
                silentNotSw.setEnabled(true);
            }
            timeStart.setEnabled(true);
            timeEnd.setEnabled(true);
            timeStart.setAlpha(1.0f);
            timeEnd.setAlpha(1.0f);
        }
    }

    private boolean initiateAlarm() {
        boolean checker = false;

        Calendar calendarNow = Calendar.getInstance();
        if (calendarNow.compareTo(calendar) < 0) {
            //Toast.makeText(this, "Time is valid", Toast.LENGTH_SHORT).show();
            Log.d("ALARM", "Time is valid");
            checker = true;
            AlarmEvent alarm = new AlarmEvent(this);
            alarm.setUpAlarm(viewModel.id.getValue(),
                    eventTitle.getText().toString(),
                    eventInput.getText().toString(),
                    calendar,
                    viewModel.priorityPicker.getValue(),
                    viewModel.colorPicker.getValue());
        }

        //Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
        Log.d("ALARM", "The alarm is set to " + checker);
        return checker;
    }

    private boolean initiateSilentNotification() {
        boolean checker = false;
        Calendar calendarNow = Calendar.getInstance();
        if (calendarNow.compareTo(calendar) < 0) {
            long delay = calendar.getTimeInMillis() - calendarNow.getTimeInMillis();
            SilentNotificationWorker.scheduleSilentNotification(
                    this,
                    viewModel.id.getValue(),
                    eventTitle.getText().toString(),
                    eventInput.getText().toString(),
                    viewModel.colorPicker.getValue(),
                    viewModel.priorityPicker.getValue(),
                    delay
            );
            checker = true;
        }
        return checker;
    }

    private void onUpdateBtn() {
        viewModel.title.setValue(eventTitle.getText().toString());
        viewModel.location.setValue(eventLocation.getText().toString());
        viewModel.input.setValue(eventInput.getText().toString());

        boolean checkAlarmState = true;
        if (isEventTitleValid(eventTitle.getText().toString())) {
            if (Boolean.TRUE.equals(viewModel.isSoundNotification.getValue())) {
                checkAlarmState = initiateAlarm();
                if (!checkAlarmState) {
                    // Time is not valid, cancel existing alarm
                    AlarmEvent alarm = new AlarmEvent(this);
                    alarm.cancelAlarm(viewModel.id.getValue());
                }
            } else {
                // Cancel existing alarm if sound notification is turned off
                AlarmEvent alarm = new AlarmEvent(this);
                alarm.cancelAlarm(viewModel.id.getValue());
            }

            if (Boolean.TRUE.equals(viewModel.isSilentNotification.getValue())) {
                boolean checkSilentState = initiateSilentNotification();
                if (!checkSilentState) {
                    SilentNotificationWorker.cancelSilentNotification(this, viewModel.id.getValue());
                }
            } else {
                SilentNotificationWorker.cancelSilentNotification(this, viewModel.id.getValue());
            }

            if (!checkAlarmState) {
                viewModel.isSoundNotification.setValue(false);
            }
            //Update DB
            viewModel.updateEvent();
        } else {
            Toast.makeText(this, R.string.event_title_empty, Toast.LENGTH_SHORT).show();
        }
    }

    public void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")) {
            // Getting data from Intent
            viewModel.id.setValue(getIntent().getStringExtra("id"));
            viewModel.title.setValue(getIntent().getStringExtra("title"));
            viewModel.location.setValue(getIntent().getStringExtra("location"));
            viewModel.input.setValue(getIntent().getStringExtra("input"));

            setActionBarTitle();

            viewModel.colorPicker.setValue(getIntent().getIntExtra("color", 0));
            viewModel.priorityPicker.setValue(getIntent().getIntExtra("avatar", 0));

            viewModel.startYear.setValue(getIntent().getIntExtra("start_year", 0));
            viewModel.startMonth.setValue(getIntent().getIntExtra("start_mouth", 0));
            viewModel.startDay.setValue(getIntent().getIntExtra("start_day", 0));
            viewModel.startHour.setValue(getIntent().getIntExtra("start_hour", 0));
            viewModel.startMinutes.setValue(getIntent().getIntExtra("start_minutes", 0));

            viewModel.endYear.setValue(getIntent().getIntExtra("end_year", 0));
            viewModel.endMonth.setValue(getIntent().getIntExtra("end_month", 0));
            viewModel.endDay.setValue(getIntent().getIntExtra("end_day", 0));
            viewModel.endHour.setValue(getIntent().getIntExtra("end_hour", 0));
            viewModel.endMinutes.setValue(getIntent().getIntExtra("end_minutes", 0));

            viewModel.createdDate.setValue(getIntent().getLongExtra("created_date", 0));
            viewModel.modifiedDate.setValue(getIntent().getLongExtra("modified_date", 0));

            viewModel.isAllDay.setValue(getIntent().getIntExtra("all_day", 0) == 1);
            viewModel.isSoundNotification.setValue(getIntent().getIntExtra("sound_notifications", 0) == 1);
            viewModel.isSilentNotification.setValue(getIntent().getIntExtra("silent_notifications", 0) == 1);

            manageAllDaySw();
            manageDataAndTime();
        } else {
            Toast.makeText(this, R.string.toast_no_data, Toast.LENGTH_SHORT).show();
        }
        getPriorityString();
    }

    private void manageDataAndTime() {
        //Import data to calendar 1 and 2
        boolean is24format = DateFormat.is24HourFormat(this);

        CharSequence charSequence = DateFormat.format("MMM d, yyyy", calendar);
        CharSequence charSequence1 = DateFormat.format("MMM d, yyyy", calendar1);
        dateStart.setText(charSequence);
        dateEnd.setText(charSequence1);
        //Set color picker text
        updateColorText(viewModel.colorPicker.getValue());
        //Update create/modified date
        createdDate.setText(timestampToDate(viewModel.createdDate.getValue(), is24format));
        modifiedDate.setText(timestampToDate(viewModel.modifiedDate.getValue(), is24format));
        if (is24format) {
            timeStart.setText(timeToString.intToTxtTime(viewModel.startHour.getValue(), viewModel.startMinutes.getValue()));
            timeEnd.setText(timeToString.intToTxtTime(viewModel.endHour.getValue(), viewModel.endMinutes.getValue()));
        } else {
            CharSequence timeSequence = DateFormat.format("hh:mm aa", calendar);
            CharSequence timeSequence1 = DateFormat.format("hh:mm aa", calendar1);
            timeStart.setText(timeSequence);
            timeEnd.setText(timeSequence1);
        }
    }

    private void selectColor() {
        String tag = String.valueOf(R.string.pickColor);
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            if (selectColor == null) {
                selectColor = new SelectColor();
            }
            selectColor.colorInit(viewModel.colorPicker.getValue());
            selectColor.show(getSupportFragmentManager(), tag);
        }
    }

    private void getPriorityString() {
        Integer priorityVal = viewModel.priorityPicker.getValue();
        if (priorityVal == null) {
            return;
        }
        switch (priorityVal) {
            case 1 -> priority.setText(R.string.set_regular);
            case 2 -> priority.setText(R.string.set_unimportant);
            default -> priority.setText(R.string.set_important);
        }
    }

    private void setEndDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            viewModel.endYear.setValue(year);
            viewModel.endMonth.setValue(month);
            viewModel.endDay.setValue(day);
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener,
                viewModel.endYear.getValue(), viewModel.endMonth.getValue(), viewModel.endDay.getValue());
        datePickerDialog.show();
    }

    private void setStartDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            viewModel.startYear.setValue(year);
            viewModel.startMonth.setValue(month);
            viewModel.startDay.setValue(day);
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener,
                viewModel.startYear.getValue(), viewModel.startMonth.getValue(), viewModel.startDay.getValue());
        datePickerDialog.show();
    }

    private void setEndTime() {
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, Hour, Minutes) -> {
            viewModel.endHour.setValue(Hour);
            viewModel.endMinutes.setValue(Minutes);
        };
        timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                viewModel.endHour.getValue(), viewModel.endMinutes.getValue(), is24format);
        timePickerDialog.show();
    }

    private void setStartTime() {
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, Hour, Minutes) -> {
            viewModel.startHour.setValue(Hour);
            viewModel.startMinutes.setValue(Minutes);
        };
        timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                viewModel.startHour.getValue(), viewModel.startMinutes.getValue(), is24format);
        timePickerDialog.show();
    }

    void confirmDialog() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        //Add vibration effect
        vibration.vibrate();

        viewModel.isDeleteDialogShowing.setValue(true);
    }

    private void showDeleteDialog() {
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
            viewModel.isDeleteDialogShowing.setValue(false);
            viewModel.deleteEvent();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            viewModel.isDeleteDialogShowing.setValue(false);
        });
        builder.setOnCancelListener(dialog -> {
            viewModel.isDeleteDialogShowing.setValue(false);
        });
        confirmDialog = builder.create();
        confirmDialog.show();
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
        viewModel.toggleExpanded();
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
        if (Boolean.TRUE.equals(viewModel.isExpanded.getValue())) {
            expandView();
        }
    }

    public void updateOnConfigurationChanges() {
        updateColorText(viewModel.colorPicker.getValue());
        updatePriorityText(viewModel.priorityPicker.getValue());
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
        viewModel.isAllDay.setValue(allDaySw.isChecked());
        viewModel.isSoundNotification.setValue(soundNotSw.isChecked());
        viewModel.isSilentNotification.setValue(silentNotSw.isChecked());
    }

    public void setSwValues() {
        allDaySw.setChecked(Boolean.TRUE.equals(viewModel.isAllDay.getValue()));
        soundNotSw.setChecked(Boolean.TRUE.equals(viewModel.isSoundNotification.getValue()));
        silentNotSw.setChecked(Boolean.TRUE.equals(viewModel.isSilentNotification.getValue()));
    }

    private void updateColorText(int color) {
        String[] stringArray = getResources().getStringArray(R.array.color_picker_array);
        eventColor.setText(stringArray[color]);
    }

    @Override
    public void setColor(int color) {
        viewModel.colorPicker.setValue(color);
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
                viewModel.isSoundNotification.setValue(false);
                viewModel.isSilentNotification.setValue(false);
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
        vibration = new VibrationUtil(this);
    }

    //Save Instance when you rotate the device or use recreate
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        viewModel.title.setValue(eventTitle.getText().toString());
        viewModel.location.setValue(eventLocation.getText().toString());
        viewModel.input.setValue(eventInput.getText().toString());
        super.onSaveInstanceState(outState);
    }

    //Restore the instance settings
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (datePickerDialog != null && datePickerDialog.isShowing()) {
            datePickerDialog.dismiss();
        }
        if (timePickerDialog != null && timePickerDialog.isShowing()) {
            timePickerDialog.dismiss();
        }
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        }
        super.onDestroy();
    }

}
