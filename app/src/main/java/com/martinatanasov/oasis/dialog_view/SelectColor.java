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

package com.martinatanasov.oasis.dialog_view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.martinatanasov.oasis.R;
import com.martinatanasov.oasis.util.ColorUtil;

public class SelectColor extends AppCompatDialogFragment {

    private int colorPosition = 0;
    private ApplyColor listener;

    public void colorInit(int color) {
        this.colorPosition = color;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            colorPosition = savedInstanceState.getInt("color_position");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        assert getActivity() != null;
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.pickup_color, null);

        RecyclerView recyclerViewColors = view.findViewById(R.id.recyclerViewColors);
        String[] colorNames = getResources().getStringArray(R.array.color_picker_array);

        ColorAdapter adapter = new ColorAdapter(ColorUtil.getColorCount(), colorNames, colorPosition, newPosition -> colorPosition = newPosition);

        int spanCount = getResources().getInteger(R.integer.color_picker_span_count);
        recyclerViewColors.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        recyclerViewColors.setAdapter(adapter);
        if (colorPosition >= 0 && colorPosition < ColorUtil.getColorCount()) {
            recyclerViewColors.scrollToPosition(colorPosition);
        }

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

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color_position", colorPosition);
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

    private static class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

        private final int totalColors;
        private final String[] colorNames;
        private final OnColorClickListener clickListener;
        private final TypedValue badgeStyleTypedValue = new TypedValue();
        private int selectedPosition;

        public ColorAdapter(int totalColors, String[] colorNames, int initialPosition, OnColorClickListener clickListener) {
            this.totalColors = totalColors;
            this.colorNames = colorNames;
            this.selectedPosition = initialPosition;
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_picker, parent, false);
            Context context = parent.getContext();
            if (context != null) {
                context.getTheme().resolveAttribute(R.attr.badgeStyle, badgeStyleTypedValue, true);
            }
            return new ColorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
            if (position == 0) {
                if (badgeStyleTypedValue.resourceId != 0) {
                    holder.colorView.setBackgroundResource(badgeStyleTypedValue.resourceId);
                } else if (badgeStyleTypedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && badgeStyleTypedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                    holder.colorView.setBackgroundColor(badgeStyleTypedValue.data);
                } else {
                    holder.colorView.setBackgroundResource(R.color.gray_new);
                }
                holder.colorView.setImageResource(R.drawable.ic_default_x);
                holder.colorView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } else {
                holder.colorView.setBackgroundResource(ColorUtil.getEventColorRes(position));
                holder.colorView.setImageDrawable(null);
            }

            if (colorNames != null && position < colorNames.length) {
                holder.colorView.setContentDescription(colorNames[position]);
            }

            if (position == selectedPosition) {
                holder.boxView.setBackgroundResource(R.drawable.rounded_bg_transparent);
            } else {
                holder.boxView.setBackgroundResource(android.R.color.transparent);
            }

            holder.itemView.setOnClickListener(v -> {
                int adapterPos = holder.getBindingAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    int previousSelected = selectedPosition;
                    selectedPosition = adapterPos;
                    if (previousSelected != RecyclerView.NO_POSITION) {
                        notifyItemChanged(previousSelected);
                    }
                    notifyItemChanged(selectedPosition);
                    if (clickListener != null) {
                        clickListener.onColorClick(selectedPosition);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return totalColors;
        }

        interface OnColorClickListener {

            void onColorClick(int position);

        }

        static class ColorViewHolder extends RecyclerView.ViewHolder {

            ImageView boxView;
            ImageView colorView;

            public ColorViewHolder(@NonNull View itemView) {
                super(itemView);
                boxView = itemView.findViewById(R.id.boxView);
                colorView = itemView.findViewById(R.id.colorView);
            }

        }

    }
}
