package com.aranha.spider.app;

import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.aranha.spider.app.mjpeg.MjpegInputStream;
import com.aranha.spider.app.mjpeg.MjpegView;
import com.example.spider.app.R;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private static final String TAG = "MainActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    public static final int PAGE_ID_CONTROL = 0;
    public static final int PAGE_ID_INFO = 1;
    public static final int PAGE_ID_SCRIPTS = 2;

    static ArrayAdapter sScriptsAdapter;
    static SpiderControllerService sSpiderControllerService;
    static Class<? extends SpiderControllerService> spiderControllerServiceType;
    static boolean sIsConnectedToService = false;
    static ImageView sImageView = null;
    public boolean isCameraEnabled = false;
    public boolean isWifiCameraSetup = false;
    static String sScriptList = null;
    Class<? extends SpiderControllerService> mSelectedConnectServiceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        sScriptsAdapter = new ArrayAdapter(this, R.layout.list_view_row_item);

        try {
            String test = getIntent().getStringExtra("ServiceClass");
            Class serviceClass = Class.forName(test);
            if(serviceClass != null) {
                bindToService(serviceClass);
            }  else {
                Log.e(TAG, "Cannot connect to the SpiderController service! (class not found)");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        setCameraEnabled(false);
    }

    @Override
    protected void onResume () {
        super.onResume();

        if(mViewPager.getCurrentItem() == 0) {
            //setCameraEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //setCameraEnabled(false);
        if (sIsConnectedToService) { // Unbind from the service
            unbindService(mConnection);
            sIsConnectedToService = false;
        }
    }

    public void bindToService(Class<? extends SpiderControllerService> serviceClass) {
        if(!sIsConnectedToService) {
            mSelectedConnectServiceClass = serviceClass;
            Intent intent = new Intent(this, serviceClass);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Used to connect to the BluetoothService
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Get the bluetoothService class via BluetoothBinder.
            sSpiderControllerService = ((SpiderControllerService.SpiderControllerServiceBinder)service).getService();
            sSpiderControllerService.setActivityMessenger(mSpiderMessenger);
            sSpiderControllerService.send(SpiderInstruction.requestScriptList);
            sIsConnectedToService = true;
            Log.d(TAG, "SpiderController service is connected" + sSpiderControllerService.getClass().getName());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            sIsConnectedToService = false;
            Log.d(TAG, "SpiderController disconnected");
        }
    };

    /**
     * Receives all the messages from the Bluetooth service
     */
    final Messenger mSpiderMessenger = new Messenger(new SpiderServiceMessageHandler());
    class SpiderServiceMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (SpiderController.SpiderMessages[msg.what]) {

                case READ_IMAGE:
                    //Log.d(TAG, "Trying to Set bitmap");
                    if(sImageView != null) {
                        byte[] decodedByteString = (byte[]) msg.obj;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByteString, 0, decodedByteString.length);
                        if(bitmap != null)
                            sImageView.setImageBitmap(bitmap);
                    }
                    break;

                case READ_SCRIPT_LIST:
                    String[] scripts = (String[])msg.obj;
                    Log.d(TAG, "Received script list");
                    sScriptsAdapter.addAll(scripts);
                    break;

                case CONNECTION_CLOSED:
                case CONNECTION_LOST:
                    Intent connectActivity;

                    if(mSelectedConnectServiceClass != null) {
                        //connectActivity = new Intent(MainActivity.this, ConnectActivity.class);
                        //connectActivity.putExtra("ServiceClass", mSelectedConnectServiceClass.getName());
                        connectActivity = new Intent(MainActivity.this, ConnectionSelect.class);
                    }else {
                        connectActivity = new Intent(MainActivity.this, ConnectionSelect.class);
                    }
                    startActivity(connectActivity);
                    Toast.makeText(MainActivity.this, "Lost Connection to the spider!", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    public void toggleCameraEnabled() {
        setCameraEnabled(!isCameraEnabled);
    }
    public void setCameraEnabled(boolean value) {
        Log.d(TAG,"setCameraEnabled( " + value + " )");
        if(sSpiderControllerService != null)
            sSpiderControllerService.setCameraEnabled(this, value);
    }

    static MjpegView imageSurfaceView;

    public void setWifiCameraStreamEnabled(boolean value) {
        if(!value) {
            if(imageSurfaceView != null)
                imageSurfaceView.stopPlayback();
            return;
        }

        if(isWifiCameraSetup) {
            isCameraEnabled = true;
            imageSurfaceView.startPlayback();
        }
        else {
            if (!isCameraEnabled) {

                isCameraEnabled = true;
                imageSurfaceView.setVisibility(View.VISIBLE);
                imageSurfaceView.setDisplayMode(MjpegView.SIZE_BEST_FIT);
                String URL = "http://10.0.0.2:8080/?action=stream&d=.mjpeg";

                // Get an inputstream for the camera. THis has to be executed on a seperate thread because
                // it's not allowed to do internet stuff on the main UI thread.
                (new AsyncTask<String, MjpegInputStream, MjpegInputStream>() {
                    @Override
                    protected MjpegInputStream doInBackground(String... strings) {
                        return MjpegInputStream.read(strings[0]);
                    }

                    protected void onPostExecute(MjpegInputStream result) {
                        imageSurfaceView.setSource(result);
                    }
                }).execute(URL);
            }
        }
    }

    // --------------------------------------------
    //    Tabs
    // --------------------------------------------

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

        switch(tab.getPosition()) {
            case 0:
                //setCameraEnabled(true);
                break;
            case 1:
                setCameraEnabled(false);
                break;
            case 2:
                setCameraEnabled(false);
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a Fragment (defined as a static inner class below).
            switch (position + 1) {
                case PAGE_ID_CONTROL + 1:
                    return mainControlFragment;
                case PAGE_ID_INFO + 1:
                    return spiderInformationFragment;
                case PAGE_ID_SCRIPTS + 1:
                    return scriptsFragment;
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return 3; // Show 3 total pages.
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case PAGE_ID_CONTROL:
                    return getString(R.string.title_section1).toUpperCase(l);
                case PAGE_ID_INFO:
                    return getString(R.string.title_section2).toUpperCase(l);
                case PAGE_ID_SCRIPTS:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    private Fragment mainControlFragment = new Fragment() {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_control, container, false);

            Button leftArrowButton = (Button)rootView.findViewById(R.id.leftArrowButton);
            Button rightArrowButton = (Button)rootView.findViewById(R.id.rightArrowButton);
            Button upArrowButton = (Button)rootView.findViewById(R.id.upArrowButton);
            Button downArrowButton = (Button)rootView.findViewById(R.id.downArrowButton);

            MainActivity.sImageView = (ImageView)rootView.findViewById(R.id.imageView);
            MainActivity.sImageView.setOnClickListener(controlOnClickListener);
            MainActivity.sImageView.setVisibility(View.INVISIBLE);
            imageSurfaceView = (MjpegView) rootView.findViewById(R.id.imageSurfaceView);
            imageSurfaceView.setVisibility(View.INVISIBLE);

            leftArrowButton.setOnClickListener(controlOnClickListener);
            rightArrowButton.setOnClickListener(controlOnClickListener);
            upArrowButton.setOnClickListener(controlOnClickListener);
            downArrowButton.setOnClickListener(controlOnClickListener);

            return rootView;
        }
    };

    private Fragment spiderInformationFragment = new Fragment() {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_information, container, false);
            return rootView;
        }
    };

    private Fragment scriptsFragment = new Fragment() {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_scripts, container, false);

            // Set up list adapter
            //
            final ListView listViewScripts = (ListView) rootView.findViewById(R.id.listView);
            listViewScripts.setAdapter(sScriptsAdapter);
            listViewScripts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //TODO: Item (Script) selected
                }
            });

            // Set up GO/STOP button
            //
            ImageButton goButton = (ImageButton) rootView.findViewById(R.id.imageButton);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listViewScripts.setEnabled(false);
                    //TODO: Run script
                }
            });

            return rootView;
        }
    };

    public View.OnClickListener controlOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(sIsConnectedToService) {
                if (view.getId() == R.id.leftArrowButton) {
                    sSpiderControllerService.send(SpiderInstruction.move, "270");
                } else if (view.getId() == R.id.rightArrowButton) {
                    sSpiderControllerService.send(SpiderInstruction.move, "90");
                } else if (view.getId() == R.id.downArrowButton) {
                    sSpiderControllerService.send(SpiderInstruction.move, "180");
                } else if (view.getId() == R.id.upArrowButton) {
                    sSpiderControllerService.send(SpiderInstruction.move, "0");
                } else if (view.getId() == R.id.imageView) {
                    toggleCameraEnabled();
                }
            }
        }
    };

    // --------------------------------------------
    //    Options menu
    // --------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        else if( id == R.id.action_disconnect) {
            //startActivity(new Intent(MainActivity.this, ConnectionSelect.class));
            sSpiderControllerService.disconnect();
        }
        else if( id == 16908332 /* Back button */ ) {
            sSpiderControllerService.disconnect();
            startActivity(new Intent(MainActivity.this, ConnectionSelect.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

