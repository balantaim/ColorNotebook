package com.martinatanasov.colornotebook.tools;

import androidx.appcompat.app.ActionBar;

import com.martinatanasov.colornotebook.R;

public class Tools {
    public void setArrowBackIcon(ActionBar supportActionBar) {
        assert supportActionBar != null;
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_custom_arrow);
    }
}
