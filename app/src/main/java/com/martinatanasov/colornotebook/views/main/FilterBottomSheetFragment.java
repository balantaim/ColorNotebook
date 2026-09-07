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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.martinatanasov.colornotebook.R;
import com.martinatanasov.colornotebook.viewmodels.MainViewModel;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private MainViewModel viewModel;

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_filter_modal, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        RadioGroup rgOrder = view.findViewById(R.id.rgOrder);
        RadioGroup rgPriority = view.findViewById(R.id.rgPriority);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        // Set initial state
        OrderFilter currentOrder = viewModel.orderFilter.getValue();
        if (currentOrder != null) {
            switch (currentOrder) {
                case A_Z -> rgOrder.check(R.id.rbAZ);
                case Z_A -> rgOrder.check(R.id.rbZA);
                case DATE -> rgOrder.check(R.id.rbDate);
                case REVERSE_DATE -> rgOrder.check(R.id.rbReverseDate);
            }
        }

        PriorityFilter currentPriority = viewModel.priorityFilter.getValue();
        if (currentPriority != null) {
            switch (currentPriority) {
                case NONE -> rgPriority.check(R.id.rbNone);
                case IMPORTANT -> rgPriority.check(R.id.rbImportant);
                case REGULAR -> rgPriority.check(R.id.rbRegular);
                case UNIMPORTANT -> rgPriority.check(R.id.rbUnimportant);
            }
        }

        rgOrder.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAZ) {
                viewModel.setOrderFilter(OrderFilter.A_Z);
            } else if (checkedId == R.id.rbZA) {
                viewModel.setOrderFilter(OrderFilter.Z_A);
            } else if (checkedId == R.id.rbDate) {
                viewModel.setOrderFilter(OrderFilter.DATE);
            } else if (checkedId == R.id.rbReverseDate) {
                viewModel.setOrderFilter(OrderFilter.REVERSE_DATE);
            }
        });

        rgPriority.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbNone) {
                viewModel.setPriorityFilter(PriorityFilter.NONE);
            } else if (checkedId == R.id.rbImportant) {
                viewModel.setPriorityFilter(PriorityFilter.IMPORTANT);
            } else if (checkedId == R.id.rbRegular) {
                viewModel.setPriorityFilter(PriorityFilter.REGULAR);
            } else if (checkedId == R.id.rbUnimportant) {
                viewModel.setPriorityFilter(PriorityFilter.UNIMPORTANT);
            }
        });
    }

}
