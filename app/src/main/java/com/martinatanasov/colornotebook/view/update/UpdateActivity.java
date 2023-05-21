package com.martinatanasov.colornotebook.view.update;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.dialog_views.ApplyColor;
import com.martinatanasov.colornotebook.dialog_views.ApplyPriority;
import com.martinatanasov.colornotebook.dialog_views.PriorityDialog;
import com.martinatanasov.colornotebook.dialog_views.SelectColor;
import com.martinatanasov.colornotebook.model.MyDatabaseHelper;
import com.martinatanasov.colornotebook.tools.ConvertTimeToTxt;
import com.martinatanasov.colornotebook.tools.Tools;
import com.martinatanasov.colornotebook.view.main.MainActivity;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity implements ApplyColor {

    EditText eventTitle, eventLocation, eventInput;
    Button btnUpdate, btnDelete;
    TextView advOptions, dateStart, dateEnd, timeStart, timeEnd, eventColor, priority, createdDate, modifiedDate;
    LinearLayout expandableLayout;
    CardView cardView;
    DatePickerDialog datePickerDialog;
    SwitchCompat allDaySw, soundNotSw, silentNotSw;
    private static Calendar calendar, calendar1;
    public static String id, title, location, input;
    private static ConvertTimeToTxt timeToString = new ConvertTimeToTxt();

    public static final String TAG = "UpdateActivity";
    public static final String SHARED_PREF = "sharedPref";
    public static final String THEME = "theme";
    public static int YEAR=0, MONTH=0, DAY=0, HOUR=0, MINUTES=0;
    public static int YEAR2=0, MONTH2=0, DAY2=0, HOUR2=0, MINUTES2=0;
    public static int dayEventBool=0, soundNotificationBool=0, silentNotificationBool=0, colorPicker=0, priorityPicker =0;
    public static long eventCreatedDate=0, eventModifiedDate=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load skin resource
        skinTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        //Change Back arrow button
        Tools tools = new Tools();
        tools.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));

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

        //First before update DB
        calendar = Calendar.getInstance();
        calendar1 = Calendar.getInstance();
        getAndSetIntentData();

        //Set actionbar title after getAndSetIntentData method
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
        if(dayEventBool == 1 || soundNotificationBool == 1 || silentNotificationBool == 1){
            expandView();
        }
        btnUpdate.setOnClickListener(v -> onUpdateBtn());

        btnDelete.setOnClickListener(v -> confirmDialog());

        advOptions.setOnClickListener(view -> expandView());
        dateStart.setOnClickListener(view -> setStartDate());
        timeStart.setOnClickListener(view -> setStartTime());
        dateEnd.setOnClickListener(view -> setEndDate());
        timeEnd.setOnClickListener(view -> setEndTime());
        eventColor.setOnClickListener(view -> selectColor());

        allDaySw.setOnClickListener(view -> dayEventBool = allDaySw.isChecked() ? 1:0);
        soundNotSw.setOnClickListener(view -> soundNotificationBool = soundNotSw.isChecked() ? 1:0);
        silentNotSw.setOnClickListener(view -> silentNotificationBool = silentNotSw.isChecked() ? 1:0);
        priority.setOnClickListener(v -> managePriority());
    }

    private void managePriority(){
        PriorityDialog priorityDialog = new PriorityDialog(getApplicationContext());
        priorityDialog.setPriority(this, priorityPicker);
        priorityDialog.setDialogResult(new ApplyPriority(){
            @Override
            public void setPriority(int status) {
                priorityPicker = status;
                getPriorityString();
            }
        });
    }

    private void onUpdateBtn(){
        //Update DB
        MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
        title = eventTitle.getText().toString().trim();
        location = eventLocation.getText().toString().trim();
        input = eventInput.getText().toString().trim();
        //Update modifier date
        eventModifiedDate = Calendar.getInstance().getTimeInMillis();

        myDB.updateData(id, title, location, input,
                colorPicker,
                priorityPicker,
                YEAR, MONTH, DAY, HOUR, MINUTES,
                YEAR2, MONTH2, DAY2, HOUR2, MINUTES2,
                eventCreatedDate,
                eventModifiedDate,
                dayEventBool,
                soundNotificationBool,
                silentNotificationBool);

//        Log.d(TAG, "onClick: " +id+" title: "+ title+" location: "+ location+" event_text: "+ input
//        + " Start Hour " + HOUR +" Start min " + MINUTES + " CreatedDate " + eventCreatedDate);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    void getAndSetIntentData(){
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")){
            // Getting data from Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            location = getIntent().getStringExtra("location");
            input = getIntent().getStringExtra("input");

//            colorPicker = Integer.parseInt(getIntent().getStringExtra("color"));
            colorPicker = getIntent().getIntExtra("color",0);
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
            silentNotificationBool = getIntent().getIntExtra("silent_notifications",0);

            //Setting Intent data
            eventTitle.setText(title);
            eventLocation.setText(location);
            eventInput.setText(input);
            allDaySw.setChecked(dayEventBool == 1);
            soundNotSw.setChecked(soundNotificationBool == 1);
            silentNotSw.setChecked(silentNotificationBool == 1);

            //Import data to calendar 1 and 2
            final boolean is24format = DateFormat.is24HourFormat(this);
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

            CharSequence charSequence= DateFormat.format("MMM d, yyyy", calendar);
            CharSequence charSequence1= DateFormat.format("MMM d, yyyy", calendar1);
            dateStart.setText(charSequence);
            dateEnd.setText(charSequence1);
            //Set color picker text
            updateColorText(colorPicker);
            //Update create/modified date
            createdDate.setText(timestampToDate(eventCreatedDate, is24format));
            modifiedDate.setText(timestampToDate(eventModifiedDate, is24format));
            if (is24format){
                timeStart.setText(timeToString.intToTxtTime(HOUR, MINUTES));
                timeEnd.setText(timeToString.intToTxtTime(HOUR2, MINUTES2));
            }else{
                CharSequence timeSequence= DateFormat.format("hh:mm aa", calendar);
                CharSequence timeSequence1= DateFormat.format("hh:mm aa", calendar1);
                timeStart.setText(timeSequence);
                timeEnd.setText(timeSequence1);
            }
        } else {
            Toast.makeText(this, R.string.toast_noData, Toast.LENGTH_SHORT).show();
        }
        getPriorityString();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            soundNotSw.setEnabled(false);
            silentNotSw.setEnabled(false);
        }
    }
    private void selectColor(){
        SelectColor selectColor = new SelectColor(colorPicker);
        selectColor.show(getSupportFragmentManager(), String.valueOf(R.string.pickColor));
    }
    private void getPriorityString(){
        switch (priorityPicker){
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

    private void setEndDate(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, day);
                CharSequence charSequence= DateFormat.format("MMM d, yyyy", calendar1);
                dateEnd.setText(charSequence);
                //save data for next reuse
                YEAR2=year;
                MONTH2=month;
                DAY2=day;
            }
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener, YEAR2, MONTH2, DAY2);
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setStartDate(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, day);
                CharSequence charSequence= DateFormat.format("MMM d, yyyy", calendar);
                dateStart.setText(charSequence);
                //save data for next reuse
                YEAR=year;
                MONTH=month;
                DAY=day;
            }
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener, YEAR, MONTH, DAY);
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setEndTime(){
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int Hour, int Minutes) {
                calendar1.set(Calendar.HOUR_OF_DAY, Hour);
                calendar1.set(Calendar.MINUTE, Minutes);
                CharSequence charSequence= DateFormat.format("hh:mm aa", calendar1);
                if(is24format){
                    timeEnd.setText(timeToString.intToTxtTime(Hour, Minutes));
                }else{
                    timeEnd.setText(charSequence);
                }
                HOUR2 = Hour;
                MINUTES2 = Minutes;
            }
        };
        TimePickerDialog timePickerDialog= new TimePickerDialog(this, onTimeSetListener, HOUR2, MINUTES2, is24format);
        //timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void setStartTime(){
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int Hour, int Minutes) {
                calendar.set(Calendar.HOUR_OF_DAY, Hour);
                calendar.set(Calendar.MINUTE, Minutes);
                CharSequence charSequence= DateFormat.format("hh:mm aa", calendar);
                if(is24format){
                    timeStart.setText(timeToString.intToTxtTime(Hour, Minutes));
                }else{
                    timeStart.setText(charSequence);
                }
                HOUR = Hour;
                MINUTES = Minutes;
            }
        };
        TimePickerDialog timePickerDialog= new TimePickerDialog(this, onTimeSetListener, HOUR, MINUTES, is24format);
        //timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete) + " " + title + "?");
        builder.setMessage(getString(R.string.alert_dialog_message_sure_to_dell) + " " + title + "?");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteDataOnOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
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

    public void expandView(){
        if (expandableLayout.getVisibility() == View.GONE){
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            expandableLayout.setVisibility(View.VISIBLE);
            advOptions.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
        }else{
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            expandableLayout.setVisibility(View.GONE);
            advOptions.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
        }
    }
    private String timestampToDate(long timestamp, boolean is24format){
        Date date = new Date(timestamp);
        Format formatter;
        if(is24format){
            formatter = new SimpleDateFormat("MMM d, yyyy  hh:mm");
        }else{
            //formatter = new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss aa");
            formatter = new SimpleDateFormat("MMM d, yyyy  hh:mm aa");
        }
        return formatter.format(date);
    }
    private void skinTheme(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
//        if(sharedPreferences.getBoolean(SWITCH_DARK_MODE, false)){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            getDelegate().applyDayNight();
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//            getDelegate().applyDayNight();
//        }

        int theme = sharedPreferences.getInt(THEME, 0);
        switch (theme){
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
    private void updateColorText(int color){
        String[] stringArray = getResources().getStringArray(R.array.color_picker_array);
        eventColor.setText(stringArray[color]);
    }
    @Override
    public void setColor(int color) {
        colorPicker = color;
        updateColorText(color);
    }
}