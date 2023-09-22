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

package com.martinatanasov.colornotebook.view.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controller.AddActivityController;
import com.martinatanasov.colornotebook.dialog_views.ApplyColor;
import com.martinatanasov.colornotebook.dialog_views.ApplyPriority;
import com.martinatanasov.colornotebook.dialog_views.PriorityDialog;
import com.martinatanasov.colornotebook.dialog_views.SelectColor;
import com.martinatanasov.colornotebook.tools.ConvertTimeToTxt;
import com.martinatanasov.colornotebook.tools.PreferencesManager;
import com.martinatanasov.colornotebook.tools.Tools;

import java.util.Calendar;
import java.util.Objects;


public class AddActivity extends AppCompatActivity implements ApplyColor {
    EditText eventTitle, eventLocation, eventInput;
    Button btnAdd;
    TextView advOptions, dateStart, dateEnd, timeStart, timeEnd, eventColor, priority;
    LinearLayout expandableLayout;
    CardView cardView;
    DatePickerDialog datePickerDialog;
    SwitchCompat allDaySw, soundNotSw, silentNotSw;
    //private static Calendar calendar, calendar1;
    private final ConvertTimeToTxt timeToString = new ConvertTimeToTxt();
    private AddActivityController controller;

    //private ApplyPriority listener;
    //Dialog colorDialog;
    //ConstraintLayout setImportant, setRegular, setUnimportant;

    public static int YEAR = 0, MONTH = 0, DAY = 0, HOUR = 0, MINUTES = 0;
    public static int YEAR2 = 0, MONTH2 = 0, DAY2 = 0, HOUR2 = 0, MINUTES2 = 0;
    private Calendar calendar, calendar1;
    public boolean firstTimeFocusText = true, isExpanded = false;
    public int dayEvent = 0, soundNotification = 0, silentNotification = 0, colorPicker = 0, priorityPicker = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Increase bottom area when the keyboard appears
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Load skin resource
        skinTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //Find view resources
        initViews();

        //Change Back arrow button
        Tools tools = new Tools();
        tools.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));

