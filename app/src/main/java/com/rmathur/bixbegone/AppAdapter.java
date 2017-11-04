package com.rmathur.bixbegone;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends ArrayAdapter<ResolveInfo> {
    public AppAdapter(Context context, List<ResolveInfo> apps) {
        super(context, 0, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResolveInfo app = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_app, parent, false);
        }

        TextView appName = (TextView) convertView.findViewById(R.id.app_name);
        appName.setText(app.loadLabel(getContext().getPackageManager()));

        return convertView;
    }
}