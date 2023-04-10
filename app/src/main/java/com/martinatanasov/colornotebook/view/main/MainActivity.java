package com.martinatanasov.colornotebook.view.main;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.data.UserEvent;
import com.martinatanasov.colornotebook.model.MyDatabaseHelper;
import com.martinatanasov.colornotebook.services.MyForegroundServices;
import com.martinatanasov.colornotebook.view.add.AddActivity;
import com.martinatanasov.colornotebook.view.option.OptionActivity;
import com.martinatanasov.colornotebook.view.tutorial.TutorialActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    FloatingActionButton add_button, scroll_top;
    CustomAdapter customAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MyDatabaseHelper myDB;
    private static TextView counter, activeAlarms;
    private static ArrayList<String> event_id;
    private static ArrayList<String> event_title;
    private static ArrayList<String> event_location;
    private static ArrayList<String> event_note;
    //Secondary data parameters
    private static ArrayList<Integer> event_color_picker;
    private static ArrayList<Integer> event_avatar_picker;
    private static ArrayList<Integer> event_start_year;
    private static ArrayList<Byte> event_start_month;
    private static ArrayList<Byte> event_start_day;
    private static ArrayList<Byte> event_start_hour;
    private static ArrayList<Byte> event_start_minutes;
    private static ArrayList<Integer> event_end_year;
    private static ArrayList<Byte> event_end_month;
    private static ArrayList<Byte> event_end_day;
    private static ArrayList<Byte> event_end_hour;
    private static ArrayList<Byte> event_end_minutes;
    private static ArrayList<Long> event_created_date;
    private static ArrayList<Long> event_modified_date;
    private static ArrayList<Integer> event_all_day;
    private static ArrayList<Integer> event_sound_notifications;
    private static ArrayList<Integer> event_silent_notifications;
    private static final String SHARED_PREF = "sharedPref";
    private static final String THEME = "theme";
    private static final String TXT_SIZE = "txtSize";
    private static final String SWITCH_DARK_MODE = "switchDarkMode";
    private static final String DISABLE_TUTORIAL = "disableTutorial";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load tutorial status
        loadTutorial();
        //Load skin resource
        skinTheme();
        setContentView(R.layout.activity_main);
