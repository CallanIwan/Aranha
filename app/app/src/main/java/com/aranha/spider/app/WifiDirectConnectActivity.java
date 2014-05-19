package com.aranha.spider.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.spider.app.R;

public class WifiDirectConnectActivity extends Activity {
    private static final String TAG = "WifiConnectActivity";

    WiFiDirectService mWifiDirectService;
    boolean mWifiDirectIsConnected = false;

    Button mConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect);

        mConnectButton = (Button)findViewById(R.id.connectWifButton);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWifiDirectService!=null) {
                    mWifiDirectService.connect();
                }
            }
        });
        mConnectButton.setEnabled(false);

    }
    @Override
    protected void onStart() {
        super.onStart();
        startWifiDirectService();
    }

    public void startWifiDirectService() {
        Intent intent = new Intent(this, WiFiDirectService.class);
        intent.putExtra("messageReceiver", mWifiDirectServiceMessenger);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, " Trying to start wifi service bro");
    }


    /**
     * Used to connect to the BluetoothService
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Get the bluetoothService class via BluetoothBinder.
            mWifiDirectService = ((WiFiDirectService.WifiDirectBinder) service).getService();
            mWifiDirectIsConnected = true;
            Log.d(TAG, "Wifi-Direct service is connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mWifiDirectIsConnected = false;
            Log.d(TAG, "Wifi-Direct service disconnected");
        }
    };

    /**
     * Receives all the messages from the Bluetooth service
     */
    final Messenger mWifiDirectServiceMessenger = new Messenger(new WifiDirectServiceMessageHandler());
    class WifiDirectServiceMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (SpiderController.SpiderMessages[msg.what]) {
                case RASPBERRYPI_FOUND:
                   // mConnectedTextView.setText("Raspberry device found");
                    mConnectButton.setEnabled(true);

                    break;

                case CONNECTING_FAILED:
                    //Toast.makeText(ConnectActivity.this, "Failed to connect to the spider!", Toast.LENGTH_LONG).show();
                    //mConnectedTextView.setText("Not connected");
                    //mConnectButton.setEnabled(false);
                    break;

                case CONNECTED_TO_RASPBERRYPI:
                    //mConnectedTextView.setText("Connected!");
                    //mConnectButton.setEnabled(false);
                    // Start the main controller screen
                    //startActivity(new Intent(ConnectActivity.this, MainActivity.class));
                    //Toast.makeText(ConnectActivity.this, "Connected to the spider!", Toast.LENGTH_LONG).show();
                    break;

                case CONNECTION_CLOSED:
                case CONNECTION_LOST:
                    //mConnectedTextView.setText("Not connected");
                    // Return to the first activity
                    //startActivity(new Intent(ConnectActivity.this, ConnectActivity.class));
                    //Toast.makeText(ConnectActivity.this, "Lost connection to the spider.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wifi_connect, menu);
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
