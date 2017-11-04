package com.rmathur.bixbegone;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class OnActionSelectedListener implements AdapterView.OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        final PreferenceHelper prefHelper = new PreferenceHelper(view.getContext());
        int action = pos;
        switch (action) {
            case 1: {
                // open app
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                final List<ResolveInfo> pkgAppsList = view.getContext().getPackageManager().queryIntentActivities( mainIntent, 0);
                Collections.sort(pkgAppsList, new AppComparator(view.getContext()));
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = (View) inflater.inflate(R.layout.app_list, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle(view.getContext().getString(R.string.app_list_title));
                ListView lv = (ListView) convertView.findViewById(R.id.app_list);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ResolveInfo appInfo = pkgAppsList.get(position);
                        Intent appIntent = view.getContext().getPackageManager().getLaunchIntentForPackage(appInfo.activityInfo.packageName);
                        prefHelper.saveAppSelection(appIntent);
                        String appName = (appInfo.loadLabel(view.getContext().getPackageManager())).toString();
                        Toast.makeText(view.getContext(), appName + " " + view.getContext().getString(R.string.app_selected), Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.setNeutralButton(view.getContext().getString(R.string.ok_action), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AppAdapter appAdapter = new AppAdapter(view.getContext(), pkgAppsList);
                lv.setAdapter(appAdapter);
                alertDialog.show();

                break;
            }
            case 3: {
                // open camera
                if(!checkCameraPermissions(view)) {
                    showPermissionsErrorSnackbar(view, view.getContext().getString(R.string.camera_permission_error));
                    action = 0;
                }
                break;
            }
            case 4: {
                // open notification shade
                if(!checkNotificationShadePermissions(view)) {
                    showPermissionsErrorSnackbar(view, view.getContext().getString(R.string.notification_permission_error));
                    action = 0;
                }
                break;
            }
            case 5: {
                // take screenshot
                break;
            }
            case 6: {
                // toggle flashlight
                break;
            }
            case 7: {
                // toggle silent/ring
                if(!checkRingerModePermissions(view)) {
                    showPermissionsErrorSnackbar(view, view.getContext().getString(R.string.change_ringer_mode_error));
                    action = 0;
                }
            }
            case 8: {
                // toggle silent/vibrate
                if(!checkRingerModePermissions(view)) {
                    showPermissionsErrorSnackbar(view, view.getContext().getString(R.string.change_ringer_mode_error));
                    action = 0;
                }
            }
            case 9: {
                // toggle vibrate/ring
                if(!checkRingerModePermissions(view)) {
                    showPermissionsErrorSnackbar(view, view.getContext().getString(R.string.change_ringer_mode_error));
                    action = 0;
                }
                break;
            }
            case 10: {
                // toggle silent/vibrate/ring
                if(!checkRingerModePermissions(view)) {
                    showPermissionsErrorSnackbar(view, view.getContext().getString(R.string.change_ringer_mode_error));
                    action = 0;
                }
                break;
            }
            case 11: {
                // home button
                break;
            }
            case 12: {
                // recents button
                break;
            }
            default: {
                break;
            }
        }

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

    public boolean checkNotificationShadePermissions(View view) {
        if (view.getContext().checkSelfPermission(Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public boolean checkRingerModePermissions(View view) {
        if (view.getContext().checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        NotificationManager n = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(!n.isNotificationPolicyAccessGranted()) {
            return false;
        }

        return true;
    }
}