//        Day and Night modes
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        getDelegate().applyDayNight();

        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        scroll_top = findViewById(R.id.scrollTop);
        drawerLayout = findViewById(R.id.layoutDrawer);
        navigationView = findViewById(R.id.navDrawer);
        setNavigationViewListener();

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
        event_created_date = new ArrayList<>();
        event_modified_date = new ArrayList<>();
        event_all_day = new ArrayList<>();
        event_sound_notifications = new ArrayList<>();
        event_silent_notifications = new ArrayList<>();

        storeDataInArrays();
        //List<UserEvent> dataItems = storeDataInObjects();
        customAdapter = new CustomAdapter(MainActivity.this, this, storeDataInObjects());

        //SimpleCallback - Drag and Drop function
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //recycler view Layout
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    scroll_top.setVisibility(View.VISIBLE);
                } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                    scroll_top.setVisibility(View.GONE);
                }
            }
        });
        scroll_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        //Create drawer menu counters
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        counter = (TextView) layoutInflater.inflate(R.layout.drawer_counter, null);
        activeAlarms = (TextView) layoutInflater.inflate(R.layout.drawer_counter, null);
        navigationView.getMenu().findItem(R.id.totalCount).setActionView(counter);
        navigationView.getMenu().findItem(R.id.activeAlarms).setActionView(activeAlarms);
        updateDrawerCounter();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            final Animator animStart = AnimatorInflater.loadAnimator(getApplicationContext(),R.animator.rotate_back);
            final Animator animEnd = AnimatorInflater.loadAnimator(getApplicationContext(),R.animator.rotate_start);
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                animStart.setTarget(recyclerView);
                animStart.start();
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                animEnd.setTarget(recyclerView);
                animEnd.start();
            }
            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        //Start Foreground Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isForegroundServiceRunning()) {
                Intent serviceIntent = new Intent(this, MyForegroundServices.class);
                startForegroundService(serviceIntent);
            }
        }
    }

    //Initiate Navigation item selection
    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressLint("SetTextI18n")
    private static void formatCount(TextView tv, int index) {
        if (index > 99) {
            tv.setText("+99");
        } else {
            tv.setText(index + "");
        }
    }

    public static void updateDrawerCounter() {
        int index = 0, alarm = 0;
        index = event_id.size();
        formatCount(counter, index);
        for (int i = 0; i < event_id.size(); i++) {
            if (event_sound_notifications.get(i) > 0) {
                alarm++;
            }
        }
        formatCount(activeAlarms, alarm);
    }

    //Update date after move from UpdateAct to MainAct
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
//            Toast.makeText(this, R.string.toast_no_data, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "storeDataInArrays: There is no data");
        } else {
            while (cursor.moveToNext()) {
                event_id.add(cursor.getString(0));
                event_title.add(cursor.getString(1));
                event_location.add(cursor.getString(2));
                event_note.add(cursor.getString(3));
                event_color_picker.add(Integer.parseInt(cursor.getString(4)));
                event_avatar_picker.add(Integer.parseInt(cursor.getString(5)));
                event_start_year.add(Integer.parseInt(cursor.getString(6)));
                event_start_month.add(Byte.parseByte(cursor.getString(7)));
                event_start_day.add(Byte.parseByte(cursor.getString(8)));
                event_start_hour.add(Byte.parseByte(cursor.getString(9)));
                event_start_minutes.add(Byte.parseByte(cursor.getString(10)));
                event_end_year.add(Integer.parseInt(cursor.getString(11)));
                event_end_month.add(Byte.parseByte(cursor.getString(12)));
                event_end_day.add(Byte.parseByte(cursor.getString(13)));
                event_end_hour.add(Byte.parseByte(cursor.getString(14)));
                event_end_minutes.add(Byte.parseByte(cursor.getString(15)));
                event_created_date.add(Long.parseLong(cursor.getString(16)));
                event_modified_date.add(Long.parseLong(cursor.getString(17)));
                event_all_day.add(Integer.parseInt(cursor.getString(18)));
                event_sound_notifications.add(Integer.parseInt(cursor.getString(19)));
                event_silent_notifications.add(Integer.parseInt(cursor.getString(20)));
            }
        }
    }
    private List<UserEvent> storeDataInObjects(){
        List<UserEvent> allDataInObjects = new ArrayList<>();
        for (int i = 0; i < event_id.size(); i++){
            allDataInObjects.add(new UserEvent(
                    event_id.get(i),
                    event_title.get(i),
                    event_location.get(i),
                    event_note.get(i),
                    event_color_picker.get(i),
                    event_avatar_picker.get(i),
                    event_start_year.get(i),
                    event_end_year.get(i),
                    event_all_day.get(i),
                    event_sound_notifications.get(i),
                    event_silent_notifications.get(i),
                    event_start_month.get(i),
                    event_start_day.get(i),
                    event_start_hour.get(i),
                    event_start_minutes.get(i),
                    event_end_month.get(i),
                    event_end_day.get(i),
                    event_end_hour.get(i),
                    event_end_minutes.get(i),
                    event_created_date.get(i),
                    event_modified_date.get(i)
            ));
        }
        return allDataInObjects;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null){
                    customAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //Navigation menu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.website: {
                Toast.makeText(this, "In development", Toast.LENGTH_SHORT).show();
                break;
            }
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
        switch (item.getItemId()) {
            case R.id.navigation_button:
                onBackPressed();
                return true;
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

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_title);
        builder.setMessage(R.string.alert_dialog_message_dell);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

    private void loadTutorial() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        boolean checkTutorial = (sharedPreferences.getBoolean(DISABLE_TUTORIAL, false));
        if (!checkTutorial) {
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        }
    }

    public boolean isForegroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyForegroundServices.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Load Theme Setting
    private void skinTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(SWITCH_DARK_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        getDelegate().applyDayNight();

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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
//        super.onBackPressed();
    }
}
