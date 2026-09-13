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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.martinatanasov.colornotebook.BuildConfig;
import com.martinatanasov.colornotebook.R;

import java.util.Objects;

public class InfoPopupFragment extends DialogFragment {

    TextView txtDevelopers;
    //Button checkDev;
    ConstraintLayout layoutPopup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.popup_info_fragment, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        txtDevelopers = view.findViewById(R.id.txtDevelopers);
        //checkDev = view.findViewById(R.id.checkDev);
        layoutPopup = view.findViewById(R.id.layoutPopup);
        setTextInfo();

        //loadInfo();
        layoutPopup.setOnClickListener(view1 -> dismiss());

        return view;
    }

    private void setTextInfo() {
        txtDevelopers.setText(getString(
                        R.string.about_dev_info,
                        getString(R.string.developer_name),
                        BuildConfig.VERSION_NAME
                )
        );
    }

//    @Deprecated(forRemoval = false)
//    private void loadInfo() {
//        final String url = BuildConfig.CHECK_DEV + ".json";
//        checkDev.setOnClickListener(view2 -> {
//            //Create http GET request for data using Volley library
//            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//                @Override
//                public void onResponse(JSONArray response) {
//                    //Toast.makeText(getActivity().getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
//                    try {
//                        JSONObject objectAppList = response.getJSONObject(0);
//                        String devName = objectAppList.getString("developer");
//                        txtDevelopers.setText(getString(R.string.about_dev_info, devName, BuildConfig.VERSION_NAME));
//                    } catch (JSONException e) {
//                        txtDevelopers.setText(getResources().getString(R.string.error_404));
//                        Log.e(this.getClass().getName(), "onResponse: cannot load the data for InfoPopupFragment", e);
//                    }
//                }
//            }, error -> txtDevelopers.setText(getResources().getString(R.string.error_404)));
//            //queue.add(request);
//            assert getActivity() != null;
//            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
//        });
//    }

}
