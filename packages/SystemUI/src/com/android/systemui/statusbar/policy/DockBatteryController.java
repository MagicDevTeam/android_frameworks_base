/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.android.systemui.R;

import java.util.ArrayList;

public class DockBatteryController extends BatteryController {
    private static final String TAG = "StatusBar.DockBatteryController";

    private int mDockBatteryStatus = BatteryManager.BATTERY_STATUS_UNKNOWN;
    private int mBatteryLevel = 0;
    private boolean mBatteryPlugged = false;
    private boolean mBatteryPresent = false;

    public DockBatteryController(Context context) {
        this(context, true);
    }
    
    public DockBatteryController(Context context, boolean ui) {
        super(context);
    }    

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mDockBatteryStatus = intent.getIntExtra(
                                        BatteryManager.EXTRA_STATUS,
                                        BatteryManager.BATTERY_STATUS_UNKNOWN);
            mBatteryPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
            mBatteryPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
            updateViews();
            updateBattery();
        }
    }

    @Override
    protected void updateViews() {
        super.updateViews();

        for (DockBatteryStateChangeCallback cb : mChangeCallbacks) {
            cb.onDockBatteryLevelChanged(getBatteryLevel(), isBatteryPresent(), getBatteryStatus());
        }
    }

    private ArrayList<DockBatteryStateChangeCallback> mChangeCallbacks =
            new ArrayList<DockBatteryStateChangeCallback>();

    public interface DockBatteryStateChangeCallback {
        public void onDockBatteryLevelChanged(int level, boolean present, int status);
    }

    public void addStateChangedCallback(DockBatteryStateChangeCallback cb) {
        mChangeCallbacks.add(cb);
        // trigger initial update
        cb.onDockBatteryLevelChanged(getBatteryLevel(), isBatteryPresent(), getBatteryStatus());
    }

    public void removeStateChangedCallback(DockBatteryStateChangeCallback cb) {
        mChangeCallbacks.remove(cb);
    }

    @Override
    protected int getBatteryLevel() {
        return mBatteryLevel;
    }

    @Override
    protected int getBatteryStatus() {
        return mDockBatteryStatus;
    }

    @Override
    protected boolean isBatteryPlugged() {
        return mBatteryPlugged;
    }

    @Override
    protected boolean isBatteryPresent() {
        return mBatteryPresent;
    }

}
