package com.martinatanasov.colornotebook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class AddActivity extends AppCompatActivity {
    EditText bookTitle, bookAuthor, pagesInput;
    Button btnAdd, btnAddLocation;

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

        bookTitle=findViewById(R.id.bookTitle);
        bookAuthor=findViewById(R.id.bookAuthor);
        pagesInput=findViewById(R.id.pagesInput);
        btnAdd=findViewById(R.id.btnAdd);
        btnAddLocation=findViewById(R.id.btnAddLocation);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tryEmpty(bookTitle.getText().toString(), bookAuthor.getText().toString(), pagesInput.getText().toString())){
                    MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                    myDB.addBook(bookTitle.getText().toString().trim(),
                            bookAuthor.getText().toString().trim(),
                            Integer.valueOf(pagesInput.getText().toString().trim()));
                }

            }
        });
    }
    private boolean tryEmpty(String title, String author, String input){
        if (title == null || author == null || input == null){
            Toast.makeText(this, "Empty Label!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if ( (title.isEmpty() || author.isEmpty() || input.isEmpty()) ){
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
        for (int i = 0;i<author.length();i++){
            if(author.charAt(i) == ' '){
                count2++;
            }
        }
        if((count1==title.length()) || (count2==author.length())){
            Toast.makeText(this, "Remove spaces!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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