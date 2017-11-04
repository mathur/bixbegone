package com.rmathur.bixbegone;

import android.content.Context;
import android.content.pm.ResolveInfo;

import java.util.Comparator;

/**
 * Created by rohanmathur on 11/4/17.
 */

public class AppComparator implements Comparator<ResolveInfo> {

    Context context;

    public AppComparator(Context appContext) {
        context = appContext;
    }

    @Override
    public int compare(ResolveInfo o1, ResolveInfo o2) {
        String appName1 = o1.loadLabel(context.getPackageManager()).toString().toLowerCase();
        String appName2 = o2.loadLabel(context.getPackageManager()).toString().toLowerCase();
        return appName1.compareTo(appName2);
    }
}
