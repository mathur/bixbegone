package com.rmathur.bixbegone;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class BixBeGoneService extends Service {

    PreferenceHelper prefHelper;

    @Override
    public void onCreate() {
        prefHelper = new PreferenceHelper(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int counter = 0;
                while (true) {
                    try {
                        Process process = Runtime.getRuntime().exec("logcat ActivityManager:W");
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("com.samsung.android.bixby.WinkService")) {
                                counter++;
                                if (counter % 2 == 0 && counter != 0) {
                                    doButtonAction();
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        // nothing
    }

    public void doButtonAction() {
        int action = prefHelper.getButtonAction();
        switch (action) {
            case 0:
                // do nothing
                break;
            case 1:
                // open app
                break;
            case 2:
                // open google assistant
                startActivity(new Intent(Intent.ACTION_VOICE_COMMAND).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case 3:
                // open camera
                startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
                break;
            case 4:
                // open notification shade
                openNotificationShade();
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
    }

    public void openNotificationShade() {
        try {
            Object sbservice = getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            if (Build.VERSION.SDK_INT >= 17) {
                showsb = statusbarManager.getMethod("expandNotificationsPanel");
            } else {
                showsb = statusbarManager.getMethod("expand");
            }

            showsb.invoke(sbservice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}