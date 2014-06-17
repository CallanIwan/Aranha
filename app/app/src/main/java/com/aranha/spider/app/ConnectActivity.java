package com.aranha.spider.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBar;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spider.app.R;

/**
 * The activity which is used to connect to the Raspberry Pi.
 */
public class ConnectActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = "ConnectActivity";

    private final int REQUEST_ENABLE_BLUETOOTH = 1;

    private EditText raspberryName;

    Class<? extends SpiderControllerService> mSelectedConnectServiceClass = BluetoothService.class;
    SpiderControllerService mConnectService;
    boolean mServiceIsConnected = false;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_alt);

        raspberryName = (EditText)findViewById(R.id.raspberryName);
        raspberryName.addTextChangedListener(textwatcher);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(50);

        try {
            String serviceClassString = getIntent().getStringExtra("ServiceClass");
            Class serviceClass = Class.forName(serviceClassString);
            if(serviceClass != null) {
                mSelectedConnectServiceClass = serviceClass;
                Log.d(TAG, "serviceClass selected: " + serviceClass);
                startConnectionService();
            } else {
                Log.e(TAG, "Cannot connect to the SpiderController service! (class not found)");
            }
        } catch (NullPointerException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mSelectedConnectServiceClass != null && mSelectedConnectServiceClass == BluetoothService.class) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Log.d(TAG, "onStart()");

            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    System.out.println("Trying to enable bluetooth");
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                } else {
                    startConnectionService();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService();
    }

    public void unbindService() {
        if (mServiceIsConnected) {
            unbindService(mConnection);
            mServiceIsConnected = false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_ENABLE_BLUETOOTH:
                if(resultCode == Activity.RESULT_OK) { // User pressed 'Allow' when asked to activate bluetooth.
                    startConnectionService();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //TODO: close application
                }
                break;
        }
    }

     /**
     * Start the connection service
     */
    public void startConnectionService() {
        if(!mServiceIsConnected) {
            Intent intent = new Intent(this, mSelectedConnectServiceClass);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
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

            mServiceIsConnected = true;
            mConnectService.discoverDevices();
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
                    progressBar.setProgress(80);
                    break;

                case CONNECTING_FAILED:
                    Toast.makeText(ConnectActivity.this, "Failed to connect to the spider!", Toast.LENGTH_LONG).show();
                    progressBar.setProgress(0);
                    if(mServiceIsConnected)
                        mConnectService.discoverDevices();
                    break;

                case CONNECTED_TO_RASPBERRYPI:
                    // Start the main controller screen
                    progressBar.setProgress(100);
                    Intent mainActivityIntent = new Intent(new Intent(ConnectActivity.this, MainActivity.class));
                    mainActivityIntent.putExtra("ServiceClass", mSelectedConnectServiceClass.getName());
                    startActivity(mainActivityIntent);
                    Toast.makeText(ConnectActivity.this, "Connected to the spider!", Toast.LENGTH_LONG).show();
                    break;

                case CONNECTION_CLOSED:
                case CONNECTION_LOST:
                    // Return to the first activity
                    progressBar.setProgress(0);
                    if(mServiceIsConnected)
                        mConnectService.discoverDevices();
                    Toast.makeText(ConnectActivity.this, "Lost connection to the spider.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.refreshButton) {
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
        else if(id == 16908332 /* R.id.home doesn't work*/ ) {
            unbindService();
            startActivity(new Intent(ConnectActivity.this, ConnectionSelect.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
