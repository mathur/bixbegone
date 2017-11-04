package com.rmathur.bixbegone;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI
    Switch enableServiceSwitch;
    Switch startOnBootSwitch;
    Spinner actionSpinner;

    // Shared Preferences
    PreferenceHelper prefHelper;

    // Disable packages feature
//    EnterpriseDeviceManager enterpriseDeviceManager;
//    ApplicationPolicy appPolicy;
//    String[] bixbyPackageNames = {
//            "com.samsung.android.bixby.es.globalaction",
//            "com.samsung.android.app.spage",
//            "com.samsung.android.bixby.plmsync",
//            "com.samsung.android.visionintelligence",
//            "com.samsung.android.bixby.agent",
//            "com.samsung.android.bixby.agent.dummy",
//            "com.samsung.android.bixby.voiceinput",
//            "com.samsung.android.bixby.wakeup"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        KnoxEnterpriseLicenseManager klmManager = KnoxEnterpriseLicenseManager.getInstance(this);
//        klmManager.activateLicense("KLM06-NYUS4-Z81GI-PHZFP-LO2BJ-AFQW8");
//
//        enterpriseDeviceManager = new EnterpriseDeviceManager(this);
//        appPolicy = enterpriseDeviceManager.getApplicationPolicy();

        prefHelper = new PreferenceHelper(this);

        enableServiceSwitch = (Switch) findViewById(R.id.service_enabled_switch);
        startOnBootSwitch = (Switch) findViewById(R.id.start_on_boot_switch);
        actionSpinner = (Spinner) findViewById(R.id.action_spinner);

        // initialize switch state
        enableServiceSwitch.setChecked(false);
        startOnBootSwitch.setChecked(false);
        startOnBootSwitch.setEnabled(false);
        actionSpinner.setEnabled(false);
        if (prefHelper.getServiceEnabledStatus() && isServiceRunning()) {
            // the service is running and our prefs say it should be running, so enable the switch
            enableServiceSwitch.setChecked(true);
            startOnBootSwitch.setEnabled(true);
            actionSpinner.setEnabled(true);
            if (prefHelper.getStartOnBootStatus()) {
                startOnBootSwitch.setChecked(true);
            }
        } else if (prefHelper.getServiceEnabledStatus() && !isServiceRunning()) {
            // our prefs indicate that the service should be running, but it isn't, so update prefs
            prefHelper.setServiceEnabledStatus(false);
        } else if (!prefHelper.getServiceEnabledStatus() && isServiceRunning()) {
            // service is running, but our prefs indicate that it shouldnt be, so update prefs
            prefHelper.setServiceEnabledStatus(true);
            enableServiceSwitch.setChecked(true);
            startOnBootSwitch.setEnabled(true);
            actionSpinner.setEnabled(true);
            if (prefHelper.getStartOnBootStatus()) {
                startOnBootSwitch.setChecked(true);
            }
        } else {
            // service is disabled and not running
            enableServiceSwitch.setChecked(false);
        }

        enableServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    prefHelper.setServiceEnabledStatus(true);
                    startService(new Intent(MainActivity.this, BixBeGoneService.class));
                    startOnBootSwitch.setEnabled(true);
                    actionSpinner.setEnabled(true);
                } else {
                    // The toggle is disabled
                    prefHelper.setServiceEnabledStatus(false);
                    stopService(new Intent(MainActivity.this, BixBeGoneService.class));
                    startOnBootSwitch.setChecked(false);
                    startOnBootSwitch.setEnabled(false);
                    actionSpinner.setEnabled(false);
                    prefHelper.setStartOnBootStatus(false);
                }

                // setBixbyPackageStatus(isChecked);
            }
        });

        startOnBootSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefHelper.setStartOnBootStatus(isChecked);
            }
        });

        actionSpinner.setOnItemSelectedListener(new OnActionSelectedListener());
//        actionSpinner.setSelection(prefHelper.getButtonAction());

        checkPermissions();
    }


    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BixBeGoneService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_LOGS) != PackageManager.PERMISSION_GRANTED) {
            // make popup telling user to grant permission via ADB
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.read_logs_dialog_title))
                    .setMessage(getString(R.string.read_logs_dialog_message))
                    .setPositiveButton(R.string.read_logs_dialog_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkManifestPermissionsIfNeeded();
                        }
                    })
                    .show();
        } else {
            checkManifestPermissionsIfNeeded();
        }

        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!n.isNotificationPolicyAccessGranted()) {
            // Ask the user to grant access
            Toast.makeText(getApplicationContext(), getString(R.string.change_ringer_mode_toast), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            MainActivity.this.startActivityForResult(intent, -1);
        }
    }

    public void checkManifestPermissionsIfNeeded() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.EXPAND_STATUS_BAR,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!report.areAllPermissionsGranted()) {
                    handleDeniedPermissions(report);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    public void handleDeniedPermissions(MultiplePermissionsReport report) {
        if (checkSelfPermission(Manifest.permission.READ_LOGS) != PackageManager.PERMISSION_GRANTED) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), getString(R.string.read_logs_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            String errorMsg = getString(R.string.permissions_error_part_1);
            List<PermissionDeniedResponse> deniedPermissions = report.getDeniedPermissionResponses();
            for (PermissionDeniedResponse response : deniedPermissions) {
                switch (response.getPermissionName()) {
                    case "android.permission.CAMERA": {
                        errorMsg += getString(R.string.camera_permission_feature);
                        break;
                    }
                    case "android.permission.EXPAND_STATUS_BAR": {
                        errorMsg += getString(R.string.notification_permission_feature);
                    }
                    case "android.permission.MODIFY_AUDIO_SETTINGS": {
                        errorMsg += getString(R.string.change_ringer_mode_feature);
                    }
                    default: {
                        break;
                    }
                }
            }

            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), errorMsg, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

//    void setBixbyPackageStatus(boolean enabled) {
//        if(enabled) {
//            // turn off all the bixby packages if the service is enabled
//            for(String packageName : bixbyPackageNames) {
//                appPolicy.setDisableApplication(packageName);
//            }
//        } else {
//            // turn on all the bixby packages if the service is disabled
//            for(String packageName : bixbyPackageNames) {
//                appPolicy.setEnableApplication(packageName);
//            }
//        }
//    }

}
