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

package com.martinatanasov.colornotebook.dialog_views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.martinatanasov.colornotebook.R;

public class SelectColor extends AppCompatDialogFragment {

    ImageView colorOne, colorTwo, colorThree, colorFour, colorFive,
            colorSix, colorSeven, colorEight, colorNine;
    ImageView boxOne, boxTwo, boxThree, boxFour, boxFive, boxSix, boxSeven, boxEight, boxNine;

    private int colorPosition = 0;
    private ApplyColor listener;

    public SelectColor(int color) {
        this.colorPosition = color;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.pickup_color, null);

        builder.setView(view)
                .setTitle(R.string.pickColor)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.setColor(colorPosition);
                    }
                });
        initView(view);
        setFocusedPosition(colorPosition, -1);
        selectColor();

        return builder.create();
    }

    private void selectColor() {
        colorOne.setOnClickListener(v -> {
            setFocusedPosition(0, colorPosition);
            colorPosition = 0;
        });
        colorTwo.setOnClickListener(v -> {
            setFocusedPosition(1, colorPosition);
            colorPosition = 1;
        });
        colorThree.setOnClickListener(v -> {
            setFocusedPosition(2, colorPosition);
            colorPosition = 2;
        });
        colorFour.setOnClickListener(v -> {
            setFocusedPosition(3, colorPosition);
            colorPosition = 3;
        });
        colorFive.setOnClickListener(v -> {
            setFocusedPosition(4, colorPosition);
            colorPosition = 4;
        });
        colorSix.setOnClickListener(v -> {
            setFocusedPosition(5, colorPosition);
            colorPosition = 5;
        });
        colorSeven.setOnClickListener(v -> {
            setFocusedPosition(6, colorPosition);
            colorPosition = 6;
        });
        colorEight.setOnClickListener(v -> {
            setFocusedPosition(7, colorPosition);
            colorPosition = 7;
        });
        colorNine.setOnClickListener(v -> {
            setFocusedPosition(8, colorPosition);
            colorPosition = 8;
        });
    }
    private void setFocusedPosition(int newPosition, int lastPosition){
        ImageView imageView;
        if(lastPosition == -1){
            imageView = getSelectedView(newPosition);
            imageView.setBackgroundResource(R.drawable.rounded_bg_transparent);
            return;
        }
        if(newPosition != lastPosition){
            imageView = getSelectedView(lastPosition);
            imageView.setBackgroundResource(android.R.color.transparent);
            imageView = getSelectedView(newPosition);
            imageView.setBackgroundResource(R.drawable.rounded_bg_transparent);
        }
    }

    /*
    private void testAnime(ImageView imageView){
        //Animation drawable
        AnimatedVectorDrawableCompat avdCompat;
        AnimatedVectorDrawable avd;
        Drawable drawable = imageView.getDrawable();
        if(drawable instanceof AnimatedVectorDrawableCompat){
            avdCompat = (AnimatedVectorDrawableCompat) drawable;
            avdCompat.start();
        }else if(drawable instanceof AnimatedVectorDrawable){
            avd = (AnimatedVectorDrawable) drawable;
            avd.start();
        }
    }
    */

    private ImageView getSelectedView(int status){
        ImageView result;
        switch (status){
            case 1:
                result = boxTwo;
                break;
            case 2:
                result = boxThree;
                break;
            case 3:
                result = boxFour;
                break;
            case 4:
                result = boxFive;
                break;
            case 5:
                result = boxSix;
                break;
            case 6:
                result = boxSeven;
                break;
            case 7:
                result = boxEight;
                break;
            case 8:
                result = boxNine;
                break;
            default:
                result = boxOne;
        }
        return result;
    }

    private void initView(View view) {
        colorOne = view.findViewById(R.id.colDefault);
        colorTwo = view.findViewById(R.id.colSkyBlue);
        colorThree = view.findViewById(R.id.colGreen);
        colorFour = view.findViewById(R.id.colYellow);
        colorFive = view.findViewById(R.id.colOrange);
        colorSix = view.findViewById(R.id.colRed);
        colorSeven = view.findViewById(R.id.colBlue);
        colorEight = view.findViewById(R.id.colPurple);
        colorNine = view.findViewById(R.id.colGray);
        //Selected item
        boxOne = view.findViewById(R.id.boxDefault);
        boxTwo = view.findViewById(R.id.boxSkyBlue);
        boxThree = view.findViewById(R.id.boxGreen);
        boxFour = view.findViewById(R.id.boxYellow);
        boxFive = view.findViewById(R.id.boxOrange);
        boxSix = view.findViewById(R.id.boxRed);
        boxSeven = view.findViewById(R.id.boxBlue);
        boxEight = view.findViewById(R.id.boxPurple);
        boxNine = view.findViewById(R.id.boxGray);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ApplyColor) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "Implement dialog listener");
        }
    }
}
