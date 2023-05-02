package com.martinatanasov.colornotebook.dialog_views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.martinatanasov.colornotebook.R;

public class SelectColor extends AppCompatDialogFragment {

    ConstraintLayout setImportant, setRegular, setUnimportant;
    private static int status = 1;
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
                        //listener.setColor(status);
                    }
                });
//        setImportant = (ConstraintLayout) view.findViewById(R.id.setImportant);
//        setRegular = (ConstraintLayout) view.findViewById(R.id.setRegular);
//        setUnimportant = (ConstraintLayout) view.findViewById(R.id.setUnimportant);

        //onClick(view);
//        setImportant.setOnClickListener(v -> status = 0);
//        setRegular.setOnClickListener(v -> status = 1);
//        setUnimportant.setOnClickListener(v -> status = 2);

        return builder.create();
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
