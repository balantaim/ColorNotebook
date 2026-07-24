package com.martinatanasov.colornotebook.views.map;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.dto.LocationResult;
import com.martinatanasov.colornotebook.repositories.PreferencesManager;
import com.martinatanasov.colornotebook.services.MapService;
import com.martinatanasov.colornotebook.utils.AppSettings;
import com.martinatanasov.colornotebook.utils.ScreenManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MapActivity extends AppCompatActivity implements AppSettings {

    private MapView mapView;
    private AutoCompleteTextView searchEditText;
    private Button searchButton;
    private Marker marker;
    private String location = "";
    private MapService mapService;
    private Button confirmButton;
    private Runnable searchRunnable;
    private static final String TAG = "MapActivity";
    private static final long DELAY_BEFORE_RETRY_IN_MILLISECONDS = 800;
    private static final int QUERY_LIMIT = 5;
    private static final double DEFAULT_LOCATION_ZOOM = 10.0D;
    private static final double FOCUSED_LOCATION_ZOOM = 18.0D;
    private static final String KEY_LOCATION_NAME = "key_location_name";
    private static final String KEY_LATITUDE = "key_latitude";
    private static final String KEY_LONGITUDE = "key_longitude";
    private static final String KEY_ZOOM_LEVEL = "key_zoom_level";
    private static final String KEY_HAS_MARKER = "key_has_marker";
    private final Handler searchHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load skin resource
        updateAppSettings();
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        initScreenManager();
        MaterialToolbar toolbar = findViewById(R.id.toolbar_search_map);
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_custom_arrow);
        }

        // Load OSM configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        initViews();
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
        initAdapter();
        setOnClickListeners();
    }

    @Override
    public void updateAppSettings() {
        PreferencesManager preferencesManager = new PreferencesManager(this);
        int theme = preferencesManager.getCurrentTheme();
        switch (theme) {
            case 1 -> setTheme(R.style.Theme_BlueColorNotebook);
            case 2 -> setTheme(R.style.Theme_DarkColorNotebook);
            default -> setTheme(R.style.Theme_DefaultColorNotebook);
        }
    }

    private void initAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        this.searchEditText.setAdapter(adapter);

        this.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> loadSuggestions(s.toString(), suggestions -> {
                    adapter.clear();
                    adapter.addAll(suggestions);
                    adapter.notifyDataSetChanged();

                    if (!suggestions.isEmpty()) {
                        searchEditText.showDropDown();
                    }
                });
                searchHandler.postDelayed(searchRunnable, DELAY_BEFORE_RETRY_IN_MILLISECONDS);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchLocation(String query) {
        if (query == null || query.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_location, Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            try {
                LocationResult result = mapService.search(query);
                if (result != null) {
                    runOnUiThread(() -> {
                        GeoPoint point = new GeoPoint(
                                result.lat(),
                                result.lon()
                        );

                        mapView.getController().setCenter(point);
                        mapView.getController().setZoom(FOCUSED_LOCATION_ZOOM);

                        setMarker(point, result.locationName());
                        location = result.locationName();
                        mapView.invalidate();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MapActivity.this, R.string.toast_not_found_location, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "searchLocation: Error: ", e);
                runOnUiThread(() -> Toast.makeText(MapActivity.this, R.string.toast_error_search_location, Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loadSuggestions(String query, Consumer<List<String>> callback) {
        if (query.length() < 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                callback.accept(new ArrayList<>());
            }
            return;
        }
        new Thread(() -> {
            try {
                List<LocationResult> results = mapService.suggest(query, QUERY_LIMIT);
                List<String> names = new ArrayList<>();
                for (LocationResult r : results) {
                    names.add(r.locationName());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    runOnUiThread(() -> {
                        if (searchEditText.getText().toString().equals(query)) {
                            callback.accept(names);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "loadSuggestions: Error: ", e);
            }
        }).start();
    }

    private void setMarker(GeoPoint point, String query) {
        //Remove previous marker
        if (marker != null) {
            mapView.getOverlays().remove(marker);
        }
        //Set new marker position
        assert marker != null;
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(query);
        mapView.getOverlays().add(marker);
    }

    private void initScreenManager() {
        new ScreenManager(findViewById(R.id.root_layout_map),
                getWindow(),
                false);
    }

    private void setOnClickListeners() {
        searchButton.setOnClickListener(v -> searchLocation(searchEditText.getText().toString()));
        this.searchEditText.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            searchLocation(selected);
        });
        confirmButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            String finalLocation = location.isEmpty() ? searchEditText.getText().toString() : location;
            resultIntent.putExtra("location", finalLocation);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void initViews() {
        mapView = findViewById(R.id.mapView);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        confirmButton = findViewById(R.id.confirmButton);
        mapService = new MapService(getString(R.string.app_name_full));
        marker = new Marker(mapView);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(DEFAULT_LOCATION_ZOOM);
        //Set default location (Central Europe)
        mapController.setCenter(new GeoPoint(48.8566, 2.3522));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_LOCATION_NAME, location);
        outState.putDouble(KEY_LATITUDE, mapView.getMapCenter().getLatitude());
        outState.putDouble(KEY_LONGITUDE, mapView.getMapCenter().getLongitude());
        outState.putDouble(KEY_ZOOM_LEVEL, mapView.getZoomLevelDouble());
        outState.putBoolean(KEY_HAS_MARKER, mapView.getOverlays().contains(marker));
    }

    private void restoreState(Bundle savedInstanceState) {
        location = savedInstanceState.getString(KEY_LOCATION_NAME, "");
        double lat = savedInstanceState.getDouble(KEY_LATITUDE);
        double lon = savedInstanceState.getDouble(KEY_LONGITUDE);
        double zoom = savedInstanceState.getDouble(KEY_ZOOM_LEVEL, DEFAULT_LOCATION_ZOOM);
        boolean hasMarker = savedInstanceState.getBoolean(KEY_HAS_MARKER, false);

        GeoPoint point = new GeoPoint(lat, lon);
        mapView.getController().setCenter(point);
        mapView.getController().setZoom(zoom);

        if (hasMarker) {
            setMarker(point, location);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

}