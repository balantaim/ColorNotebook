package com.martinatanasov.colornotebook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    EditText eventTitle, eventLocation, eventInput;
    Button btnUpdate, btnDelete;
    TextView advOptions, dateStart, dateEnd, timeStart, timeEnd, eventColor, avatar;
    LinearLayout expandableLayout;
    CardView cardView;
    DatePickerDialog datePickerDialog;
    SwitchCompat allDaySw, soundNotSw, silentNotSw;
    static String id, title, location, input;

    public static final String TAG = "UpdateActivity";
    public static final String SHARED_PREF = "sharedPref";
    public static final String THEME = "theme";
    public static final String TXT_SIZE = "txtSize";
    public static final String SWITCH_DARK_MODE = "switchDarkMode";
    public static int YEAR=10, MONTH=10, DAY=10, HOUR=0, MINUTES=0;
    public static int YEAR2=0, MONTH2=0, DAY2=0, HOUR2=0, MINUTES2=0;
    public static int dayEventBool=0, soundNotificationBool=0, silentNotificationBool=1, colorPicker=0, avatarPicker=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load skin resource
        skinTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);

        advOptions =findViewById(R.id.advOptions2);
        dateStart =findViewById(R.id.startDate2);
        dateEnd =findViewById(R.id.endDate2);
        timeStart =findViewById(R.id.startTime2);
        timeEnd =findViewById(R.id.endTime2);
        cardView =findViewById(R.id.cardView2);
        expandableLayout =findViewById(R.id.expandableLayout2);
        eventTitle =findViewById(R.id.eventTitle2);
        eventLocation =findViewById(R.id.eventLocation2);
        eventInput =findViewById(R.id.eventInput2);
        btnUpdate =findViewById(R.id.btnUpdate);
        btnDelete =findViewById(R.id.btnDelete);
        allDaySw=findViewById(R.id.allDaySw2);
        soundNotSw =findViewById(R.id.soundNotSw2);
        silentNotSw =findViewById(R.id.silentNotificationSw2);
        eventColor =findViewById(R.id.eventColor2);
        avatar =findViewById(R.id.avatar2);

        //First before update DB
        getAndSetIntentData();

        //Set actionbar title after getAndSetIntentData method
        ActionBar ab =getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateBtn();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });

        advOptions.setOnClickListener(view -> expandView());

        dateStart.setOnClickListener(view -> setStartDate());

        timeStart.setOnClickListener(view -> setStartTime());

        dateEnd.setOnClickListener(view -> setEndDate());

        allDaySw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayEventBool = allDaySw.isChecked() ? 1:0;
            }
        });
        soundNotSw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundNotificationBool = soundNotSw.isChecked() ? 1:0;
            }
        });
        silentNotSw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                silentNotificationBool = silentNotSw.isChecked() ? 1:0;
            }
        });

    }

    private void onUpdateBtn(){
        //Update DB
        MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
        title = eventTitle.getText().toString().trim();
        location = eventLocation.getText().toString().trim();
        input = eventInput.getText().toString().trim();


        myDB.updateData(id, title, location, input,
                colorPicker,
                avatarPicker,
                YEAR, MONTH, DAY, HOUR, MINUTES,
                YEAR2, MONTH2, DAY2, HOUR2, MINUTES2,
                dayEventBool,
                soundNotificationBool,
                silentNotificationBool);

        Log.d(TAG, "onClick: " +id+" title: "+ title+" location: "+ location+" event_text: "+ input);
    }

    void getAndSetIntentData(){
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")
                && getIntent().hasExtra("location") && getIntent().hasExtra("input")){
            // Getting data from Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            location = getIntent().getStringExtra("location");
            input = getIntent().getStringExtra("input");

            //Setting Intent data
            eventTitle.setText(title);
            eventLocation.setText(location);
            eventInput.setText(input);

        } else {
            Toast.makeText(this, R.string.toast_noData, Toast.LENGTH_SHORT).show();
        }
    }

    private String intToTxtTime(int h, int m){
        String s ="";
        if(h<10){
            s+="0" + Integer.toString(h);
        }else{
            s+="" + Integer.toString(h);
        }
        if(m<10){
            s+=":0" + Integer.toString(m);
        }else{
            s+=":" + Integer.toString(m);
        }
        return s;
    }

    private void setEndDate(){
        Calendar calendar = Calendar.getInstance();
        String data = dateEnd.getText().toString();
        // todo

        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

    private void setStartDate(){
        Calendar calendar = Calendar.getInstance();
        if(YEAR>-1){
            YEAR = calendar.get(Calendar.YEAR);
            MONTH = calendar.get(Calendar.MONTH);
            DAY = calendar.get(Calendar.DATE);
        }

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, day);

                CharSequence charSequence= DateFormat.format("MMM d, yyyy", calendar1);
                dateStart.setText(charSequence);
                //save data for next reuse
                YEAR=year;
                MONTH=month;
                DAY =day;
            }
        };
        datePickerDialog = new DatePickerDialog(this, dateSetListener, YEAR, MONTH, DAY);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    private void setStartTime(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR, HOUR);
//        calendar.set(Calendar.MINUTE, MINUTES);
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int Hour, int Minutes) {
                if(Hour!=0 && Minutes!=0){
                    HOUR = Hour;
                    MINUTES = Minutes;
                }
                if (is24format){
                    timeStart.setText(intToTxtTime(HOUR, MINUTES));
                }else{
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(Calendar.HOUR, HOUR);
                    calendar1.set(Calendar.MINUTE, MINUTES);
                    CharSequence charSequence= DateFormat.format("hh:mm aa", calendar1);
                    timeStart.setText(charSequence);
                }
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

    @SuppressLint("ResourceAsColor")
    private void skinTheme(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
//        boolean swOnOff = sharedPreferences.getBoolean(SWITCH_DARK_MODE, false);


        if(sharedPreferences.getBoolean(SWITCH_DARK_MODE, false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getDelegate().applyDayNight();
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            getDelegate().applyDayNight();
        }

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
}