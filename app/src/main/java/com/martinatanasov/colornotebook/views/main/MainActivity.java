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

package com.martinatanasov.colornotebook.views.main;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.dto.UserEvent;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.services.RescheduleWorkerService;
import com.martinatanasov.colornotebook.utils.AppSettings;
import com.martinatanasov.colornotebook.utils.ScreenManager;
import com.martinatanasov.colornotebook.utils.events.VibrationUtil;
import com.martinatanasov.colornotebook.viewmodels.MainViewModel;
import com.martinatanasov.colornotebook.views.add.AddActivity;
import com.martinatanasov.colornotebook.views.chart.ChartActivity;
import com.martinatanasov.colornotebook.views.option.OptionActivity;
import com.martinatanasov.colornotebook.views.tutorial.TutorialActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AppSettings {

    private MainViewModel viewModel;
    RecyclerView recyclerView;
    FloatingActionButton scroll_top;
    ExtendedFloatingActionButton add_button;
    CustomAdapter customAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView counter, activeAlarms, importantEvents, regularEvents, lowPriorityEvents;
    private VibrationUtil vibration;
    private static ItemTouchHelper.SimpleCallback itemTouchHelperCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load skin resource
        updateAppSettings();

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //Set top toolbar
        initToolbar();
        //hide Status Bar
        initScreenManager();
        initViews();
        setNavigationViewListener();
        addClickListeners();
        //Make navigation drawer responsive
        navigationView.bringToFront();

        initObservers();

        if (viewModel.shouldShowTutorial()) {
            loadTutorial();
        } else {
            viewModel.init();
            viewModel.loadData();
            rescheduleWork();
        }
    }

    private void initObservers() {
        viewModel.filteredEvents.observe(this, events -> {
            if (customAdapter == null) {
                setUpRecyclerView(events);
            } else {
                customAdapter.updateData(events);
            }

            if (events.isEmpty()) {
                extendMenuButton();
            } else {
                shrinkMenuButton();
            }
        });

        viewModel.importantCount.observe(this, imp -> updateCounters());
        viewModel.regularCount.observe(this, reg -> updateCounters());
        viewModel.unimportantCount.observe(this, uni -> updateCounters());
        viewModel.soundNotificationsCount.observe(this, sound -> updateCounters());
    }

    private void updateCounters() {
        Integer imp = viewModel.importantCount.getValue();
        Integer reg = viewModel.regularCount.getValue();
        Integer uni = viewModel.unimportantCount.getValue();
        Integer sound = viewModel.soundNotificationsCount.getValue();
        Integer total = viewModel.events.getValue() != null ? viewModel.events.getValue().size() : 0;

        if (imp != null && reg != null && uni != null && sound != null) {
            if (counter == null) {
                createDrawerCounters(imp, reg, uni, sound, total);
            } else {
                updateDrawerCounter(counter, activeAlarms, importantEvents, regularEvents, lowPriorityEvents,
                        imp, reg, uni, sound, total);
            }
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

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public CustomAdapter getAdapter() {
        return customAdapter;
    }

    private void initScreenManager() {
        new ScreenManager(findViewById(R.id.layoutDrawer),
                getWindow(),
                true);
    }

    private void addClickListeners() {
        add_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        });
    }

    public void createDrawerCounters(int important, int regular, int unimportant, int sound_notifications, int sizeCount) {
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
                important, regular, unimportant, sound_notifications, sizeCount); //variables
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

    @SuppressLint("SetTextI18n")
    private static void formatCount(TextView tv, int index) {
        if (index > 99) {
            tv.setText("+99");
        } else {
            tv.setText(index + "");
        }
    }

    //Initiate Navigation item selection
    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Update date after move from UpdateAct to MainAct
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    //Navigation menu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int itemId = item.getItemId();

        if (itemId == R.id.events_chart) {
            if (Boolean.FALSE.equals(viewModel.isDataEmpty.getValue())) {
                initiateChartFragment();
            } else {
                Toast.makeText(this, R.string.no_data_to_show, Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.website) {
            startActivity(viewModel.getWebsiteIntent());
            moveTaskToBack(true);
        } else if (itemId == R.id.about) {
            if (getSupportFragmentManager().findFragmentByTag("InfoPopupFragment") == null) {
                InfoPopupFragment infoPopupFragment = new InfoPopupFragment();
                infoPopupFragment.show(getSupportFragmentManager(), "InfoPopupFragment");
            }
        } else if (itemId == R.id.exit) {
            finishAffinity();
        } else {
            Log.e(getClass().getName(), "onNavigationItemSelected: Method NOT implemented!");
        }

        // Close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initiateChartFragment() {
        openChartFragment(
                viewModel.importantCount.getValue() != null ? viewModel.importantCount.getValue() : 0,
                viewModel.regularCount.getValue() != null ? viewModel.regularCount.getValue() : 0,
                viewModel.unimportantCount.getValue() != null ? viewModel.unimportantCount.getValue() : 0
        );
    }

    public void openChartFragment(int important, int regular, int unimportant) {
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("important", Integer.toString(important));
        intent.putExtra("regular", Integer.toString(regular));
        intent.putExtra("unimportant", Integer.toString(unimportant));
        startActivity(intent);
    }

    //Create top nav menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        MenuItem menuItemSearch = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItemSearch.getActionView();
        assert searchView != null;

        // Restore search query if it exists
        String currentQuery = viewModel.searchQuery.getValue();
        if (currentQuery != null && !currentQuery.isEmpty()) {
            menuItemSearch.expandActionView();
            searchView.setQuery(currentQuery, false);
            searchView.clearFocus();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null) {
                    viewModel.setSearchQuery(newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //Disable and Hide Menu buttons if there are no events
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (viewModel != null) {
            if (Boolean.TRUE.equals(viewModel.isDataEmpty.getValue())) {
                if (menu != null) {
                    MenuItem deleteAll = menu.findItem(R.id.delete_all);
                    if (deleteAll != null) {
                        deleteAll.setEnabled(false);
                        deleteAll.setVisible(false);
                    }
                    MenuItem search = menu.findItem(R.id.search);
                    if (search != null) {
                        search.setEnabled(false);
                        search.setVisible(false);
                    }
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_button) {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        } else if (itemId == R.id.delete_all) {
            confirmDialog();
            return true;
        } else if (itemId == R.id.options) {
            navigateToOptions();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToOptions() {
        // Inside your activity (if you did not enable transitions in your theme)
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        //getWindow().setExitTransition(R.anim.slide_in);
        Intent intent = new Intent(MainActivity.this, OptionActivity.class);
        startActivity(intent);
        //overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.out, R.anim.in);
        //overridePendingTransition(R.anim.out, R.anim.in);
    }

    private void confirmDialog() {
        //Add vibration effect
        vibration.vibrate();
        //Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_title);
        builder.setMessage(R.string.alert_dialog_message_dell);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            viewModel.deleteBatch();
            //Refresh activity
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {

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
                UserEvent event = Objects.requireNonNull(viewModel.events.getValue()).get((int) id);
                viewModel.removeEvent(event.txtEventId());

                customAdapter.notifyDataSetChanged();
            }
        };
    }

    public void loadTutorial() {
        startActivity(new Intent(MainActivity.this, TutorialActivity.class));
    }

    //Reschedule Alarms and Notifications
    public void rescheduleWork() {
        OneTimeWorkRequest rescheduleWork = new OneTimeWorkRequest.Builder(RescheduleWorkerService.class).build();
        WorkManager.getInstance(this).enqueue(rescheduleWork);
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
            scroll_top.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));
        });
    }

    public void printDatabaseEmpty() {
        Log.d("MainActivityController", "storeDataInArrays: There is no data");
    }

    public void shrinkMenuButton() {
        add_button.shrink();
    }

    public void extendMenuButton() {
        add_button.extend();
    }

    //Check if Night mode is activated
    private void darkModeChecker(PreferencesManager preferencesManager) {
        if (preferencesManager.getForceDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        getDelegate().applyDayNight();
    }

    //Load Theme Setting
    @Override
    public void updateAppSettings() {
        EdgeToEdge.enable(this);
        PreferencesManager preferencesManager = new PreferencesManager(this, true, false);
        darkModeChecker(preferencesManager);
        switch (preferencesManager.getCurrentTheme()) {
            case 1 -> setTheme(R.style.Theme_BlueColorNotebook);
            case 2 -> setTheme(R.style.Theme_DarkColorNotebook);
            default -> setTheme(R.style.Theme_DefaultColorNotebook);
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        scroll_top = findViewById(R.id.scrollTop);
        drawerLayout = findViewById(R.id.layoutDrawer);
        navigationView = findViewById(R.id.navDrawer);
        vibration = new VibrationUtil(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
