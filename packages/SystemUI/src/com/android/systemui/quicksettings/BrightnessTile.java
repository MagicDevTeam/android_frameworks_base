/*
 * Copyright (C) 2012 The Android Open Source Project
 * Copyright (C) 2013 CyanogenMod Project
 * Copyright (C) 2013 The SlimRoms Project
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

package com.android.systemui.quicksettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.settings.BrightnessController.BrightnessStateChangeCallback;

public class BrightnessTile extends QuickSettingsTile implements BrightnessStateChangeCallback {

    private static final String TAG = "BrightnessTile";

    public BrightnessTile(Context context, final QuickSettingsController qsc) {
        super(context, qsc);

        mOnClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                flipTile(0);
                qsc.mBar.collapseAllPanels(true);
                showBrightnessDialog();
            }
        };

        mOnLongClick = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startSettingsActivity(Settings.ACTION_DISPLAY_SETTINGS);
                return true;
            }

        };

        qsc.registerObservedContent(Settings.System.getUriFor(
                Settings.System.SCREEN_BRIGHTNESS), this);
        qsc.registerObservedContent(Settings.System.getUriFor(
                Settings.System.SCREEN_BRIGHTNESS_MODE), this);
        onBrightnessLevelChanged();
    }

    private void showBrightnessDialog() {
        Intent intent = new Intent(Intent.ACTION_SHOW_BRIGHTNESS_DIALOG);
        mContext.sendBroadcast(intent);
    }

    @Override
    void onPostCreate() {
        updateTile();
        super.onPostCreate();
    }

    @Override
    public void updateResources() {
        updateTile();
        super.updateResources();
    }

    private synchronized void updateTile() {
        int mode;
        try {
            mode = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            boolean autoBrightness = (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            mDrawable = autoBrightness
                    ? R.drawable.ic_qs_brightness_auto_on
                    : R.drawable.ic_qs_brightness_auto_off;
            mLabel = mContext.getString(R.string.quick_settings_brightness_label);
        } catch (SettingNotFoundException e) {
            Log.e(TAG, "Brightness setting not found", e);
        }
    }

    @Override
    public void onBrightnessLevelChanged() {
        updateResources();
    }

    @Override
    public void onChangeUri(ContentResolver resolver, Uri uri) {
        updateResources();
    }
}
