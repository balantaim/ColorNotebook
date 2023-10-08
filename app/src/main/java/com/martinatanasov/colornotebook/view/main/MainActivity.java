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

package com.martinatanasov.colornotebook.view.main;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.controller.MainActivityController;
import com.martinatanasov.colornotebook.model.MyDatabaseHelper;
import com.martinatanasov.colornotebook.model.UserEvent;
import com.martinatanasov.colornotebook.services.MyForegroundServices;
import com.martinatanasov.colornotebook.tools.PreferencesManager;
import com.martinatanasov.colornotebook.view.add.AddActivity;
import com.martinatanasov.colornotebook.view.chart.ChartActivity;
import com.martinatanasov.colornotebook.view.option.OptionActivity;
import com.martinatanasov.colornotebook.view.tutorial.TutorialActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MainActivityController controller;
    RecyclerView recyclerView;
    FloatingActionButton scroll_top;
    ExtendedFloatingActionButton add_button;
    CustomAdapter customAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MyDatabaseHelper myDB;
    TextView counter, activeAlarms, importantEvents, regularEvents, lowPriorityEvents;
    private static ItemTouchHelper.SimpleCallback itemTouchHelperCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load skin resource
        skinTheme();

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        initViews();
        setNavigationViewListener();

        add_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        });
        //Make navigation drawer responsive
        navigationView.bringToFront();

        myDB = new MyDatabaseHelper(MainActivity.this);

        //storeDataInArrays();
        //customAdapter = new CustomAdapter(MainActivity.this, this, storeDataInObjects());

        //Swipe to delete
        /*
        swipeAction();
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

         */

        //createDrawerCounters();
    }
    public void createDrawerCounters(int important, int regular, int unimportant, int  sound_notifications, int sizeCount){
        //Create drawer menu counters
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        counter = (TextView) layoutInflater.inflate(R.layout.drawer_counter, null);
        activeAlarms = (TextView) layoutInflater.inflate(R.layout.drawer_counter, null);
        importantEvents = (TextView) layoutInflater.inflate(R.layout.drawer_counter, null);
        regularEvents = (TextView) layoutInflater.inflate(R.layout.drawer_counter, null);
        lowPriorityEvents = (TextView) layoutInflater.inflate(R.layout.drawer_counter, null);
        navigationView.getMenu().findItem(R.id.totalCount).setActionView(counter);
        navigationView.getMenu().findItem(R.id.activeAlarms).setActionView(activeAlarms);
        navigationView.getMenu().findItem(R.id.importantEvents).setActionView(importantEvents);
        navigationView.getMenu().findItem(R.id.regularEvents).setActionView(regularEvents);
        navigationView.getMenu().findItem(R.id.lowPriorityEvents).setActionView(lowPriorityEvents);
        updateDrawerCounter(counter, activeAlarms, importantEvents, regularEvents, lowPriorityEvents, //view
                important, regular, unimportant,  sound_notifications, sizeCount ); //variables
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            final Animator animStart = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.rotate_back);
            final Animator animEnd = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.rotate_start);

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

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
            public void onDrawerStateChanged(int newState) {
            }
        });
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
    public static void updateDrawerCounter(TextView counter, TextView activeAlarms, TextView importantEvents,
                                           TextView regularEvents, TextView lowPriorityEvents,
                                           int important, int regular, int unimportant,
                                           int sound_notifications, int sizeCount) {
        formatCount(counter, sizeCount);
        formatCount(activeAlarms, sound_notifications);
        formatCount(importantEvents, important);
        formatCount(regularEvents, regular);
        formatCount(lowPriorityEvents, unimportant);
    }

    //Update date after move from UpdateAct to MainAct
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
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
                if (newText != null) {
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
            case R.id.events_chart: {
                controller.initiateChartFragment();
                break;
            }
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

    public void openChartFragment(int important, int regular, int unimportant) {
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("important", Integer.toString(important));
        intent.putExtra("regular", Integer.toString(regular));
        intent.putExtra("unimportant", Integer.toString(unimportant));
        startActivity(intent);
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
    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_title);
        builder.setMessage(R.string.alert_dialog_message_dell);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                controller.deleteBDRecords();
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
    private void swipeAction() {
        //Drag and Drop Items
        itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //ToDo empty list
                List<String> event_id = new ArrayList<>();

                long id = viewHolder.getAbsoluteAdapterPosition();
                String idString = String.valueOf(id);
                controller.removeRowOnSwipe(idString);

//            txtBookId
                event_id.remove(viewHolder.getAbsoluteAdapterPosition());
                String delItem = String.valueOf(event_id);
//            swipeDeleteItem(delItem);
                customAdapter.notifyDataSetChanged();
            }
        };
    }

    public void loadTutorial() {
        startActivity(new Intent(MainActivity.this, TutorialActivity.class));
    }
    //Start Foreground Services
    public void startForegroundService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (!isForegroundServiceRunning()) {
//                Intent serviceIntent = new Intent(this, MyForegroundServices.class);
//                startForegroundService(serviceIntent);
//            }
        }
    }
    private boolean isForegroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyForegroundServices.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void setUpRecyclerView(List<UserEvent> data) {
        customAdapter = new CustomAdapter(MainActivity.this, this, data);
        runOnUiThread(() -> {
            //Swipe to delete
            swipeAction();
            //SimpleCallback - Drag and Drop function
            //new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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
        });
    }
    public void emptyDB() {
        //TODO shine animation
        Log.d("MainActivityController", "storeDataInArrays: There is no data");
    }
    public void shrinkMenuButton() {
        add_button.shrink();
    }
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        scroll_top = findViewById(R.id.scrollTop);
        drawerLayout = findViewById(R.id.layoutDrawer);
        navigationView = findViewById(R.id.navDrawer);
        controller = new MainActivityController(this);
    }
    //Check if Night mode is activated
    private void darkModeChecker(PreferencesManager preferencesManager){
        if (preferencesManager.getForceDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        getDelegate().applyDayNight();
    }
    //Load Theme Setting
    private void skinTheme(){
        PreferencesManager preferencesManager = new PreferencesManager(this, true, false);
        darkModeChecker(preferencesManager);
                switch (preferencesManager.getCurrentTheme()) {
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
