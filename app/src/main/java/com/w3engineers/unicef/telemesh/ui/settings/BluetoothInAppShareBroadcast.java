package com.w3engineers.unicef.telemesh.ui.settings;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.w3engineers.unicef.util.helper.Utils;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [28-Feb-2019 at 4:24 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [28-Feb-2019 at 4:24 PM].
 * --> <Second Editor> on [28-Feb-2019 at 4:24 PM].
 * Reviewed by :
 * --> <First Reviewer> on [28-Feb-2019 at 4:24 PM].
 * --> <Second Reviewer> on [28-Feb-2019 at 4:24 PM].
 * ============================================================================
 **/
public class BluetoothInAppShareBroadcast extends BroadcastReceiver {

    String connectedBleUsersAddress = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (TextUtils.equals(action, BluetoothDevice.ACTION_ACL_CONNECTED)) {
            connectedBleUsersAddress = device.getAddress();
        } else if (TextUtils.equals(action, BluetoothDevice.ACTION_ACL_DISCONNECTED)) {

            if (TextUtils.equals(connectedBleUsersAddress, device.getAddress())) {
                Utils.getInstance().deleteBackUpApk();
            }

        }
    }
}
