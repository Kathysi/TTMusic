package com.ssc.ttmusic;

import java.io.File;

import com.ssc.ttmusic.service.PlayService;
import com.ssc.ttmusic.service.PlayService.onMusicEventListner;
import com.xiami.sdk.entities.OnlineSong;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public abstract class BaseActivity extends FragmentActivity {
	protected PlayService mPlayService;

	// 绑定service
	private ServiceConnection mPlayServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mPlayService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.i("symm", "serviceconnected");
			mPlayService = ((PlayService.PlayBinder) service).getPlayService();
			mPlayService.setOnMusicEventListener(musicEventListner);
			bindService();

		}
	};

	public void allowBindService() {
		boolean isbool= bindService(new Intent(this, PlayService.class),
				mPlayServiceConnection, Context.BIND_AUTO_CREATE);
		Log.i("symm", "是否绑定"+isbool);

	}

	/**
	 * fragment的view消失后回调
	 */
	public void allowUnbindService() {
		unbindService(mPlayServiceConnection);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		allowBindService();
		IntentFilter filter = new IntentFilter("com.ssc.playaction");
		registerReceiver(receiver, filter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			onplayReceive();
		}
	};

	public abstract void onplayReceive();

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		allowUnbindService();
		unregisterReceiver(receiver);
	}

	public abstract void bindService();

	public abstract void onPublish(int percent);

	public abstract void onError();

	public abstract void onCompletion();

	public abstract void onChange(OnlineSong song);

	public abstract void onAutoDownload(File file);

	public abstract void onPlay(OnlineSong song);

	public abstract void onMusicPause();

	private onMusicEventListner musicEventListner = new onMusicEventListner() {

		@Override
		public void onPublish(int percent) {
			// TODO Auto-generated method stub

			Log.i("sym", "baseactivity" + percent);
			BaseActivity.this.onPublish(percent);

		}

		@Override
		public void onError() {
			// TODO Auto-generated method stub

			BaseActivity.this.onError();

		}

		@Override
		public void onCompletion() {
			// TODO Auto-generated method stub

			BaseActivity.this.onCompletion();
		}

		@Override
		public void onChange(OnlineSong song) {
			// TODO Auto-generated method stub

			BaseActivity.this.onChange(song);
		}

		@Override
		public void onAutoDownLoad(File file) {
			// TODO Auto-generated method stub

			BaseActivity.this.onAutoDownload(file);
		}

		@Override
		public void onPlay(OnlineSong song) {
			// TODO Auto-generated method stub
			BaseActivity.this.onPlay(song);
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			BaseActivity.this.onMusicPause();
		}
	};
}
