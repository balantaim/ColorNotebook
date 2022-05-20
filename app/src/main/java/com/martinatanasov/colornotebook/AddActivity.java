package com.martinatanasov.colornotebook;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    EditText eventTitle, eventLocation, eventInput;
    Button btnAdd;
    TextView advOptions, dateStart, dateEnd, timeStart, timeEnd;
    LinearLayout expandableLayout;
    CardView cardView;

    public static final String SHARED_PREF = "sharedPref";
    public static final String THEME = "theme";
    public static final String TXT_SIZE = "txtSize";
    public static final String SWITCH_DARK_MODE = "switchDarkMode";
    public static int YEAR=0, MONTH=0, DATE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Increase bottom area when the keyboard appears
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Load skin resource
        skinTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);

        advOptions =findViewById(R.id.advOptions);
        dateStart =findViewById(R.id.startDate);
        dateEnd =findViewById(R.id.endDate);
        timeStart =findViewById(R.id.startTime);
        timeEnd =findViewById(R.id.endTime);
        cardView =findViewById(R.id.cardView);
        expandableLayout =findViewById(R.id.expandableLayout);
        eventTitle =findViewById(R.id.eventTitle);
        eventLocation =findViewById(R.id.eventLocation);
        eventInput =findViewById(R.id.eventNode);
        btnAdd=findViewById(R.id.btnAdd);

        if ((dateStart.getText().toString().equals("") || dateStart == null)
        && (dateEnd.getText().toString().equals("") || dateEnd == null)){
            initAdvancedOptions();
        }
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tryEmpty(eventTitle.getText().toString(), eventLocation.getText().toString(), eventInput.getText().toString())){
                    MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                    myDB.addEvent(eventTitle.getText().toString().trim(),
                            eventLocation.getText().toString().trim(),
                            eventInput.getText().toString().trim());
                }
            }
        });

        advOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandView();
            }
        });

        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartDate();
            }
        });

        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartTime();
            }
        });

        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndDate();
            }
        });
    }

    private void initAdvancedOptions(){
        Calendar calendar = Calendar.getInstance();

        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24format = DateFormat.is24HourFormat(this);

        CharSequence charSequence= DateFormat.format("MMM d, yyyy", calendar);
        dateStart.setText(charSequence);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH + 1));
        dateEnd.setText(charSequence);
        if (is24format){
            timeStart.setText(intToTxtTime(HOUR, MINUTE));
            timeEnd.setText(intToTxtTime(HOUR, MINUTE));
        }else{
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR, HOUR);
            calendar1.set(Calendar.MINUTE, MINUTE);
            CharSequence charSequence1= DateFormat.format("hh:mm aa", calendar1);
            timeStart.setText(charSequence1);
            timeEnd.setText(charSequence1);
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
            DATE = calendar.get(Calendar.DATE);
        }
//        calendar.set(Calendar.YEAR, YEAR);
//        calendar.set(Calendar.MONTH, MONTH);
//        calendar.set(Calendar.DATE, DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);

                CharSequence charSequence= DateFormat.format("MMM d, yyyy", calendar1);
                dateStart.setText(charSequence);
                //save data for next reuse
                YEAR=year;
                MONTH=month;
                DATE=date;
                System.out.println(""+YEAR +MONTH +DATE);
                System.out.println(""+year +month +date);
            }
        },YEAR, MONTH, DATE);
        //datePickerDialog.updateDate(YEAR,2,3);
        datePickerDialog.show();
    }

    private void setStartTime(){
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24format = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog= new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (is24format){
                    timeStart.setText(intToTxtTime(hour, minute));
                }else{
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(Calendar.HOUR, hour);
                    calendar1.set(Calendar.MINUTE, minute);
                    CharSequence charSequence= DateFormat.format("hh:mm aa", calendar1);
                    timeStart.setText(charSequence);
                }
            }
        },HOUR, MINUTE, is24format);
        timePickerDialog.show();
    }

    private boolean tryEmpty(String title, String location, String input){
        if (title == null || location == null || input == null){
            Toast.makeText(this, "Empty Label!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if ( (title.isEmpty() || location.isEmpty() || input.isEmpty()) ){
            Toast.makeText(this, "Empty Label!", Toast.LENGTH_SHORT).show();
            return true;
        }
        int count1 =0;
        for (int i = 0;i<title.length();i++){
            if(title.charAt(i) == ' '){
                count1++;
            }
        }
        int count2 =0;
        for (int i = 0;i<input.length();i++){
            if(input.charAt(i) == ' '){
                count2++;
            }
        }
        if((count1==title.length()) || (count2==location.length())){
            Toast.makeText(this, "Remove spaces!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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