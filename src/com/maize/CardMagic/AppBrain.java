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

import java.util.HashSet;

import android.hardware.SensorEvent;
import android.util.Log;

/**
 * AppBrain.java - The Model of the card swapper. Monitors states of the card.
 *
 */
public class AppBrain {
    /* Value and states */
    private static final int THRESHOLD = 5; // default 5cm
    private boolean mProcessing = false;

    /*
     * Use enum instead of boolean for monitoring states. The reason is boolean
     * only supports a max of 2 states. But with enum, more states can be
     * supported and added easily in the future. (i.e. more cards can be used)
     */
    private enum State {
        HEARTS, CLUBS
    }; // There are only 2 cards in this app, King of Hearts and King of Clubs

    private State state = State.HEARTS; // Default card is King of Hearts

    /* Listeners */
    protected HashSet<BrainStateChangeListener> listeners;

    /* Constructor */
    public AppBrain() {
        listeners = new HashSet<BrainStateChangeListener>();
    }

    /**
     * Takes in a SensorEvent (Sensor.TYPE_PROXIMITY), analyzes it and
     * broadcasts the appropriate card drawable id to listeners.
     *
     * @param event
     */
    public void changeState(SensorEvent event) {
        if (!mProcessing) {
            // Avoid rapid changes due to spam of SensorEvents
            mProcessing = true;

            float[] valuesArray = event.values;

            // According to the Android docs, value of distance measured is
            // stored in element 0 of the float array
            // Sensor.TYPE_PROXIMITY: values[0]: Proximity sensor distance
            // measured in centimeters
            float value = valuesArray[0];

            Log.i("SensorChanged", "Distance is  " + value + "cm");

            // Swap card when distance detected by the proximity sensor is
            // closer than the threshold
            if (value < THRESHOLD) {
                if (state == State.HEARTS) {
                    fireStateChange(R.drawable.king_clubs);
                    state = State.CLUBS;
                } else {
                    fireStateChange(R.drawable.king_hearts);
                    state = State.HEARTS;
                }
            }

            mProcessing = false;
        }
    }

    /**
     *  Broadcasts the resource id of the desired card to all the listeners
     * @param resId Resource id of the card to be displayed
     */
    protected void fireStateChange(int resId) {
        for (BrainStateChangeListener l : listeners) {
            l.swapCard(resId);
        }
    }

    public void addBrainStateChangeListener(BrainStateChangeListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    public void removeBrainStateChangeListener(BrainStateChangeListener l) {
        listeners.remove(l);
    }

    /* Listener */
    public interface BrainStateChangeListener {
        public void swapCard(int resId);
    }
}
