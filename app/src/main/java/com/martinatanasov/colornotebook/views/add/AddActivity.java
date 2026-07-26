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

package com.martinatanasov.colornotebook.views.add;

import android.Manifest;
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
import com.martinatanasov.colornotebook.utils.ActionBarIconSetter;
import com.martinatanasov.colornotebook.utils.AppSettings;
import com.martinatanasov.colornotebook.utils.ConvertTimeToTxt;
import com.martinatanasov.colornotebook.utils.EventValidator;
import com.martinatanasov.colornotebook.utils.ScreenManager;
import com.martinatanasov.colornotebook.utils.events.AlarmEvent;
import com.martinatanasov.colornotebook.utils.events.SilentNotificationWorker;
import com.martinatanasov.colornotebook.viewmodels.AddViewModel;
import com.martinatanasov.colornotebook.views.main.MainActivity;
import com.martinatanasov.colornotebook.views.map.MapActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddActivity extends AppCompatActivity implements ApplyColor, ApplyPriority, AppSettings, EventValidator, EasyPermissions.PermissionCallbacks {

    EditText eventTitle, eventLocation, eventInput;
    Button btnAdd;
    TextView advOptions, dateStart, dateEnd, timeStart, timeEnd, eventColor, priority;
    LinearLayout expandableLayout;
    CardView cardView;
    DatePickerDialog datePickerDialog;
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
    SwitchCompat allDaySw, soundNotSw, silentNotSw;
    private final ConvertTimeToTxt timeToString = new ConvertTimeToTxt();
    private AddViewModel viewModel;
    private SelectColor selectColor = new SelectColor();
    private Calendar calendar, calendar1;
    private boolean firstTimeFocusText = true;
    TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Increase bottom area when the keyboard appears
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Load skin resource
        updateAppSettings();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        viewModel = new ViewModelProvider(this).get(AddViewModel.class);

        //hide Status Bar
        initScreenManager();

        //Find view resources
        initViews();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_add);
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

        //Focus first edit text
        if (firstTimeFocusText) {
            eventTitle.requestFocus();
            firstTimeFocusText = false;
        }

        calendar = Calendar.getInstance();
        calendar1 = Calendar.getInstance();
        //Seconds is set to 0
        calendar.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.SECOND, 0);

        initObservers();
        initClickListeners();
    }

    private void initObservers() {
        viewModel.title.observe(this, s -> {
            if (!eventTitle.getText().toString().equals(s)) {
                eventTitle.setText(s);
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

        viewModel.isStartDatePickerShowing.observe(this, isShowing -> {
            if (isShowing && (datePickerDialog == null || !datePickerDialog.isShowing())) {
                showStartDatePicker();
            }
        });
        viewModel.isEndDatePickerShowing.observe(this, isShowing -> {
            if (isShowing && (datePickerDialog == null || !datePickerDialog.isShowing())) {
                showEndDatePicker();
            }
        });
        viewModel.isStartTimePickerShowing.observe(this, isShowing -> {
            if (isShowing && (timePickerDialog == null || !timePickerDialog.isShowing())) {
                showStartTimePicker();
            }
        });
        viewModel.isEndTimePickerShowing.observe(this, isShowing -> {
            if (isShowing && (timePickerDialog == null || !timePickerDialog.isShowing())) {
                showEndTimePicker();
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

        viewModel.eventAddedEvent.observe(this, newId -> {
            if (newId != -1) {
                if (Boolean.TRUE.equals(viewModel.isSoundNotification.getValue())) {
                    initiateAlarm(String.valueOf(newId));
                }
                if (Boolean.TRUE.equals(viewModel.isSilentNotification.getValue())) {
                    initiateSilentNotification(String.valueOf(newId));
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        // Update Date/Time displays when any date/time component changes
        viewModel.startYear.observe(this, v -> manageDateAndTime());
        viewModel.startMonth.observe(this, v -> manageDateAndTime());
        viewModel.startDay.observe(this, v -> manageDateAndTime());
        viewModel.startHour.observe(this, v -> manageDateAndTime());
        viewModel.startMinutes.observe(this, v -> manageDateAndTime());
        viewModel.endYear.observe(this, v -> manageDateAndTime());
        viewModel.endMonth.observe(this, v -> manageDateAndTime());
        viewModel.endDay.observe(this, v -> manageDateAndTime());
        viewModel.endHour.observe(this, v -> manageDateAndTime());
        viewModel.endMinutes.observe(this, v -> manageDateAndTime());
    }

    private void initClickListeners() {
        btnAdd.setOnClickListener(v -> onAddBtn());
        eventLocation.setOnTouchListener((view, motionEvent) -> locationEvent(motionEvent));
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

        //Scale Text
        /*
        Scale fonts change getApplicationContext with context
        Configuration configuration = getApplicationContext().getResources().getConfiguration();
        configuration.fontScale = 3.0f;
        getApplicationContext().createConfigurationContext(configuration);
        */
    }

    private void initScreenManager() {
        new ScreenManager(findViewById(R.id.root_layout_add),
                getWindow(),
                false);
    }

    private boolean locationEvent(MotionEvent motionEvent) {
        final int DRAWABLE_RIGHT = 2;

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (motionEvent.getRawX() >= (eventLocation.getRight() - eventLocation.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                mapActivityLauncher.launch(new Intent(this, MapActivity.class));
                return true;
            }
        }
        return false;
    }

    private void changeArrowBackBtn() {
        ActionBarIconSetter actionBarIconSetter = new ActionBarIconSetter();
        actionBarIconSetter.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));
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

    private void managePriority() {
        String tag = "PriorityDialog";
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            PriorityDialog priorityDialog = PriorityDialog.newInstance(viewModel.priorityPicker.getValue());
            priorityDialog.show(getSupportFragmentManager(), tag);
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

    private boolean initiateAlarm(String id) {
        boolean checker = false;

        Calendar calendarNow = Calendar.getInstance();
        if (calendarNow.compareTo(calendar) < 0) {
            Log.d("ALARM", "Time is valid");
            checker = true;
            AlarmEvent alarm = new AlarmEvent(this);
            alarm.setUpAlarm(id,
                    eventTitle.getText().toString(),
                    eventInput.getText().toString(),
                    calendar,
                    viewModel.priorityPicker.getValue(),
                    viewModel.colorPicker.getValue());
        }

        Log.d("ALARM", "The alarm is set to " + checker);
        return checker;
    }

    private boolean initiateSilentNotification(String id) {
        boolean checker = false;
        Calendar calendarNow = Calendar.getInstance();
        if (calendarNow.compareTo(calendar) < 0) {
            long delay = calendar.getTimeInMillis() - calendarNow.getTimeInMillis();
            SilentNotificationWorker.scheduleSilentNotification(
                    this,
                    id,
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

    private void onAddBtn() {
        viewModel.title.setValue(eventTitle.getText().toString().trim());
        viewModel.location.setValue(eventLocation.getText().toString().trim());
        viewModel.input.setValue(eventInput.getText().toString().trim());

        if (eventTitle.getText().toString().length() > 1) {
            if (isEventTitleValid(eventTitle.getText().toString())) {
                viewModel.addEvent();
            }
        } else {
            Toast.makeText(this, R.string.event_title_empty, Toast.LENGTH_SHORT).show();
        }
    }
//    public static Fragment newInstance()
//    {
//        MyFragment myFragment = new MyFragment();
//        return myFragment;
//    }

    public void initAdvancedOptions() {
        manageDateAndTime();
        manageAllDaySw();
    }

    private void manageDateAndTime() {
        boolean is24format = DateFormat.is24HourFormat(this);

        CharSequence charSequenceStart = DateFormat.format("MMM d, yyyy", calendar);
        CharSequence charSequenceEnd = DateFormat.format("MMM d, yyyy", calendar1);
        dateStart.setText(charSequenceStart);
        dateEnd.setText(charSequenceEnd);
        if (is24format) {
            timeStart.setText(timeToString.intToTxtTime(viewModel.startHour.getValue(), viewModel.startMinutes.getValue()));
            timeEnd.setText(timeToString.intToTxtTime(viewModel.endHour.getValue(), viewModel.endMinutes.getValue()));
        } else {
            CharSequence charSequenceStart1 = DateFormat.format("hh:mm aa", calendar);
            CharSequence charSequenceEnd1 = DateFormat.format("hh:mm aa", calendar1);
            timeStart.setText(charSequenceStart1);
            timeEnd.setText(charSequenceEnd1);
        }
    }

    private void setEndDate() {
        viewModel.isEndDatePickerShowing.setValue(true);
    }

    private void showEndDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            viewModel.endYear.setValue(year);
            viewModel.endMonth.setValue(month);
            viewModel.endDay.setValue(day);
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener,
                viewModel.endYear.getValue(), viewModel.endMonth.getValue(), viewModel.endDay.getValue());

        Calendar minDate = Calendar.getInstance();
        minDate.set(viewModel.startYear.getValue(), viewModel.startMonth.getValue(), viewModel.startDay.getValue(), 0, 0, 0);
        minDate.set(Calendar.MILLISECOND, 0);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.setOnDismissListener(dialog -> {
            if (!isChangingConfigurations()) {
                viewModel.isEndDatePickerShowing.setValue(false);
            }
        });
        datePickerDialog.show();
    }

    private void setStartDate() {
        viewModel.isStartDatePickerShowing.setValue(true);
    }

    private void showStartDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            viewModel.startYear.setValue(year);
            viewModel.startMonth.setValue(month);
            viewModel.startDay.setValue(day);
            syncEndWithStartIfNeeded(year, month, day, viewModel.startHour.getValue(), viewModel.startMinutes.getValue());
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener,
                viewModel.startYear.getValue(), viewModel.startMonth.getValue(), viewModel.startDay.getValue());
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setOnDismissListener(dialog -> {
            if (!isChangingConfigurations()) {
                viewModel.isStartDatePickerShowing.setValue(false);
            }
        });
        datePickerDialog.show();
    }

    private void setEndTime() {
        viewModel.isEndTimePickerShowing.setValue(true);
    }

    private void showEndTimePicker() {
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, Hour, Minutes) -> {
            if (Objects.equals(viewModel.startYear.getValue(), viewModel.endYear.getValue()) &&
                    Objects.equals(viewModel.startMonth.getValue(), viewModel.endMonth.getValue()) &&
                    Objects.equals(viewModel.startDay.getValue(), viewModel.endDay.getValue())) {

                if (Hour < viewModel.startHour.getValue() || (Hour == viewModel.startHour.getValue() && Minutes < viewModel.startMinutes.getValue())) {
                    Toast.makeText(this, R.string.toast_invalid_time, Toast.LENGTH_SHORT).show();
                    viewModel.endHour.setValue(viewModel.startHour.getValue());
                    viewModel.endMinutes.setValue(viewModel.startMinutes.getValue());
                    return;
                }
            }
            viewModel.endHour.setValue(Hour);
            viewModel.endMinutes.setValue(Minutes);
        };
        timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                viewModel.endHour.getValue(), viewModel.endMinutes.getValue(), is24format);
        timePickerDialog.setOnDismissListener(dialog -> {
            if (!isChangingConfigurations()) {
                viewModel.isEndTimePickerShowing.setValue(false);
            }
        });
        timePickerDialog.show();
    }

    private void setStartTime() {
        viewModel.isStartTimePickerShowing.setValue(true);
    }

    private void showStartTimePicker() {
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, Hour, Minutes) -> {
            viewModel.startHour.setValue(Hour);
            viewModel.startMinutes.setValue(Minutes);
            syncEndWithStartIfNeeded(viewModel.startYear.getValue(), viewModel.startMonth.getValue(),
                    viewModel.startDay.getValue(), Hour, Minutes);
        };
        timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                viewModel.startHour.getValue(), viewModel.startMinutes.getValue(), is24format);
        timePickerDialog.setOnDismissListener(dialog -> {
            if (!isChangingConfigurations()) {
                viewModel.isStartTimePickerShowing.setValue(false);
            }
        });
        timePickerDialog.show();
    }

    private void syncEndWithStartIfNeeded(int sYear, int sMonth, int sDay, int sHour, int sMin) {
        Calendar startCal = Calendar.getInstance();
        startCal.set(sYear, sMonth, sDay, sHour, sMin, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(viewModel.endYear.getValue(), viewModel.endMonth.getValue(), viewModel.endDay.getValue(),
                viewModel.endHour.getValue(), viewModel.endMinutes.getValue(), 0);
        endCal.set(Calendar.MILLISECOND, 0);

        if (startCal.after(endCal)) {
            viewModel.endYear.setValue(sYear);
            viewModel.endMonth.setValue(sMonth);
            viewModel.endDay.setValue(sDay);
            viewModel.endHour.setValue(sHour);
            viewModel.endMinutes.setValue(sMin);
        }
    }

//    private boolean isEventTitleValid(String title) {
//        return title == null || title.isEmpty();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuUpdate) {
            onAddBtn();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkIfCardIsExpanded() {
        if (Boolean.TRUE.equals(viewModel.isExpanded.getValue())) {
            expandView();
        }
    }

    public void expandView() {
        viewModel.toggleExpanded();
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

    public void updateOnConfigurationChanges() {
        updateColorText(viewModel.colorPicker.getValue());
        updatePriorityText(viewModel.priorityPicker.getValue());
    }

    private void updatePriorityText(int value) {
        if (value == 0) {
            priority.setText(R.string.set_important);
        } else {
            priority.setText(R.string.set_unimportant);
        }
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
        advOptions = findViewById(R.id.advOptions);
        dateStart = findViewById(R.id.startDate);
        dateEnd = findViewById(R.id.endDate);
        timeStart = findViewById(R.id.startTime);
        timeEnd = findViewById(R.id.endTime);
        cardView = findViewById(R.id.cardView);
        expandableLayout = findViewById(R.id.expandableLayout);
        eventTitle = findViewById(R.id.eventTitle);
        eventLocation = findViewById(R.id.eventLocation);
        eventInput = findViewById(R.id.eventNode);
        btnAdd = findViewById(R.id.btnAdd);
        allDaySw = findViewById(R.id.allDaySw);
        soundNotSw = findViewById(R.id.soundNotSw);
        silentNotSw = findViewById(R.id.silentNotificationSw);
        eventColor = findViewById(R.id.eventColor);
        priority = findViewById(R.id.priority);
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
        super.onDestroy();
    }

}