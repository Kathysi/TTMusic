package com.ssc.ttmusic;

import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.bean.LoveSong;
import com.ssc.ttmusic.tools.MusicOprate;
import com.ssc.ttmusic.untils.LrcUntils;
import com.ssc.ttmusic.view.CircleImageView;
import com.ssc.ttmusic.view.LrcView;
import com.xiami.sdk.entities.OnlineSong;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {

	private OnlineSong mSong;
	AnimatorSet animSet;
	private float mValueAvatar = 0f;
	private static final int ROTATE_TIME = 12 * 1000;
	private static final int ROTATE_COUNT = 10000;
	private ObjectAnimator mAvatar;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private Button arrowButton;
	private TextView titleTextView;
	private CircleImageView cImageView;
	private Button preButton, playButton, nextButton, playlistButton;

	private Button downButton;
	private TextView nowtimeView, totalTextView;
	private SeekBar mBar;

	private LrcView lrcView;
	private CheckBox cBox;
	private MusicOprate loveOperate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		// allowBindService();
		loveOperate = new MusicOprate(this);
		initOption();
		initView();

	}

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// Log.i("symm", "playactivity onresume");
	// allowBindService();
	// }
	//
	// @Override
	// protected void onPause() {
	// // TODO Auto-generated method stub
	// super.onPause();
	// Log.i("symm", "playactivity onpause");
	// allowUnbindService();
	// }

	private void initOption() {
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.defaulticon)
				.showImageOnFail(R.drawable.defaulticon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		imageLoader = ImageLoader.getInstance();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//overridePendingTransition(0,R.anim.activity_close); 
	}

	private void initAvatar(float start) {
		mAvatar = ObjectAnimator.ofFloat(cImageView, "rotation", start,
				360f + start);
		mAvatar.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				mValueAvatar = (Float) animation.getAnimatedValue("rotation");
			}
		});
		mAvatar.setDuration(ROTATE_TIME);
		mAvatar.setInterpolator(new LinearInterpolator());
		mAvatar.setRepeatCount(ROTATE_COUNT);
	}

	private void initView() {
		// TODO Auto-generated method stub
		animSet = new AnimatorSet();

		cBox = (CheckBox) findViewById(R.id.play_fav);
		cBox.setOnCheckedChangeListener(this);
		lrcView = (LrcView) findViewById(R.id.play_lrc);
		arrowButton = (Button) findViewById(R.id.play_arrow);
		titleTextView = (TextView) findViewById(R.id.play_songname_textview);
		cImageView = (CircleImageView) findViewById(R.id.play_avatar);
		mBar = (SeekBar) findViewById(R.id.play_seekbar);
		nowtimeView = (TextView) findViewById(R.id.play_nowtime_textview);
		totalTextView = (TextView) findViewById(R.id.play_alltime_textview);
		preButton = (Button) findViewById(R.id.play_playprebutton);
		playButton = (Button) findViewById(R.id.play_playbutton);
		nextButton = (Button) findViewById(R.id.play_playnextbutton);
		playlistButton = (Button) findViewById(R.id.play_playlistbutton);
		downButton = (Button) findViewById(R.id.play_download);
		downButton.setOnClickListener(downClickListener);
		arrowButton.setOnClickListener(this);
		preButton.setOnClickListener(this);
		playButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		mBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int progress = seekBar.getProgress();

				lrcView.onDrag(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (fromUser)
					mPlayService.seekTo(progress);
			}
		});

	}

	OnClickListener downClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mPlayService != null) {
				mPlayService.down();
				Toast.makeText(PlayActivity.this, R.string.down_starttoast,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void initUI() {
		Log.i("sym", "" + mPlayService.isPlaying());
		initAvatar(mValueAvatar);
		if (mPlayService.isPlaying()) {
			playButton.setBackgroundResource(R.drawable.btn_pause);
			animSet.play(mAvatar);
			animSet.start();
		}

	}

	private void play() {
		animSet.play(mAvatar);
		animSet.start();
		mPlayService.resume();
	}

	private void pause() {
		animSet.cancel();
		initAvatar(mValueAvatar);
		mPlayService.pause();
	}

	private void next() {
		mPlayService.next();
		playButton.setBackgroundResource(R.drawable.btn_pause);
	}

	private void pre() {
		mPlayService.pre();
		playButton.setBackgroundResource(R.drawable.btn_pause);
	}

	public void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPublish(int percent) {
		// TODO Auto-generated method stub
		Log.i("tag", "precent" + percent);
		nowtimeView.setText(timeToStr(percent));
		mBar.setProgress(percent);
		mBar.setMax(mPlayService.getTotalDur());
		totalTextView.setText(timeToStr(mPlayService.getTotalDur()));
		if (lrcView.hasLrc()) {
			lrcView.changeCurrent(percent);
		}
	}

	private String timeToStr(int time) {

		int totalDuration = time / 1000;
		int minute = totalDuration / 60;
		int second = totalDuration % 60;
		return String.format("%02d:%02d", minute, second);

	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChange(OnlineSong song) {
		// TODO Auto-generated method stub
		Log.i("sym", "yy");
		initData();
	}

	@Override
	public void onAutoDownload(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlay(OnlineSong song) {
		// TODO Auto-generated method stub
		initData();
	}

	private void initData() {
		mSong = mPlayService.getCurrSong();
		if (mSong != null) {
			titleTextView.setText(mSong.getSongName());

			if (loveOperate.searchSongs(mSong.getSongId())) {
				cBox.setChecked(true);
			} else {
				cBox.setChecked(false);
			}
			totalTextView.setText(timeToStr(mPlayService.getTotalDur()));
			new IconTask().execute(mSong.getImageUrl(400));
			new LrcTask(mSong.getSongName(), mSong.getArtistName(),
					mSong.getSongId()).execute("");
		}
	};

	class LrcTask extends AsyncTask<String, Integer, String> {
		private String name;
		private String singer;
		private long id;

		public LrcTask(String name, String singer, long id) {
			this.name = name;
			this.singer = singer;
			this.id = id;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String musicLrc = LrcUntils.getXiamiLrc(name, singer, id);
			return musicLrc;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				lrcView.setLrcPath(result);
			}
		}

	}

	class IconTask extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			String uri = params[0];
			HttpGet get = new HttpGet(uri);
			HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					InputStream is = entity.getContent();
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					is.close();
					return bitmap;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {

				cImageView.setImageBitmap(result);

			}
		}

	}

	@Override
	public void bindService() {
		// TODO Auto-generated method stub
		Log.i("tag", "bind");
		initData();
		initUI();
	}

	@Override
	public void onMusicPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.play_arrow:
			finish();
			break;

		case R.id.play_playprebutton:
			pre();
			break;
		case R.id.play_playnextbutton:
			next();
			break;
		case R.id.play_playbutton:
			if (mPlayService.isPlaying()) {
				playButton.setBackgroundResource(R.drawable.btn_play);
				pause();
			} else {
				playButton.setBackgroundResource(R.drawable.btn_pause);
				play();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onplayReceive() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		OnlineSong song = mPlayService.getCurrSong();

		if (song == null) {
			Toast.makeText(PlayActivity.this, R.string.toast_lovesong,
					Toast.LENGTH_SHORT).show();
			return;
		}
		LoveSong loveSong = new LoveSong();
		loveSong.setName(song.getSongName());
		loveSong.setSinger(song.getArtistName());
		loveSong.setSongid(song.getSongId());
		loveSong.setUrl(song.getImageUrl(100));
		if (isChecked) {
			loveOperate.insert(loveSong);
		} else {

			loveOperate.deleteBymId(song.getSongId());
		}
	}

}
