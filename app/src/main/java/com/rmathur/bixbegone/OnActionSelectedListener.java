package com.rmathur.bixbegone;

import android.view.View;
import android.widget.AdapterView;

public class OnActionSelectedListener implements AdapterView.OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        PreferenceHelper prefHelper = new PreferenceHelper(view.getContext());
        prefHelper.setButtonAction(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // nothing
    }
}
