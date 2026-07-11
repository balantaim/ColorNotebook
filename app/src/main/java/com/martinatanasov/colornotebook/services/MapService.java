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

import com.martinatanasov.colornotebook.dto.LocationResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapService {

    private static final String BASE_URL = "https://nominatim.openstreetmap.org/search";
    private final OkHttpClient client = new OkHttpClient();
    private final String userAgent;

    public MapService(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocationResult search(String query) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        List<LocationResult> results = suggest(query, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<LocationResult> suggest(String query, int limit) throws Exception {
        List<LocationResult> suggestions = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return suggestions;
        }

        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("format", "json")
                .addQueryParameter("addressdetails", "0")
                .addQueryParameter("limit", String.valueOf(limit))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return suggestions;
            }
            String bodyString = response.body().string();
            if (bodyString == null || !bodyString.trim().startsWith("[")) {
                return suggestions;
            }
            JSONArray results = new JSONArray(bodyString);
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
