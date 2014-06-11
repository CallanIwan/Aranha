package com.aranha.spider.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.spider.app.R;

public class ConnectionSelect extends Activity {

    Button wifiButton;
    Button bluetoothButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_select);

        wifiButton = (Button)findViewById(R.id.wifiButton);
        bluetoothButton = (Button)findViewById(R.id.bluetoothButton);

        wifiButton.setOnClickListener(onClickListener);
        bluetoothButton.setOnClickListener(onClickListener);
    }


    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(view.getId() == R.id.wifiButton || view.getId() == R.id.bluetoothButton) {
                Intent connectActivityIntent = new Intent(new Intent(ConnectionSelect.this, ConnectActivity.class));

                if (view.getId() == R.id.wifiButton) {
                    connectActivityIntent.putExtra("ServiceClass", WifiService.class.getName());
                } else if (view.getId() == R.id.bluetoothButton) {
                    connectActivityIntent.putExtra("ServiceClass", BluetoothService.class.getName());
                }

                startActivity(connectActivityIntent);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connection_select, menu);
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
