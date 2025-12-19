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

package com.martinatanasov.colornotebook.services;

import android.net.Uri;

import com.martinatanasov.colornotebook.dto.LocationResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapService {

    private static final String BASE_URL = "https://nominatim.openstreetmap.org/search";
    private final OkHttpClient client = new OkHttpClient();

    public LocationResult search(String query) throws Exception {
        List<LocationResult> results = suggest(query, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<LocationResult> suggest(String query, int limit) throws Exception {
        List<LocationResult> suggestions = new ArrayList<>();
        String url = BASE_URL
                + "?q=" + Uri.encode(query)
                + "&format=json"
                + "&addressdetails=0"
                + "&limit=" + limit;
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Android")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                return suggestions;
            }
            JSONArray results = new JSONArray(response.body().string());
            for (int i = 0; i < results.length(); i++) {
                JSONObject place = results.getJSONObject(i);

                double lat = place.getDouble("lat");
                double lon = place.getDouble("lon");
                String locationName = place.optString("display_name");

                suggestions.add(new LocationResult(lat, lon, locationName));
            }
        }
        return suggestions;
    }

}
