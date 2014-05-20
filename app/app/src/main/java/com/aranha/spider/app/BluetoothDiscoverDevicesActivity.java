package com.aranha.spider.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.spider.app.R;

public class BluetoothDiscoverDevicesActivity extends Activity {
    private static final String TAG = "BluetoothService";

    private BluetoothDeviceAdapter mBluetoothAdapter;
    private ListView mBluetoothListView;
    BluetoothService mBluetoothService;
    boolean mBluetoothIsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_discover_devices);

        mBluetoothListView = (ListView) findViewById(R.id.bluetoothDevicesListView);
        startBluetoothService();
    }
    @Override
    protected void onStop() {
        super.onStop();

        if (mBluetoothIsConnected) { // Unbind from the service
            unbindService(mConnection);
            mBluetoothIsConnected = false;
        }
    }

    public void startBluetoothService() {
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }



    /**
     * Used to connect to the BluetoothService
     */

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Get the bluetoothService class via BluetoothBinder.
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.discoverBluetoothDevices();
            mBluetoothIsConnected = true;

            mBluetoothAdapter = mBluetoothService.getDiscoveredDevicesAdapter();
            mBluetoothListView.setAdapter(mBluetoothAdapter);
            mBluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //TODO: Item (Script) selected
                }
            });

            Log.d(TAG, "Bluetooth service is connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBluetoothIsConnected = false;
            Log.d(TAG, "Bluetooth service disconnected");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bluetooth_discover_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
