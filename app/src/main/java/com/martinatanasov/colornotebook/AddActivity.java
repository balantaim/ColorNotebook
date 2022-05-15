package com.martinatanasov.colornotebook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

public class AddActivity extends AppCompatActivity {
    EditText eventTitle, eventLocation, eventInput;
    Button btnAdd;
    TextView advOptions;
    LinearLayout expandableLayout;
    CardView cardView;

    public static final String SHARED_PREF = "sharedPref";
    public static final String THEME = "theme";
    public static final String TXT_SIZE = "txtSize";
    public static final String SWITCH_DARK_MODE = "switchDarkMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load skin resource
        skinTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);

        advOptions =findViewById(R.id.advOptions);
        cardView =findViewById(R.id.cardView);
        expandableLayout =findViewById(R.id.expandableLayout);
        eventTitle =findViewById(R.id.eventTitle);
        eventLocation =findViewById(R.id.eventLocation);
        eventInput =findViewById(R.id.eventNode);
        btnAdd=findViewById(R.id.btnAdd);
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