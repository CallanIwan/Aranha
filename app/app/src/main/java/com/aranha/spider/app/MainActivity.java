package com.aranha.spider.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.ListView;

import com.example.spider.app.R;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

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

    static SpiderController mSpiderControllerService;
    boolean mIsConnectedToService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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

        connectToSpiderControllerService();
    }

    public void connectToSpiderControllerService() {
        //TODO: Wifi or Bluetooth
        Intent intent = new Intent(this, BluetoothService.class);
        intent.putExtra("messageReceiver", mSpiderMessenger);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Used to connect to the BluetoothService
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Get the bluetoothService class via BluetoothBinder.
            mSpiderControllerService = ((BluetoothService.BluetoothBinder) service).getService();
            mIsConnectedToService = true;
            Log.d("MainActivity", "Bluetooth service is connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsConnectedToService = false;
            Log.d("MainActivity", "Bluetooth service disconnected");
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
                case RASPBERRYPI_FOUND:
                    break;

                case CONNECTING_FAILED:
                    break;

                case CONNECTED_TO_RASPBERRYPI:
                    break;
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
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
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
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
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main_control, container, false);
                    createTabFragment_Control(rootView);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_main_leg_control, container, false);
                    createTabFragment_legControl(rootView);
                    break;
                case 3:
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

            leftArrowButton.setOnClickListener(controlOnClickListener);

            //TODO: Add rest of the control

        }

        public View.OnClickListener controlOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.leftArrowButton) {
                    mSpiderControllerService.send_moveLeft();
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
            List<String> scriptList = new ArrayList<String>();
            scriptList.add("Kill Balloons");
            scriptList.add("Dance");
            scriptList.add("Dance 2");
            scriptList.add("Walk with pauses");
            //TODO: Add available scripts to scriptList.
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.list_view_row_item, scriptList);
            listViewScripts.setAdapter(adapter);
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
        } else if( id == R.id.action_disconnect) {
            mSpiderControllerService.disconnect();
            startActivity(new Intent(MainActivity.this, ConnectActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}

