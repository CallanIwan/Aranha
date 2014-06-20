package com.aranha.spider.app;

import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.aranha.spider.app.mjpeg.MjpegInputStream;
import com.aranha.spider.app.mjpeg.MjpegView;
import com.example.spider.app.R;
import com.jjoe64.graphview.*;

import static com.jjoe64.graphview.GraphView.GraphViewData;


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

    ArrayAdapter sScriptsAdapter;
    SpiderControllerService mSpiderControllerService;
    Class<? extends SpiderControllerService> spiderControllerServiceType;
    boolean mIsConnectedToService = false;
    ImageView sImageView = null;
    public boolean isCameraEnabled = false;
    public boolean isWifiCameraSetup = false;
    String sScriptList = null;
    Class<? extends SpiderControllerService> mSelectedConnectServiceClass;
    private boolean isRunningScript = false;

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
        if (mIsConnectedToService) { // Unbind from the service
            unbindService(mConnection);
            mIsConnectedToService = false;
        }
    }

    public void bindToService(Class<? extends SpiderControllerService> serviceClass) {
        if(!mIsConnectedToService) {
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
            mSpiderControllerService = ((SpiderControllerService.SpiderControllerServiceBinder)service).getService();
            mSpiderControllerService.setActivityMessenger(mSpiderMessenger);
            Log.d(TAG, "Sending Script request");
            mSpiderControllerService.send(SpiderInstruction.requestScriptList);
            mIsConnectedToService = true;
            Log.d(TAG, "SpiderController service is connected" + mSpiderControllerService.getClass().getName());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsConnectedToService = false;
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
        if(mSpiderControllerService != null)
            mSpiderControllerService.setCameraEnabled(this, value);
    }

    MjpegView imageSurfaceView;

    public void setWifiCameraStreamEnabled(boolean value) {
        if(!value) {
            if(imageSurfaceView != null) imageSurfaceView.stopPlayback();
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

                isWifiCameraSetup = true;

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



    private static int REQUEST_INFO_DELAY = 1000;
    private static Handler requestSpiderInfoHandler = new Handler();
    private Runnable requestSpiderInfoRunnable = new Runnable() {
        @Override
        public void run() {
            mSpiderControllerService.send(SpiderInstruction.requestSpiderInfo);
            requestSpiderInfoHandler.postDelayed(this, REQUEST_INFO_DELAY);
        }
    };
    public void setRequestSpiderInfoEnabled(boolean value) {
        if(value)
            requestSpiderInfoHandler.postDelayed(requestSpiderInfoRunnable, REQUEST_INFO_DELAY);
        else
            requestSpiderInfoHandler.removeCallbacks(requestSpiderInfoRunnable);
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
            Button leftRotateArrowButton = (Button)rootView.findViewById(R.id.leftRotateArrowButton);
            Button rightRotateArrowButton = (Button)rootView.findViewById(R.id.rightRotateArrowButton);

            sImageView = (ImageView)rootView.findViewById(R.id.imageView);
            sImageView.setOnTouchListener(controlOnClickListener);
            //sImageView.setVisibility(View.INVISIBLE);
            imageSurfaceView = (MjpegView) rootView.findViewById(R.id.imageSurfaceView);
            imageSurfaceView.setVisibility(View.INVISIBLE);

            leftArrowButton.setOnTouchListener(controlOnClickListener);
            rightArrowButton.setOnTouchListener(controlOnClickListener);
            upArrowButton.setOnTouchListener(controlOnClickListener);
            downArrowButton.setOnTouchListener(controlOnClickListener);
            leftRotateArrowButton.setOnTouchListener(controlOnClickListener);
            rightRotateArrowButton.setOnTouchListener(controlOnClickListener);

            return rootView;
        }
    };

    private Fragment spiderInformationFragment = new Fragment() {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_information, container, false);


            // init example series data
            GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
                    new GraphViewData(1, 100)
                    , new GraphViewData(2, 99)
                    , new GraphViewData(3, 98)
                    , new GraphViewData(4, 75)
            });

            GraphView graphView = new LineGraphView(
                    MainActivity.this // context
                    , "Accu" // heading
            );
            graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
            graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
            graphView.addSeries(exampleSeries); // data

            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graphLinearLayout1);
            layout.addView(graphView);


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
                    //listViewScripts

                    //System.out.println("HAHA");
                }
            });

            // Set up GO/STOP button
            //
            final ImageButton goButton = (ImageButton) rootView.findViewById(R.id.imageButton);
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!isRunningScript) {
                        String scriptChoice = (String)sScriptsAdapter.getItem((int)listViewScripts.getSelectedItemId());

                        isRunningScript = true;
                        listViewScripts.setEnabled(false);
                        Log.d(TAG, "Script running " + scriptChoice);
                        goButton.setBackgroundResource(R.drawable.stop);
                        mSpiderControllerService.send(SpiderInstruction.startScript, scriptChoice);
                    } else {
                        isRunningScript = false;
                        listViewScripts.setEnabled(true);
                        Log.d(TAG, "Stop script!");
                        goButton.setBackgroundResource(R.drawable.go);
                        mSpiderControllerService.send(SpiderInstruction.stopScript);
                    }

                }
            });

            return rootView;
        }
    };

    private static int SEND_RECURRING_INSTRUCTION_TIME = 400;
    private SpiderInstruction currentInstruction;
    private String currentInstructionExtraData;
    private static Handler sendRecurringInstructionHandler = new Handler();
    private Runnable sendRecurringInstructionRunnable = new Runnable() {
        @Override
        public void run() {
            mSpiderControllerService.send(currentInstruction, currentInstructionExtraData);
            System.out.println("Sending: " + currentInstruction);
            sendRecurringInstructionHandler.postDelayed(this, SEND_RECURRING_INSTRUCTION_TIME);
        }
    };

    public void sendRecurringInstruction(SpiderInstruction instruction, String extraData) {
        this.currentInstruction = instruction;
        this.currentInstructionExtraData = extraData;
        sendRecurringInstructionHandler.postDelayed(sendRecurringInstructionRunnable, SEND_RECURRING_INSTRUCTION_TIME);
    }
    public void stopRecurringInstruction() {
        sendRecurringInstructionHandler.removeCallbacks(sendRecurringInstructionRunnable);
    }

    public View.OnTouchListener controlOnClickListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                if(mIsConnectedToService) {
                    if (view.getId() == R.id.leftArrowButton) {
                        //mSpiderControllerService.send(SpiderInstruction.move, "270");
                        sendRecurringInstruction(SpiderInstruction.move, "270");
                    } else if (view.getId() == R.id.rightArrowButton) {
                        //mSpiderControllerService.send(SpiderInstruction.move, "90");
                        sendRecurringInstruction(SpiderInstruction.move,  "90");
                    } else if (view.getId() == R.id.downArrowButton) {
                        //mSpiderControllerService.send(SpiderInstruction.move, "180");
                        sendRecurringInstruction(SpiderInstruction.move, "180");
                    } else if (view.getId() == R.id.upArrowButton) {
                       // mSpiderControllerService.send(SpiderInstruction.move, "0");
                        sendRecurringInstruction(SpiderInstruction.move, "0");
                    } else if (view.getId() == R.id.imageView) {
                        toggleCameraEnabled();
                    } else if (view.getId() == R.id.leftRotateArrowButton) {
                       // mSpiderControllerService.send(SpiderInstruction.move, "strafe;l");
                        sendRecurringInstruction(SpiderInstruction.move, "strafe;l");
                    } else if (view.getId() == R.id.rightRotateArrowButton) {
                        //mSpiderControllerService.send(SpiderInstruction.move, "strafe;r");
                        sendRecurringInstruction(SpiderInstruction.move, "strafe;r");
                    } else if (view.getId() == R.id.resetButton) {
                        mSpiderControllerService.send(SpiderInstruction.relax);
                    } else if (view.getId() == R.id.upButton) {
                        mSpiderControllerService.send(SpiderInstruction.spiderUpDown, "u");
                    } else if (view.getId() == R.id.downButton) {
                        mSpiderControllerService.send(SpiderInstruction.spiderUpDown, "d");
                    }
                }

            } else if (action == MotionEvent.ACTION_UP) {

                stopRecurringInstruction();

                System.out.println("Release");
            }


            return false;
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
            mSpiderControllerService.disconnect();
        }
        else if( id == 16908332 /* Back button */ ) {
            mSpiderControllerService.disconnect();
            //startActivity(new Intent(MainActivity.this, ConnectionSelect.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

