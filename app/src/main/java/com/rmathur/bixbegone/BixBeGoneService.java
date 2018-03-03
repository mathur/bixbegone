package com.rmathur.bixbegone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BixBeGoneService extends Service {

    PreferenceHelper prefHelper;
    AudioManager audioManager;

    @Override
    public void onCreate() {
        prefHelper = new PreferenceHelper(this.getApplicationContext());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.SSS");
                Date oldDate = null;
                while (true) {
                    try {
                        Runtime.getRuntime().exec("logcat -c");
                        Process process = Runtime.getRuntime().exec("logcat ActivityManager:W");
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("com.samsung.android.bixby.WinkService")) {
                                String timestamp = line.split("\\s+")[1];
                                Date parsedDate;
                                try {
                                    parsedDate = dateFormat.parse(timestamp);
                                } catch (ParseException e) {
                                    parsedDate = null;
                                    e.printStackTrace();
                                }

                                if (oldDate != null) {
                                    long numSecondDiff = getDateDiff(oldDate, parsedDate, TimeUnit.MILLISECONDS);
                                    if (numSecondDiff > 500) {
                                        doButtonAction();
                                    }

                                }

                                oldDate = parsedDate;
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
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doButtonAction() {
        int action = prefHelper.getButtonAction();
        switch (action) {
            case 0: {
                // do nothing
                break;
            }
            case 1: {
                // open app
                Intent intent = prefHelper.getAppSelection();
                if (intent != null) {
                    startActivity(intent);
                }
                break;
            }
            case 2: {
                // open google assistant
                startActivity(new Intent(Intent.ACTION_VOICE_COMMAND).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            }
            case 3: {
                // open camera
                startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
                break;
            }
            case 4: {
                // open notification shade
                openNotificationShade();
                break;
            }
            case 5: {
                // toggle silent/ring
                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    setVolumeState(2);
                } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    setVolumeState(0);
                }  else {
                    setVolumeState(0);
                }
                break;
            }
            case 6: {
                // toggle silent/vibrate
                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    setVolumeState(1);
                } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    setVolumeState(0);
                }  else {
                    setVolumeState(0);
                }
                break;
            }
            case 7: {
                // toggle vibrate/ring
                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    setVolumeState(2);
                } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    setVolumeState(1);
                }  else {
                    setVolumeState(1);
                }
                break;
            }
            case 8: {
                // toggle silent/vibrate/ring
                if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    setVolumeState(1);
                } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    setVolumeState(2);
                }  else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    setVolumeState(0);
                }
                break;
            }
            case 9: {
                // home button
                startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                break;
            }
            default: {
                break;
            }
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

    public void setVolumeState(int type) {
        switch (type) {
            case 0: {
                // silent
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
                break;
            }
            case 1: {
                // vibrate
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            }
            case 2: {
                // ring
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
                break;
            }
            default: {
                break;
            }
        }
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}