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

    //ConstraintLayout setImportant, setRegular, setUnimportant;
    ImageView colorOne, colorTwo, colorThree, colorFour, colorFive,
            colorSix, colorSeven, colorEight, colorNine;
    private static int colorPosition = 0;
    private ApplyColor listener;

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
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.setColor(colorPosition);
                    }
                });
        initView(view);
        selectColor();

        return builder.create();
    }

    private void selectColor() {
//        setImportant.setOnClickListener(v -> status = 0);
//        setRegular.setOnClickListener(v -> status = 1);
//        setUnimportant.setOnClickListener(v -> status = 2);
        colorOne.setOnClickListener(v -> colorPosition = 0);
        colorTwo.setOnClickListener(v -> colorPosition = 1);
        colorThree.setOnClickListener(v -> colorPosition = 2);
        colorFour.setOnClickListener(v -> colorPosition = 3);
        colorFive.setOnClickListener(v -> colorPosition = 4);
        colorSix.setOnClickListener(v -> colorPosition = 5);
        colorSeven.setOnClickListener(v -> colorPosition = 6);
        colorEight.setOnClickListener(v -> colorPosition = 7);
        colorNine.setOnClickListener(v -> colorPosition = 8);
    }

    private void initView(View view) {
//        setImportant = (ConstraintLayout) view.findViewById(R.id.setImportant);
//        setRegular = (ConstraintLayout) view.findViewById(R.id.setRegular);
//        setUnimportant = (ConstraintLayout) view.findViewById(R.id.setUnimportant);
        colorOne = view.findViewById(R.id.colDefault);
        colorTwo = view.findViewById(R.id.colSkyBlue);
        colorThree = view.findViewById(R.id.colGreen);
        colorFour = view.findViewById(R.id.colYellow);
        colorFive = view.findViewById(R.id.colOrange);
        colorSix = view.findViewById(R.id.colRed);
        colorSeven = view.findViewById(R.id.colBlue);
        colorEight = view.findViewById(R.id.colPurple);
        colorNine = view.findViewById(R.id.colGray);
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
