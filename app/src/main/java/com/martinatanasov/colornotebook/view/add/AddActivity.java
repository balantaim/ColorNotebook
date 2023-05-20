package com.martinatanasov.colornotebook.view.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
    private static Calendar calendar, calendar1;
    private static ConvertTimeToTxt timeToString = new ConvertTimeToTxt();

    //private ApplyPriority listener;
    //Dialog colorDialog;
    //ConstraintLayout setImportant, setRegular, setUnimportant;

    public static final String SHARED_PREF = "sharedPref";
    public static final String THEME = "theme";
    public static int YEAR = 0, MONTH = 0, DAY = 0, HOUR = 0, MINUTES = 0;
    public static int YEAR2 = 0, MONTH2 = 0, DAY2 = 0, HOUR2 = 0, MINUTES2 = 0;
    public static int dayEventBool = 0, soundNotificationBool = 0, silentNotificationBool = 0, colorPicker = 0, priorityPicker = 1;

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

        //Change Back arrow button
        Tools tools = new Tools();
        tools.setArrowBackIcon(Objects.requireNonNull(getSupportActionBar()));

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

        //Focus first edit text
        eventTitle.requestFocus();

        calendar = Calendar.getInstance();
        calendar1 = Calendar.getInstance();
        if ((dateStart.getText().toString().equals("") || dateStart == null)
                && (dateEnd.getText().toString().equals("") || dateEnd == null)) {
            initAdvancedOptions();
        }
        //Custom dialog set event priority
        //colorDialog = new Dialog(this);

        btnAdd.setOnClickListener(v -> onAddBtn());
        advOptions.setOnClickListener(view -> expandView());
        dateStart.setOnClickListener(view -> setStartDate());
        timeStart.setOnClickListener(view -> setStartTime());
        dateEnd.setOnClickListener(view -> setEndDate());
        timeEnd.setOnClickListener(view -> setEndTime());
        eventColor.setOnClickListener(view -> selectColor());

//        Scale fonts change getApplicationContext with context
//        Configuration configuration = getApplicationContext().getResources().getConfiguration();
//        configuration.fontScale = 3.0f;
//        getApplicationContext().createConfigurationContext(configuration);

        allDaySw.setOnClickListener(view -> dayEventBool = allDaySw.isChecked() ? 1 : 0);
        soundNotSw.setOnClickListener(view -> soundNotificationBool = soundNotSw.isChecked() ? 1 : 0);
        silentNotSw.setOnClickListener(view -> silentNotificationBool = silentNotSw.isChecked() ? 1 : 0);
        priority.setOnClickListener(v -> managePriority());
    }
    private void selectColor(){
        SelectColor selectColor = new SelectColor(colorPicker);
        selectColor.show(getSupportFragmentManager(), String.valueOf(R.string.pickColor));
    }

    private void managePriority() {
        PriorityDialog priorityDialog = new PriorityDialog(getApplicationContext());
        priorityDialog.setPriority(this, priorityPicker);
        priorityDialog.setDialogResult(new ApplyPriority() {
            @Override
            public void setPriority(int status) {
                priorityPicker = status;
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
                MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                final long timestamp = Calendar.getInstance().getTimeInMillis();
                myDB.addEvent(eventTitle.getText().toString().trim(),
                        eventLocation.getText().toString().trim(),
                        eventInput.getText().toString().trim(),
                        colorPicker,
                        priorityPicker,
                        YEAR, MONTH, DAY, HOUR, MINUTES,
                        YEAR2, MONTH2, DAY2, HOUR2, MINUTES2,
                        timestamp,
                        timestamp,
                        dayEventBool,
                        soundNotificationBool,
                        silentNotificationBool);
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
        HOUR = HOUR2 = calendar.get(Calendar.HOUR_OF_DAY);
        MINUTES = MINUTES2 = calendar.get(Calendar.MINUTE);
        final boolean is24format = DateFormat.is24HourFormat(this);
        YEAR = YEAR2 = calendar.get(Calendar.YEAR);
        MONTH = MONTH2 = calendar.get(Calendar.MONTH);
        DAY = DAY2 = calendar.get(Calendar.DATE);

        CharSequence charSequence = DateFormat.format("MMM d, yyyy", calendar);
        dateStart.setText(charSequence);
        dateEnd.setText(charSequence);
        if (is24format) {
            timeStart.setText(timeToString.intToTxtTime(HOUR, MINUTES));
            timeEnd.setText(timeToString.intToTxtTime(HOUR2, MINUTES2));
        } else {
            CharSequence charSequence1 = DateFormat.format("hh:mm aa", calendar);
            timeStart.setText(charSequence1);
            timeEnd.setText(charSequence1);
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

    public void expandView() {
        if (expandableLayout.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            expandableLayout.setVisibility(View.VISIBLE);
            advOptions.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
        } else {
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            expandableLayout.setVisibility(View.GONE);
            advOptions.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
        }
    }

    private void skinTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
//        if (sharedPreferences.getBoolean(SWITCH_DARK_MODE, false)) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            getDelegate().applyDayNight();
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//            getDelegate().applyDayNight();
//        }

        int theme = sharedPreferences.getInt(THEME, 0);
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