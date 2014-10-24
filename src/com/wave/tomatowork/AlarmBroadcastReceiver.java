package com.wave.tomatowork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "TW-MA";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "收到 broadcast intent: " + intent);
	}

}
