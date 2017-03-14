package com.ssc.ttmusic.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.ssc.ttmusic.MainActivity;
import com.ssc.ttmusic.R;
import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.fragment.OnlineFragment;
import com.ssc.ttmusic.untils.MusicUntils;
import com.xiami.player.PlayMode;
import com.xiami.sdk.MusicPlayer;
import com.xiami.sdk.MusicPlayer.OnAutoDownloadCompleteListener;
import com.xiami.sdk.MusicPlayer.OnCompletionListener;
import com.xiami.sdk.MusicPlayer.OnErrorListener;
import com.xiami.sdk.MusicPlayer.OnPreparedListener;
import com.xiami.sdk.MusicPlayer.OnSongChangedListener;
import com.xiami.sdk.entities.OnlineSong;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class PlayService extends Service implements OnCompletionListener,
		OnSongChangedListener, OnErrorListener, OnAutoDownloadCompleteListener,
		OnPreparedListener {

	public static final String ACTION = "com.ssc.ttmusic.action";
	public static final String PLAYTAG = "tag";
	public static final String PLAY = "play";
	public static final String PAUSE = "pause";
	private MusicPlayer mPlayer;// 虾米播放器
	private onMusicEventListner eventListner;// 播放回调
	// private int mPlayPosition;
	private ExecutorService execService = Executors.newFixedThreadPool(5);// 线程池

	public class PlayBinder extends Binder {
		public PlayService getPlayService() {
			return PlayService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("symm", "onbind");
		return new PlayBinder();
	}

	private boolean isflag = false;

	private Thread th;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MusicUntils.initMusics();
		mPlayer = App.mSdk.createMusicPlayer();
		mPlayer.setOnCompletionListener(this);
		mPlayer.setOnSongChangeListener(this);
		mPlayer.setOnErrorListener(this);
		th=new Thread(publishRunnable);
		th.start();
		IntentFilter filter = new IntentFilter(OnlineFragment.ACTION);
		registerReceiver(receiver, filter);

	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			play();
			Toast.makeText(PlayService.this,
					getResources().getString(R.string.addsongs_toast),
					Toast.LENGTH_SHORT).show();
		}
	};

	// 重新播放
	public void play(int position) {
		mPlayer.setSongs(MusicUntils.mOnlineSongs, position);
		mPlayer.setAutoPlayNext(true);
		mPlayer.setPlayMode(setMode());
		mPlayer.setOnPreparedListener(this);
		// start();
	}

	public void play() {
		mPlayer.setSongs(MusicUntils.mOnlineSongs);
		mPlayer.setAutoPlayNext(true);
		mPlayer.setPlayMode(setMode());
		mPlayer.setOnPreparedListener(this);
		// start();
	}

	private PlayMode setMode() {
		int mode = MainActivity.getPref(this);
		PlayMode playMode = null;
		switch (mode) {
		case MainActivity.LOOP:
			playMode = PlayMode.LOOP_LIST;
			break;

		case MainActivity.RANDOM:
			playMode = PlayMode.SHUFFLE;
			break;
		case MainActivity.SINGLE:
			playMode = PlayMode.LOOP_SINGLE;
			break;
		default:
			break;
		}
		return playMode;
	}

	public void start() {
		Log.i("sym", "onprepared");
		mPlayer.play();

		Intent intent = new Intent("com.ssc.playaction");
		sendBroadcast(intent);
		if (mPlayer != null && eventListner != null) {
			handler.sendEmptyMessage(1);

		}

	}

	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	// 切换播放模式
	public void setPlayMode(PlayMode mode) {
		mPlayer.setPlayMode(mode);
	}

	public PlayMode getPlayMode() {
		return mPlayer.getPlayMode();
	}
	// 恢复播放
	public void resume() {
		try {
			if (!mPlayer.isPlaying()) {
				mPlayer.play();
				eventListner.onPlay(getCurrSong());
				// sendBroad(PLAY);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// 暂停播放
	public void pause() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
			eventListner.onPause();
			// sendBroad(PAUSE);
		}
	}

	// 播放下一曲
	public void next() {
		mPlayer.playNext();
	}

	// 播放上一曲
	public void pre() {
		mPlayer.playPrev();

	}

	// 下载歌曲
	public void down() {
		OnlineSong song = getCurrSong();
		if (song != null) {
			// Log.i("sssss", song.getListenFile());
			execService.submit(new DownRunnable(song.getListenFile(), song
					.getSongName()));
		}
	}

	class DownRunnable implements Runnable {
		public String downfile;
		public String songName;

		public DownRunnable(String downfile, String songName) {
			this.downfile = downfile;
			this.songName = songName;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			OutputStream output = null;
			InputStream is = null;
			File file = new File(MusicUntils.getMusicDir() + songName + ".mp3");
			if (file.exists()) {
				handler.sendEmptyMessage(3);
				return;
			}
			URL url = null;
			try {
				url = new URL(downfile);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				handler.sendEmptyMessage(5);
				e1.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();

				is = conn.getInputStream();
				file.createNewFile();
				output = new FileOutputStream(file);
				byte[] buffer = new byte[4 * 1024];
				while (is.read(buffer) != -1) {
					output.write(buffer);
				}

				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				handler.sendEmptyMessage(5);
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(4);
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public OnlineSong getCurrSong() {
		return (OnlineSong) mPlayer.getCurrentSong();
	}

	public void seekTo(int pos) {
		if (mPlayer.isPlaying())
			mPlayer.seekTo(pos);
	}

	public int getTotalDur() {
		if (mPlayer != null) {
			return mPlayer.getDuration();
		}
		return 0;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("symm", "onunbind");
		eventListner = null;
		return true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("symm", "ondestroy");
		flag=false;
		th.interrupt();
		th=null;
		mPlayer.release();
		unregisterReceiver(receiver);
		
		super.onDestroy();
	}

	public void setOnMusicEventListener(onMusicEventListner l) {
		this.eventListner = l;
	}

	private boolean flag=true;
	private Runnable publishRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
		while(flag)
		{

				if (mPlayer != null && mPlayer.isPlaying()
						&& eventListner != null) {
					
					handler.sendEmptyMessage(0);

				}
				SystemClock.sleep(100);
		}
		}
	};
	private void scanSDCard() {
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
				Uri.parse("file://"+ MusicUntils.getMusicDir())));
	}
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0) {
				try {
					eventListner.onPublish(mPlayer.getCurrentPosition());
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
			if (msg.what == 1) {

				try {
					eventListner
							.onChange((OnlineSong) mPlayer.getCurrentSong());
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
			if (msg.what == 4) {
				scanSDCard();
				Toast.makeText(PlayService.this, R.string.down_success,
						Toast.LENGTH_SHORT).show();
			}
			if (msg.what == 5) {
				Toast.makeText(PlayService.this, R.string.down_error,
						Toast.LENGTH_SHORT).show();
			}
			if (msg.what == 3) {
				Toast.makeText(PlayService.this, R.string.down_hastoast,
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	// 音乐播放回调接口
	public interface onMusicEventListner {

		public void onPublish(int percent);// 进度回调接口

		public void onChange(OnlineSong song);// 歌曲切换回调接口

		public void onError();// 播放错误回调接口

		public void onCompletion();// 播放完成回调接口

		public void onAutoDownLoad(File file);

		public void onPlay(OnlineSong song);

		public void onPause();
	}

	@Override
	public void onCompletion(int arg0) {
		// TODO Auto-generated method stub
		if (eventListner != null) {
			eventListner.onCompletion();
		}
	}

	@Override
	public void onSongChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int arg0, int arg1) {
		// TODO Auto-generated method stub
		if (eventListner != null)
			eventListner.onError();
	}

	@Override
	public void onDownloaded(File arg0) {
		// TODO Auto-generated method stub
		if (eventListner != null) {
			eventListner.onAutoDownLoad(arg0);
		}
	}

	@Override
	public void onPrepared() {
		// TODO Auto-generated method stub

		start();
	}
}
