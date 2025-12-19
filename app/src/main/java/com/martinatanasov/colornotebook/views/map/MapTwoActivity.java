package com.martinatanasov.colornotebook.views.map;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.dto.LocationResult;
import com.martinatanasov.colornotebook.services.MapService;
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

public class MapTwoActivity extends AppCompatActivity {

    private final MapService mapService = new MapService();
    private MapView mapView;
    private AutoCompleteTextView searchEditText;
    private Button searchButton;
    private Marker marker;
    private String location = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_two);

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

        mapView = findViewById(R.id.mapView);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        marker = new Marker(mapView);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(10.0);
        mapController.setCenter(new GeoPoint(48.8566, 2.3522));

        initAdapter();
        searchButton.setOnClickListener(v -> searchLocation(searchEditText.getText().toString()));
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
                loadSuggestions(s.toString(), suggestions -> {
                    adapter.clear();
                    adapter.addAll(suggestions);
                    adapter.notifyDataSetChanged();

                    searchEditText.showDropDown();
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchLocation(String query) {
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
                        mapView.getController().setZoom(18.0);

                        setMarker(point, result.locationName());
                        location = result.locationName();
                        mapView.invalidate();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                List<LocationResult> results = mapService.suggest(query, 5);
                List<String> names = new ArrayList<>();
                for (LocationResult r : results) {
                    names.add(r.locationName());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    runOnUiThread(() -> callback.accept(names));
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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