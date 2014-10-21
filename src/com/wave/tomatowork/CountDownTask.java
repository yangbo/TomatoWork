package com.wave.tomatowork;

import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class CountDownTask extends AsyncTask<Long, Long, Long> implements OnCompletionListener {

	private static String TAG = "TW-MA";
	private MainActivity mainActivity;
	private MediaPlayer mMediaPlayer;
	
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
		long totalCount = params[0];
		long countDown = totalCount;
		while(--countDown >= 0){
			try {
				Log.d(TAG, "CountDown: " + countDown);
				Thread.sleep(1000);
				publishProgress(countDown);
			} catch (InterruptedException e) {
				Log.e(TAG, "Thread be interrupted! " + e);
			}
			if (countDown <= 0){
				// alert
				Log.d(TAG, "Count down number <= 0, should alert user.");
				countDown = totalCount;
				playNotifySound();
			}
		}
		return countDown;
	}

	private void playNotifySound() {
		long id = 75441;	/* retrieve it from somewhere */;
		Uri contentUri = ContentUris.withAppendedId(
		        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

		// 先检查 MediaPlayer 的状态
		if (mMediaPlayer == null){
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
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);
		updateCountDownText(values);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.release();
		mMediaPlayer = null;
	}
	
}
