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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.martinatanasov.colornotebook.R;

public class PriorityDialog extends AppCompatDialogFragment {

    private ConstraintLayout setImportant, setRegular, setUnimportant;
    private ApplyPriority listener;
    private int priorityPosition = 1;

    public static PriorityDialog newInstance(int priority) {
        PriorityDialog frag = new PriorityDialog();
        Bundle args = new Bundle();
        args.putInt("priority", priority);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            priorityPosition = getArguments().getInt("priority");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_priority_dialog, null);

        initViews(view);
        highlightSelected(priorityPosition);
        setupClickListeners();

        builder.setView(view);
        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    private void initViews(View view) {
        setImportant = view.findViewById(R.id.setImportant);
        setRegular = view.findViewById(R.id.setRegular);
        setUnimportant = view.findViewById(R.id.setUnimportant);
    }

    private void highlightSelected(int position) {
        switch (position) {
            case 1 -> setRegular.setBackgroundResource(R.drawable.rounded_bg);
            case 2 -> setUnimportant.setBackgroundResource(R.drawable.rounded_bg);
            default -> setImportant.setBackgroundResource(R.drawable.rounded_bg);
        }
    }

    private void setupClickListeners() {
        setImportant.setOnClickListener(v -> {
            if (listener != null) {
                listener.setPriority(0);
            }
            dismiss();
        });
        setRegular.setOnClickListener(v -> {
            if (listener != null) {
                listener.setPriority(1);
            }
            dismiss();
        });
        setUnimportant.setOnClickListener(v -> {
            if (listener != null) {
                listener.setPriority(2);
            }
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ApplyPriority) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement ApplyPriority");
        }
    }
}
