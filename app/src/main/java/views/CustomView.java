package views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.martinatanasov.colornotebook.R;

public class CustomView extends Dialog {

    ConstraintLayout setImportant, setRegular, setUnimportant;
    Dialog priorityDialog;
    ApplyPriority listener;

    public CustomView(@NonNull Context context) {
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
            ;priorityDialog.dismiss();}
        );
        priorityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        priorityDialog.show();
    }
    public void setDialogResult(ApplyPriority dialogResult){
        listener = dialogResult;
    }
}
