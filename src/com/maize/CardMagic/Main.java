/*
 * Copyright 2010 Maize Labs
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
    private ImageView cardView;

    /* App model */
    AppBrain brain;

    /* Options menu constant */
    private final static int DEMO = Menu.FIRST;
    private final static int ABOUT = Menu.FIRST + 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        cardView = (ImageView) findViewById(R.id.card);

        brain = new AppBrain();
        brain.addBrainStateChangeListener(new BrainStateChangeListener() {
            public void swapCard(int card) {
                cardView.setImageResource(card);
            }
        });

        // Get instance of SensorManager
        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Checks if the device has a light sensor before proceeding
        if (manager.getSensorList(Sensor.TYPE_PROXIMITY).size() == 0) {
            // OMG, the device has no proximity sensor!!!
            UIUtils.showSensorMissingDialog(this);
        } else {
            // Get the proximity sensor
            Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            // Create a listener that listens to the light sensor's event change
            manager.registerListener(new SensorEventListener() {
                // Called when sensor values have changed.
                public void onSensorChanged(SensorEvent event) {
                    brain.changeState(event);
                }

                // Called when the accuracy of a sensor has changed. (We don't
                // need this)
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    Log.i("AccuracyChanged", "Accuracy is " + accuracy);
                }

            }, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
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
            // Load up the demo Youtube video
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
