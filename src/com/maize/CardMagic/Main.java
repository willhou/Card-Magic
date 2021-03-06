/*
 * Copyright 2011 Maize Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.maize.CardMagic;

import com.maize.CardMagic.AppBrain.BrainStateChangeListener;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

/**
 * Main.java - The main activity of the app
 *
 */
public class Main extends Activity {
    /* UI components */
    private ImageView mCardView;

    /* App model */
    AppBrain mBrain;

    /* Options menu constant */
    private final static int DEMO = Menu.FIRST;
    private final static int ABOUT = Menu.FIRST + 1;
    
    private SensorManager mSensorManager;
    
    final SensorEventListener mListener = new SensorEventListener() {
        /**
         * Triggered when sensor values changed
         */
        public void onSensorChanged(SensorEvent event) {
            mBrain.changeState(event);
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.i("AccuracyChanged", "Accuracy is " + accuracy);
        }

    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCardView = (ImageView) findViewById(R.id.card);

        mBrain = new AppBrain();
        mBrain.addBrainStateChangeListener(new BrainStateChangeListener() {
            public void swapCard(int card) {
                mCardView.setImageResource(card);
            }
        });
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // Get instance of SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Checks if the device has a light sensor before proceeding
        if (mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY).size() == 0) {
            // OMG, the device has no proximity sensor!!!
            UIUtils.showSensorMissingDialog(this);
        } else {
            // Get the proximity sensor
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            // Listens to the light sensor's event change
            mSensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        /*
         * Unregister listener to avoid memory leak
         */
        mSensorManager.unregisterListener(mListener);
    }

    /* Options menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item;
        item = menu.add(0, DEMO, 0, "Demo");
        item.setIcon(android.R.drawable.ic_menu_help);
        item = menu.add(0, ABOUT, 0, "About");
        item.setIcon(android.R.drawable.ic_menu_info_details);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
        case DEMO:
            /*
             *  Load up the demo Youtube video
             */
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=DT5K96iwoas"));
            startActivity(intent);
            return true;

        case ABOUT:
            intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
