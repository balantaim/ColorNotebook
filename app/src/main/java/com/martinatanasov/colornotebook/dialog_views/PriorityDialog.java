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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.martinatanasov.colornotebook.R;

public class PriorityDialog extends Dialog {

    ConstraintLayout setImportant, setRegular, setUnimportant;
    Dialog priorityDialog;
    private ApplyPriority listener;

    public PriorityDialog(@NonNull Context context) {
        super(context);
    }

    public void setPriority(Activity activity, int position){
        priorityDialog = new Dialog(activity);
        priorityDialog.setContentView(R.layout.set_priority_dialog);
        setImportant = (ConstraintLayout) priorityDialog.findViewById(R.id.setImportant);
        setRegular = (ConstraintLayout) priorityDialog.findViewById(R.id.setRegular);
        setUnimportant = (ConstraintLayout) priorityDialog.findViewById(R.id.setUnimportant);

        switch(position){
            case 1:
                setRegular.setBackgroundResource(R.drawable.rounded_bg);
                break;
            case 2:
                setUnimportant.setBackgroundResource(R.drawable.rounded_bg);
                break;
            default:
                setImportant.setBackgroundResource(R.drawable.rounded_bg);
                break;
        }
        updateStatus();
        priorityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        priorityDialog.show();
    }

    private void updateStatus() {
        //OnClick listener for set priority
        setImportant.setOnClickListener(v -> {
            if(listener != null){
                listener.setPriority(0);
            }
            priorityDialog.dismiss();
        });
        setRegular.setOnClickListener(v -> {
            if(listener != null){
                listener.setPriority(1);
            }
            priorityDialog.dismiss();
        });
        setUnimportant.setOnClickListener(v -> {
            if(listener != null){
                listener.setPriority(2);
            }
            priorityDialog.dismiss();
        });
    }

    public void setDialogResult(ApplyPriority dialogResult){
        listener = dialogResult;
    }
}
