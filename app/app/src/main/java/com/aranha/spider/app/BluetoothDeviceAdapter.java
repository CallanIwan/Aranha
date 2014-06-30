package com.aranha.spider.app;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.spider.app.R;

/**
 *
 */
public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater inflater = null;

    public BluetoothDeviceAdapter(Context context) {
        super(context, R.layout.list_view_row_item);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if(rowView == null) {
            rowView = inflater.inflate(R.layout.list_view_bluetooth_device_item, null);
        }

        ((TextView)rowView.findViewById(R.id.text)).setText(getItem(position).getName());

        return rowView;
    }
}
