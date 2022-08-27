package com.martinatanasov.colornotebook;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class InfoPopupFragment extends DialogFragment {

    //Dialog dialogInfo;
    TextView txtDevelopers;
    Button checkDev;
    ConstraintLayout layoutPopup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.popup_info_fragment, container, false);
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        txtDevelopers= (TextView) view.findViewById(R.id.txtDevelopers);
        checkDev= (Button) view.findViewById(R.id.checkDev);
        layoutPopup= (ConstraintLayout) view.findViewById(R.id.layoutPopup);

        final String url = BuildConfig.CHECK_DEV + ".json";

        checkDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create http GET request for data using Volley library
                //RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response){
                        //Toast.makeText(getActivity().getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject objectAppList = response.getJSONObject(0);
                            String devName = objectAppList.getString("developer");
                            txtDevelopers.setText("Created by:\n\n" + devName
                                    + "\n\nVersion " + BuildConfig.VERSION_NAME);
                        } catch (JSONException e) {
                            txtDevelopers.setText(getResources().getString(R.string.error_404));
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtDevelopers.setText(getResources().getString(R.string.error_404));
                    }
                });
                //queue.add(request);
                MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
            }
        });
        layoutPopup.setOnClickListener(view1 -> dismiss());

        return view;
    }



}