//        Log.d("ADD", "onCreate: color: " + colorPicker);
//        Log.d("ADD", "onCreate: priority: " + priorityPicker);
//        Log.d("ADD", "onCreate: expand: " + isExpanded);
//        Log.d("ADD", "onCreate: dayEvent: " + dayEvent);
//        Log.d("ADD", "onCreate: focused: " + "--------");

        //Focus first edit text
        if(firstTimeFocusText){
            eventTitle.requestFocus();
            firstTimeFocusText = false;
        }

        if ((dateStart.getText().toString().equals("") || dateStart == null)
                && (dateEnd.getText().toString().equals("") || dateEnd == null)) {
            //TODO wrong if
            //initAdvancedOptions();
        }
        initAdvancedOptions();
        //Custom dialog set event priority
        //colorDialog = new Dialog(this);

        //updateOnConfigurationChanges();

        //checkIfCardIsExpanded();

        //Set up On Click Listeners
        btnAdd.setOnClickListener(v -> onAddBtn());
        advOptions.setOnClickListener(view -> expandView());
        dateStart.setOnClickListener(view -> setStartDate());
        timeStart.setOnClickListener(view -> setStartTime());
        dateEnd.setOnClickListener(view -> setEndDate());
        timeEnd.setOnClickListener(view -> setEndTime());
        eventColor.setOnClickListener(view -> selectColor());
        //Set up Switches
        allDaySw.setOnClickListener(view -> dayEvent = allDaySw.isChecked() ? 1 : 0);
        soundNotSw.setOnClickListener(view -> soundNotification = soundNotSw.isChecked() ? 1 : 0);
        silentNotSw.setOnClickListener(view -> silentNotification = silentNotSw.isChecked() ? 1 : 0);
        priority.setOnClickListener(v -> managePriority());
        //Scale Text
        /*
        Scale fonts change getApplicationContext with context
        Configuration configuration = getApplicationContext().getResources().getConfiguration();
        configuration.fontScale = 3.0f;
        getApplicationContext().createConfigurationContext(configuration);
        */
    }
    private void selectColor(){
        Log.d("ADD", "selectColor: " + colorPicker);
        SelectColor selectColor = new SelectColor(colorPicker);
        selectColor.show(getSupportFragmentManager(), String.valueOf(R.string.pickColor));
        //todo update color
    }

    private void managePriority() {
        PriorityDialog priorityDialog = new PriorityDialog(getApplicationContext());
        Log.d("ADD", "managePriority: " + priorityPicker);
        priorityDialog.setPriority(this, priorityPicker);
        priorityDialog.setDialogResult(new ApplyPriority() {
            @Override
            public void setPriority(int status) {
                priorityPicker = status;
                switch (status){
                    case 1:
                        priority.setText(R.string.set_regular);
                        break;
                    case 2:
                        priority.setText(R.string.set_unimportant);
                        break;
                    default:
                        priority.setText(R.string.set_important);
                        break;
                }
            }
        });
        /*
        SetPriority setPriority = new SetPriority();
        setPriority.show(getSupportFragmentManager(), "Set priority");
        priorityDialog.setContentView(R.layout.set_priority_dialog);
        setImportant = (ConstraintLayout) priorityDialog.findViewById(R.id.setImportant);
        setRegular = (ConstraintLayout) priorityDialog.findViewById(R.id.setRegular);
        setUnimportant = (ConstraintLayout) priorityDialog.findViewById(R.id.setUnimportant);
        setImportant.setOnClickListener(v -> { priorityPicker=0; priorityDialog.dismiss();});
        setRegular.setOnClickListener(v -> {listener.setPriority(1);priorityDialog.dismiss();});
        setUnimportant.setOnClickListener(v -> {listener.setPriority(2);priorityDialog.dismiss();});
        priorityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        priorityDialog.show();
        */
    }

    private void onAddBtn() {
        if (eventTitle.getText().toString().length() > 1) {
            if (!tryEmpty(eventTitle.getText().toString(), eventInput.getText().toString())) {

                long timestamp = Calendar.getInstance().getTimeInMillis();
                controller.addRecord(
                        eventTitle.getText().toString().trim(),
                        eventLocation.getText().toString().trim(),
                        eventInput.getText().toString().trim(),
                        colorPicker,
                        priorityPicker,
                        YEAR, MONTH, DAY, HOUR, MINUTES,
                        YEAR2, MONTH2, DAY2, HOUR2, MINUTES2,
                        timestamp,
                        dayEvent,
                        soundNotification,
                        silentNotification
                );

                /*
                Log.d("TEST", "onAddBtn: " + timestamp);
                String pattern = "dd-M-yyyy hh:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String ss = simpleDateFormat.format(new Date(timestamp));
                Log.d("TEST", "TimePrint: " + ss + " ," + timestamp);
                */
            }
        } else {
            Toast.makeText(this, "Header should contain at least 2 symbols!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAdvancedOptions() {
        calendar = Calendar.getInstance();
        calendar1 = Calendar.getInstance();
        if((YEAR == 0) || (YEAR2 == 0)){
            //Get the values from the calendar instance from now
            HOUR = HOUR2 = calendar.get(Calendar.HOUR_OF_DAY);
            MINUTES = MINUTES2 = calendar.get(Calendar.MINUTE);
            YEAR = YEAR2 = calendar.get(Calendar.YEAR);
            MONTH = MONTH2 = calendar.get(Calendar.MONTH);
            DAY = DAY2 = calendar.get(Calendar.DATE);
        }else{
            //First calendar
            calendar.set(Calendar.YEAR, YEAR);
            calendar.set(Calendar.MONTH, MONTH);
            calendar.set(Calendar.DATE, DAY);
            calendar.set(Calendar.HOUR_OF_DAY, HOUR);
            calendar.set(Calendar.MINUTE, MINUTES);
            //Second calendar
            calendar1.set(Calendar.YEAR, YEAR2);
            calendar1.set(Calendar.MONTH, MONTH2);
            calendar1.set(Calendar.DATE, DAY2);
            calendar1.set(Calendar.HOUR_OF_DAY, HOUR2);
            calendar1.set(Calendar.MINUTE, MINUTES2);
        }
        boolean is24format = DateFormat.is24HourFormat(this);

        CharSequence charSequenceStart = DateFormat.format("MMM d, yyyy", calendar);
        CharSequence charSequenceEnd = DateFormat.format("MMM d, yyyy", calendar1);
        dateStart.setText(charSequenceStart);
        dateEnd.setText(charSequenceEnd);
        if (is24format) {
            timeStart.setText(timeToString.intToTxtTime(HOUR, MINUTES));
            timeEnd.setText(timeToString.intToTxtTime(HOUR2, MINUTES2));
        } else {
            CharSequence charSequenceStart1 = DateFormat.format("hh:mm aa", calendar);
            CharSequence charSequenceEnd1 = DateFormat.format("hh:mm aa", calendar1);
            timeStart.setText(charSequenceStart1);
            timeEnd.setText(charSequenceEnd1);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            soundNotSw.setEnabled(false);
            silentNotSw.setEnabled(false);
        }
    }

    private void setEndDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, day);
                CharSequence charSequence = DateFormat.format("MMM d, yyyy", calendar1);

                dateEnd.setText(charSequence);
                //save data for next reuse
                YEAR2 = year;
                MONTH2 = month;
                DAY2 = day;
            }
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener, YEAR2, MONTH2, DAY2);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setStartDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, day);
                CharSequence charSequence = DateFormat.format("MMM d, yyyy", calendar);

                dateStart.setText(charSequence);
                //save data for next reuse
                YEAR = year;
                MONTH = month;
                DAY = day;
            }
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener, YEAR, MONTH, DAY);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setEndTime() {
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int Hour, int Minutes) {
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
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, HOUR2, MINUTES2, is24format);
        //timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void setStartTime() {
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int Hour, int Minutes) {
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
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, HOUR, MINUTES, is24format);
        //timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private boolean tryEmpty(String title, String input) {
        if (title == null || title.isEmpty()) {
            Toast.makeText(this, "Title is empty!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (input == null || input.isEmpty()) {
            Toast.makeText(this, "Event's description is empty!", Toast.LENGTH_SHORT).show();
            return true;
        }
        int count1 = 0;
        for (int i = 0; i < title.length(); i++) {
            if (title.charAt(i) == ' ') {
                count1++;
            }
        }
        int count2 = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                count2++;
            }
        }
        if ((count1 == title.length()) || (count2 == input.length())) {
            Toast.makeText(this, "Too much space characters!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

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
    public void checkIfCardIsExpanded(){
        if(isExpanded){
            expandView();
        }
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
    private void initViews(){
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
        //Add controller
        controller = new AddActivityController(this);
    }
    public void updateOnConfigurationChanges(){
        int color = colorPicker;
        Log.d("ADD", "update On ConfigurationChanges Color: " + color);
        int priority = priorityPicker;
        Log.d("ADD", "update On ConfigurationChanges Priority: " + priority);
        if(color != 0){
            updateColorText(color);
        }
        if(priority != 1){
            updatePriorityText(priority);
        }
        //updateColorText(color);
        //updatePriorityText(priority);
    }

    private void skinTheme() {
        PreferencesManager preferencesManager = new PreferencesManager(this);
        int theme = preferencesManager.getCurrentTheme();
        switch (theme) {
            case 1:
                setTheme(R.style.Theme_BlueColorNotebook);
                break;
            case 2:
                setTheme(R.style.Theme_DarkColorNotebook);
                break;
            default:
                setTheme(R.style.Theme_DefaultColorNotebook);
                break;
        }
    }
    private void updatePriorityText(int value){
        if(value == 0){
            priority.setText(R.string.set_important);
        }else{
            priority.setText(R.string.set_unimportant);
        }
    }
    private void updateColorText(int color){
        String[] stringArray = getResources().getStringArray(R.array.color_picker_array);
        eventColor.setText(stringArray[color]);
    }
    @Override
    public void setColor(int color) {
        Log.d("ADD", "setColor: " + color);
        colorPicker = color;
        updateColorText(color);
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
        outState.putBoolean("key_focus_text", firstTimeFocusText);
        outState.putInt("key_day_event", dayEvent);
        outState.putInt("key_sound_notification", soundNotification);
        outState.putInt("key_silent_notification", silentNotification);
        outState.putInt("key_color", colorPicker);
        outState.putInt("key_priority", priorityPicker);

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
        firstTimeFocusText = savedInstanceState.getBoolean("key_focus_text", false);
        dayEvent = savedInstanceState.getInt("key_day_event", 0);
        soundNotification = savedInstanceState.getInt("key_sound_notification", 0);
        silentNotification = savedInstanceState.getInt("key_silent_notification", 0);
        colorPicker = savedInstanceState.getInt("key_color");
        priorityPicker = savedInstanceState.getInt("key_priority");

        super.onRestoreInstanceState(savedInstanceState);
    }

}