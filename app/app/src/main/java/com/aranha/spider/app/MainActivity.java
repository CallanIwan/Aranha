package com.aranha.spider.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    static String sScriptList = null;

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
            setCameraEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        setCameraEnabled(false);
        if (sIsConnectedToService) { // Unbind from the service
            unbindService(mConnection);
            sIsConnectedToService = false;
        }
    }

    public void bindToService(Class<? extends SpiderControllerService> serviceClass) {
        if(!sIsConnectedToService) {
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
            Log.d(TAG, "SpiderController service is connected");
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
    final Messenger mSpiderMessenger = new Messenger(new BluetoothServiceMessageHandler());
    class BluetoothServiceMessageHandler extends Handler {
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
                    startActivity(new Intent(MainActivity.this, ConnectActivity.class));
                    Toast.makeText(MainActivity.this, "Lost Connection to the spider!", Toast.LENGTH_LONG).show();
                    break;

            }
        }
    }

    private static int CAMERA_UPDATE_DELAY = 1000;
    private static Handler requestCameraImagesHandler = new Handler();
    private static Runnable requestCameraImagesRunnable = new Runnable() {
        @Override
        public void run() {
            if(sSpiderControllerService != null) {
                sSpiderControllerService.send(SpiderInstruction.requestCameraImage);
                requestCameraImagesHandler.postDelayed(this, CAMERA_UPDATE_DELAY);
            }
        }
    };
    public static void setCameraEnabled(boolean value) {
        if(value) {
            requestCameraImagesHandler.postDelayed(requestCameraImagesRunnable, CAMERA_UPDATE_DELAY);
            Log.d(TAG, "Camera is ON");
        }
        else {
            requestCameraImagesHandler.removeCallbacks(requestCameraImagesRunnable);
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
                setCameraEnabled(true);
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
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
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

    /**
     * Creates the views for each of the tabs
     */
    public static class PlaceholderFragment extends Fragment  {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            // Unpack Arguments
            //
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            // Select Proper View
            //
            View rootView = null;
            switch (sectionNumber) {
                case PAGE_ID_CONTROL + 1:
                    rootView = inflater.inflate(R.layout.fragment_main_control, container, false);
                    createTabFragment_Control(rootView);
                    break;
                case PAGE_ID_INFO + 1:
                    rootView = inflater.inflate(R.layout.fragment_main_leg_control, container, false);
                    createTabFragment_legControl(rootView);
                    break;
                case PAGE_ID_SCRIPTS + 1:
                    rootView = inflater.inflate(R.layout.fragment_main_scripts, container, false);
                    createTabFragment_scripts(rootView);
                    break;
            }

            return rootView;
        }


        public void createTabFragment_Control(View rootView) {
            Button leftArrowButton = (Button)rootView.findViewById(R.id.leftArrowButton);
            Button rightArrowButton = (Button)rootView.findViewById(R.id.rightArrowButton);
            Button upArrowButton = (Button)rootView.findViewById(R.id.upArrowButton);
            Button downArrowButton = (Button)rootView.findViewById(R.id.downArrowButton);

            MainActivity.sImageView = (ImageView)rootView.findViewById(R.id.imageView);
            MainActivity.sImageView.setOnClickListener(controlOnClickListener);

            leftArrowButton.setOnClickListener(controlOnClickListener);
            rightArrowButton.setOnClickListener(controlOnClickListener);
            upArrowButton.setOnClickListener(controlOnClickListener);
            downArrowButton.setOnClickListener(controlOnClickListener);
        }

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
                        setCameraEnabled(false);
                    }
                }
            }
        };

        public void createTabFragment_legControl(View rootView) {
            //TODO: Create controls
        }

        public void createTabFragment_scripts(View rootView) {

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
            ImageButton goButton = (ImageButton)rootView.findViewById(R.id.imageButton);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                listViewScripts.setEnabled(false);
                //TODO: Run script
                }
            });
        }
    }

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
            sSpiderControllerService.disconnect();
            startActivity(new Intent(MainActivity.this, ConnectActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}

