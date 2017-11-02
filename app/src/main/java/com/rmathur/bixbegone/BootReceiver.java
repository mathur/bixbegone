package com.rmathur.bixbegone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        if (preferenceHelper.getServiceEnabledStatus() == true && preferenceHelper.getStartOnBootStatus() == true) {
            Intent intent = new Intent(context, BixBeGoneService.class);
            context.startService(intent);
        }
    }
}