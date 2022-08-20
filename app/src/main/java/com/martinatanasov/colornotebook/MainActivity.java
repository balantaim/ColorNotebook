package com.martinatanasov.colornotebook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    FloatingActionButton add_button, nav_button;
    CustomAdapter customAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MyDatabaseHelper myDB;
    public static ArrayList<String> event_id;
    public static ArrayList<String> event_title;
    public static ArrayList<String> event_location;
    public static ArrayList<String> event_note;
    //Secondary data parameters
    public static ArrayList<Integer> event_color_picker;
    public static ArrayList<Integer> event_avatar_picker;
    public static ArrayList<Integer> event_start_year;
    public static ArrayList<Integer> event_start_month;
    public static ArrayList<Integer> event_start_day;
    public static ArrayList<Integer> event_start_hour;
    public static ArrayList<Integer> event_start_minutes;
    public static ArrayList<Integer> event_end_year;
    public static ArrayList<Integer> event_end_month;
    public static ArrayList<Integer> event_end_day;
    public static ArrayList<Integer> event_end_hour;
    public static ArrayList<Integer> event_end_minutes;
    public static ArrayList<Integer> event_all_day;
    public static ArrayList<Integer> event_sound_notifications;
    public static ArrayList<Integer> event_silent_notifications;
    public static final String SHARED_PREF = "sharedPref";
    public static final String THEME = "theme";
    public static final String TXT_SIZE = "txtSize";
    public static final String SWITCH_DARK_MODE = "switchDarkMode";
    private static final String DISABLE_TUTORIAL = "disableTutorial";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load tutorial status
        loadTutorial();
        //Load skin resource
        skinTheme();
        setContentView(R.layout.activity_main);
//        Day and Night modes
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        getDelegate().applyDayNight();

        super.onCreate(savedInstanceState);
        recyclerView= findViewById(R.id.recyclerView);
        add_button=findViewById(R.id.add_button);
        nav_button=findViewById(R.id.navigation_button);
        drawerLayout=findViewById(R.id.layoutDrawer);
        navigationView=findViewById(R.id.navDrawer);
        setNavigationViewListener();

        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
        //Make navigation drawer responsive
        navigationView.bringToFront();

        myDB = new MyDatabaseHelper(MainActivity.this);
        event_id = new ArrayList<>();
        event_title = new ArrayList<>();
        event_location = new ArrayList<>();
        event_note = new ArrayList<>();
        //Secondary data parameters
        event_color_picker = new ArrayList<>();
        event_avatar_picker = new ArrayList<>();
        event_start_year = new ArrayList<>();
        event_start_month = new ArrayList<>();
        event_start_day = new ArrayList<>();
        event_start_hour = new ArrayList<>();
        event_start_minutes = new ArrayList<>();
        event_end_year = new ArrayList<>();
        event_end_month = new ArrayList<>();
        event_end_day = new ArrayList<>();
        event_end_hour = new ArrayList<>();
        event_end_minutes = new ArrayList<>();
        event_all_day = new ArrayList<>();
        event_sound_notifications = new ArrayList<>();
        event_silent_notifications = new ArrayList<>();

        storeDataInArrays();
        customAdapter= new CustomAdapter(MainActivity.this, this, event_id, event_title, event_location, event_note,
                event_color_picker, event_avatar_picker, event_start_year, event_start_month, event_start_day, event_start_hour,
                event_start_minutes, event_end_year, event_end_month, event_end_day, event_end_hour, event_end_minutes,
                event_all_day, event_sound_notifications, event_silent_notifications);

        //SimpleCallback - Drag and Drop function
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //recycler view Layout
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    //Initiate Navigation item selection
    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Update date after move from UpdateAct to MainAct
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            recreate();
        }
    }

    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
//            Toast.makeText(this, R.string.toast_no_data, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "storeDataInArrays: There is no data");
        } else {
            while (cursor.moveToNext()){
                event_id.add(cursor.getString(0));
                event_title.add(cursor.getString(1));
                event_location.add(cursor.getString(2));
                event_note.add(cursor.getString(3));
                event_color_picker.add(Integer.parseInt(cursor.getString(4)));
                event_avatar_picker.add(Integer.parseInt(cursor.getString(5)));
                event_start_year.add(Integer.parseInt(cursor.getString(6)));
                event_start_month.add(Integer.parseInt(cursor.getString(7)));
                event_start_day.add(Integer.parseInt(cursor.getString(8)));
                event_start_hour.add(Integer.parseInt(cursor.getString(9)));
                event_start_minutes.add(Integer.parseInt(cursor.getString(10)));
                event_end_year.add(Integer.parseInt(cursor.getString(11)));
                event_end_month.add(Integer.parseInt(cursor.getString(12)));
                event_end_day.add(Integer.parseInt(cursor.getString(13)));
                event_end_hour.add(Integer.parseInt(cursor.getString(14)));
                event_end_minutes.add(Integer.parseInt(cursor.getString(15)));
                event_all_day.add(Integer.parseInt(cursor.getString(16)));
                event_sound_notifications.add(Integer.parseInt(cursor.getString(17)));
                event_silent_notifications.add(Integer.parseInt(cursor.getString(18)));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Navigation menu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.about: {
                InfoPopupFragment infoPopupFragment = new InfoPopupFragment();
                infoPopupFragment.show(getSupportFragmentManager(), "InfoPopupFragment");
                break;
            }
            case R.id.exit: {
                finish();
                System.exit(0);
                break;
            }
            default: {
                //drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all:
                confirmDialog();
                return true;
            case R.id.options:
                Intent intent = new Intent(MainActivity.this, OptionActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( R.string.alert_dialog_title );
        builder.setMessage( R.string.alert_dialog_message_dell );
        builder.setPositiveButton( R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
                myDB.deleteAllData();
                //Refresh activity
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton( R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    //Drag and Drop Items
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
          long id = viewHolder.getAbsoluteAdapterPosition();
          String idS = String.valueOf(id);

            MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);

            myDB.deleteDataOnOneRow(idS);
//            txtBookId

            event_id.remove(viewHolder.getAbsoluteAdapterPosition());
            String delItem = String.valueOf(event_id);
//            swipeDeleteItem(delItem);
            customAdapter.notifyDataSetChanged();
        }
    };

    private void loadTutorial(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        boolean checkTutorial = (sharedPreferences.getBoolean(DISABLE_TUTORIAL, false));
        if (!checkTutorial){
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        }
    }

    //Load Theme Setting
    private void skinTheme(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        if(sharedPreferences.getBoolean(SWITCH_DARK_MODE, false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        getDelegate().applyDayNight();

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

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            drawerLayout.openDrawer(GravityCompat.START);
        }
//        super.onBackPressed();
    }
}