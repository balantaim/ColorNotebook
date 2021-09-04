package com.martinatanasov.colornotebook;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class OptionActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private TextView txtSize;
    private Spinner spinner;

    public static final String SHARED_PREF = "sharedPref";
    public static final String THEME = "theme";
    public static final String TXT_SIZE = "txtSize";
    public static final String SWITCH_DARK_MODE = "switchDarkMode";

    private String color;
    private int theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



//        saveOptions();
//
        loadOptions();
        skinTheme();
//        updateViews();
//        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
//            setTheme(R.style.Theme_DarkColorNotebook);
//        } else {
//            setTheme(R.style.Theme_DefaultColorNotebook);
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);


        //Change Back arrow button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);

        //Change actionBar Background color dynamic
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#201E1E"));
        actionBar.setBackgroundDrawable(colorDrawable);

        switchDarkMode=findViewById(R.id.switchDarkMode);
        txtSize=findViewById(R.id.txtSize);
        spinner=findViewById(R.id.spinnerSkins);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        switchDarkMode.setChecked(sharedPreferences.getBoolean(SWITCH_DARK_MODE, false));

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SWITCH_DARK_MODE, switchDarkMode.isChecked());
                editor.apply();
            }
        });
        spinner.setSelection(theme);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Choice color")){

                }else{
//                    String item = parent.getItemAtPosition(position).toString();
                    theme = position;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(THEME, theme);
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void saveOptions(){
        //initiate SPref
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //SPref injection
        editor.putBoolean(SWITCH_DARK_MODE, switchDarkMode.isChecked());

        editor.apply();

    }

    private void loadOptions(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //color = sharedPreferences.getString(THEME, "");
        theme = sharedPreferences.getInt(THEME, 0);
        sharedPreferences.getBoolean(SWITCH_DARK_MODE, false);

        editor.apply();

    }

    public void updateViews(){
        //switchDarkMode.setChecked(swOnOff);
        //spinner.setOnItemSelectedListener(color);

    }

    private void skinTheme(){
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
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