package com.rmathur.bixbegone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;

public class OnActionSelectedListener implements AdapterView.OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        int action = pos;
        switch (action) {
            case 1:
                // open app
                break;
            case 3:
                // open camera
                if(!checkCameraPermissions(view)) {
                    showPermissionsErrorSnackbar(view, view.getContext().getString(R.string.camera_permission_error));
                    action = 0;
                }
                break;
            case 4:
                // open notification shade
                break;
            case 5:
                // take screenshot
                break;
            case 6:
                // toggle flashlight
                break;
            case 7:
                // toggle silent/ring
                break;
            case 8:
                // toggle silent/vibrate
                break;
            case 9:
                // toggle vibrate/ring
                break;
            case 10:
                // open power menu
                break;
            case 11:
                // home button
                break;
            case 12:
                // recents button
                break;
            default:
                break;
        }

        PreferenceHelper prefHelper = new PreferenceHelper(view.getContext());
        prefHelper.setButtonAction(action);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // nothing
    }

    public void showPermissionsErrorSnackbar(View view, String errorMsg) {
        Snackbar snackbar = Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public boolean checkCameraPermissions(View view) {
        if (view.getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

}
