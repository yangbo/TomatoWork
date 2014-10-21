package com.wave.tomatowork;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	private static String TAG = "TW-MA";
	private static long TotalCount = 25*60L;
	private static CountDownTask countDownTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// start count down thread
		// 需要正确处理 Activity 多次创建的问题。
		startCountTask();
		//queryMediaStore();
	}

	@SuppressWarnings("unused")
	private void queryMediaStore() {
		ContentResolver contentResolver = getContentResolver();
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor == null) {
		    // query failed, handle error.
		} else if (!cursor.moveToFirst()) {
		    // no media on the device
		} else {
		    int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
		    int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
		    do {
		       long thisId = cursor.getLong(idColumn);
		       String thisTitle = cursor.getString(titleColumn);
		       // ...process entry...
		       Log.d(TAG, "Music Title: " + thisTitle);
		       Log.d(TAG, "Music id: " + thisId);
		    } while (cursor.moveToNext());
		}		
	}

	private void startCountTask() {
		if (countDownTask == null){
			Log.d(TAG, "开始 count down thread.");
			countDownTask = new CountDownTask(this);
			countDownTask.execute(TotalCount);
		}else{
			Log.d(TAG, "Already started a task, so we don't need start again.");
			// 但要更新 task 的属性
			countDownTask.setMainActivity(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
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
		return super.onOptionsItemSelected(item);
	}
}
