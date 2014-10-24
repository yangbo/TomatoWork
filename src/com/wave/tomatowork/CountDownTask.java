package com.wave.tomatowork;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

public class CountDownTask extends AsyncTask<Long, Long, Long> implements OnCompletionListener {

	private static String TAG = "TW-MA";
	private MainActivity mainActivity;
	private MediaPlayer mMediaPlayer;
	private long totalSeconds;
	private long endTime;
	private AlarmManager alarmMgr;
	private WakeLock lock;
	
	public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public CountDownTask(MainActivity mainActivity){
		this.mainActivity = mainActivity;
	}
	
	@Override
	protected Long doInBackground(Long... params) {
		totalSeconds = params[0];
		endTime = SystemClock.elapsedRealtime() + totalSeconds * 1000L;
		// startAlarmManager();
		acquireWakeLock();
		while(true){
			try {
				
				long remainSeconds = (endTime - SystemClock.elapsedRealtime())/1000;
				remainSeconds = Math.max(0, remainSeconds);
				Log.d(TAG, "总秒数: " + totalSeconds + "，剩余秒数: " + remainSeconds);
				publishProgress(remainSeconds);
				if (remainSeconds <= 0 && this.mMediaPlayer == null){
					// alert
					Log.d(TAG, "剩余秒数 <= 0, 时间到，响铃！");
					playNotifySound();
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Thread be interrupted! " + e);
			}
		}
	}

	// 获取 partial wake lock
	private void acquireWakeLock() {
		PowerManager pm = (PowerManager) mainActivity.getSystemService(Context.POWER_SERVICE);
		lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TomatoWork partial lock");
		lock.acquire();
	}

	private void releaseWakeLock() {
		if (lock != null){
			lock.release();
		}
	}
	
	@SuppressWarnings("unused")
	private void startAlarmManager() {
		Context context = mainActivity.getApplicationContext();
		if (alarmMgr == null){
			alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		}
		Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		Log.d(TAG, "调度 AlarmManager.setRepeating...");
		alarmMgr.setRepeating(
			AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+1000, 5000L, pendingIntent
		);
	}

	private void playNotifySound() {
		// 先检查 MediaPlayer 的状态
		if (mMediaPlayer == null){
			long id = 75441;	/* retrieve it from somewhere */;
			Uri contentUri = ContentUris.withAppendedId(
			        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mMediaPlayer.setDataSource(mainActivity.getApplicationContext(), contentUri);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				mMediaPlayer.setOnCompletionListener(this);
			} catch (Exception e) {
				Log.e(TAG, "不能播放通知声音。" + e);
			}
		}else{
			Log.i(TAG, "已经有一个MediaPlayer存在，它还没播放完，这次先不播放声音了。");
		}
	}

	private void updateCountDownText(Long[] values) {
		TextView text = (TextView) mainActivity.findViewById(R.id.countDownLabel);
		long count = values[0];
		StringBuffer buf = new StringBuffer();
		String countLabel = mainActivity.getString(R.string.countdown);
		buf.append(countLabel).append(" ").append(count);
		text.setText(buf);
	}

	@Override
	protected void onPostExecute(Long result) {
		super.onPostExecute(result);
		releaseWakeLock();
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);
		updateCountDownText(values);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mMediaPlayer = null;
		mp.release();
		this.endTime = SystemClock.elapsedRealtime() + this.totalSeconds*1000L;
	}
	
}
