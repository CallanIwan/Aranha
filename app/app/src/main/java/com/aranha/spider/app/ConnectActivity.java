package com.aranha.spider.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spider.app.R;

/**
 * The activity which is used to connect to the Raspberry Pi.
 */
public class ConnectActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = "ConnectActivity";

    private final int REQUEST_ENABLE_BLUETOOTH = 1;

    private Button mConnectButton, mRefreshButton, mManualConnectButton;
    private TextView mConnectedTextView;

    private EditText raspberryName;

    SpiderControllerService mConnectService;
    boolean mServiceIsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // Get the UI Resources from the xml
        //
        mConnectButton = (Button)findViewById(R.id.connectButton);
        mConnectButton.setOnClickListener(this);
        mConnectButton.setEnabled(false);
        mManualConnectButton = (Button)findViewById(R.id.manualConnectButton);
        mManualConnectButton.setOnClickListener(this);
        mConnectedTextView = (TextView)findViewById(R.id.connectedTextView);

        mRefreshButton = (Button)findViewById(R.id.refreshButton);
        mRefreshButton.setOnClickListener(this);

        raspberryName = (EditText)findViewById(R.id.raspberryName);
        raspberryName.addTextChangedListener(textwatcher);
    }

    @Override
    protected void onStart() {
        super.onStart();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println("Checking for bluetooth ?");
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                System.out.println("Trying to enable bluetooth");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            } else {
                startBluetoothService();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mServiceIsConnected) { // Unbind from the service
            unbindService(mConnection);
            mServiceIsConnected = false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_ENABLE_BLUETOOTH:
                if(resultCode == Activity.RESULT_OK) { // User pressed 'Allow' when asked to activate bluetooth.
                    startBluetoothService();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //TODO: close application
                }
                break;
        }
    }

    public void startBluetoothService() {
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Used to connect to the Connection Service
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Get the SpiderControllerService class via a Binder.
            mConnectService =  ((SpiderControllerService.SpiderControllerServiceBinder)service).getService();
            mConnectService.setActivityMessenger(mConnectServiceMessenger);
            mConnectService.discoverDevices();
            mServiceIsConnected = true;
            Log.d(TAG, "Connect service is connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mServiceIsConnected = false;
            Log.d(TAG, "Connect service disconnected");
        }
    };

    /**
     * Receives all the messages from the Connection service
     */
    final Messenger mConnectServiceMessenger = new Messenger(new ConnectServiceMessageHandler());
    class ConnectServiceMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (SpiderController.SpiderMessages[msg.what]) {
                case RASPBERRYPI_FOUND:
                    mConnectedTextView.setText("Raspberry device found");
                    mConnectButton.setEnabled(true);
                    break;

                case CONNECTING_FAILED:
                    Toast.makeText(ConnectActivity.this, "Failed to connect to the spider!", Toast.LENGTH_LONG).show();
                    mConnectedTextView.setText("Not connected");
                    mConnectButton.setEnabled(false);
                    if(mServiceIsConnected)
                        mConnectService.discoverDevices();
                    break;

                case CONNECTED_TO_RASPBERRYPI:
                    mConnectedTextView.setText("Connected!");
                    mConnectButton.setEnabled(false);
                    // Start the main controller screen
                    startActivity(new Intent(ConnectActivity.this, MainActivity.class));
                    Toast.makeText(ConnectActivity.this, "Connected to the spider!", Toast.LENGTH_LONG).show();
                    break;

                case CONNECTION_CLOSED:
                case CONNECTION_LOST:
                    mConnectedTextView.setText("Not connected");
                    // Return to the first activity
                    if(mServiceIsConnected)
                        mConnectService.discoverDevices();
                    Toast.makeText(ConnectActivity.this, "Lost connection to the spider.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.connectButton) {
            if(mServiceIsConnected)
                mConnectService.connect();
            mConnectButton.setEnabled(false);
        }
        else if(view.getId() == R.id.refreshButton) {
            if(mServiceIsConnected) {
                mConnectService.discoverDevices();
            }
        }
        else if(view.getId() == R.id.manualConnectButton) {
            if(mServiceIsConnected) {
                // TODO:  mConnectService.manualConnect("00:15:83:6A:31:B7");
            }
        }
    }

    private TextWatcher textwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
            if(mServiceIsConnected) {
                 mConnectService.setRaspberryPiName(editable.toString());
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // TODO: startActivity(new Intent(ConnectActivity.this, WifiDirectConnectActivity.class));
            startActivity(new Intent(ConnectActivity.this, BluetoothDiscoverDevicesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
