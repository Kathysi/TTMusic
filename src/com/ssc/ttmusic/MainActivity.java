package com.ssc.ttmusic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.fragment.LocalFragment;
import com.ssc.ttmusic.fragment.OnlineFragment;
import com.ssc.ttmusic.service.PlayService;
import com.ssc.ttmusic.untils.MusicUntils;
import com.ssc.ttmusic.view.CircleImageView;
import com.ssc.ttmusic.view.Indicator;
import com.xiami.player.PlayMode;
import com.xiami.sdk.entities.OnlineSong;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener {

	private Indicator mIndicator;
	private TextView localTextView;
	private TextView onlineTextView;
	private ViewPager mViewPager;
	private List<Fragment> mArrayList = new ArrayList<Fragment>();

	private Button searchButton;

	// miniplayer
	private CircleImageView miniIcon;
	private TextView minititleView;
	private TextView minisingerView;
	private Button miniplayButton;
	private Button miniplaylistButton;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private View miniView;

	public static final int SINGLE = 2;
	public static final int LOOP = 1;
	public static final int RANDOM = 3;
	public static final String PLAYMODE = "playmode";
	public static final String MODE = "mode";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initOption();
		initViews();
		initMiniPlayer();
		// registerReceiver(receiver, new IntentFilter(PlayService.ACTION));

	}

	BroadcastReceiver playReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String extra = intent.getStringExtra(PlayService.PLAYTAG);
			if (extra.equals(PlayService.PLAY)) {
				miniplayButton
						.setBackgroundResource(R.drawable.player_btnpause);
				isPlay = true;
			} else if (extra.equals(PlayService.PAUSE)) {
				miniplayButton.setBackgroundResource(R.drawable.player_btnplay);
				isPlay = false;

			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("mmm", "mainactivity onpause");
		// allowBindService();
		if(mPlayService!=null)
		{
		if (mPlayService.isPlaying()) {
			miniplayButton.setBackgroundResource(R.drawable.player_btnpause);
			isPlay = true;
		} else {
			miniplayButton.setBackgroundResource(R.drawable.player_btnplay);
			isPlay = true;
		}
		}
	}

	//
	// @Override
	// protected void onPause() {
	// // TODO Auto-generated method stub
	// super.onPause();
	// Log.i("symm", "mainactivity onpause");
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

	private void initMiniPlayer() {
		// TODO Auto-generated method stub
		miniView = (View) findViewById(R.id.miniplayer);
		miniIcon = (CircleImageView) findViewById(R.id.mini_icon);
		minititleView = (TextView) findViewById(R.id.mini_musictext);
		minisingerView = (TextView) findViewById(R.id.mini_musicsingertext);
		miniplayButton = (Button) findViewById(R.id.mini_btnplay);
		miniplaylistButton = (Button) findViewById(R.id.mini_playlist);
		miniIcon.setOnClickListener(miniClickListener);
		miniplayButton.setOnClickListener(miniClickListener);
		miniplaylistButton.setOnClickListener(miniClickListener);
	}

	OnClickListener miniClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.mini_icon:
				startActivity(new Intent(MainActivity.this, PlayActivity.class));
				//overridePendingTransition(R.anim.activity_open, 0);
				break;

			case R.id.mini_btnplay:
				if (mPlayService.isPlaying()) {
					// 暂停播放
					isPlay = false;
					mPlayService.pause();
					miniplayButton
							.setBackgroundResource(R.drawable.player_btnplay);
				} else {
					// 恢复播放
					isPlay = true;
					mPlayService.resume();
					miniplayButton
							.setBackgroundResource(R.drawable.player_btnpause);
				}
				break;
			case R.id.mini_playlist:

				onPopWindow();
				break;
			default:
				break;
			}
		}
	};

	private TextView popTitleSongSize;
	private Button popPlayModeButton;
	private ListView popMusicListView;
	PopListAdapter adapter;

	private void showListView(ListView listView) {

		adapter = new PopListAdapter(this, MusicUntils.mOnlineSongs);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mPlayService.play(position);

			}
		});
		changePopWindowList();
		// OnlineSong song = mPlayService.getCurrSong();
		// if (song != null) {
		// for (int i = 0; i < MusicUntils.mOnlineSongs.size(); i++) {
		// if (song.equals(MusicUntils.mOnlineSongs.get(i))) {
		// Log.i("SYM", "POS=" + i);
		// if(adapter!=null)
		// {
		// adapter.setTag(i);
		// adapter.notifyDataSetChanged();
		// popMusicListView.setSelection(i);
		// }
		// break;
		// }
		// }
		// }

	}

	class PopListAdapter extends BaseAdapter {
		private Context context;
		private List<OnlineSong> songs;

		private int pos = -1;

		public PopListAdapter(Context context, List<OnlineSong> songs) {
			this.context = context;
			this.songs = songs;
		}

		public void clear()
		{
			this.songs.clear();
			pos=-1;
			notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return songs.size();
		}

		public void setSongs(List<OnlineSong> songs) {
			this.songs = songs;
			notifyDataSetChanged();
		}

		public void setTag(int pos) {
			this.pos = pos;
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return songs.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.pop_listviewitem, null);
				holder.nameTextView = (TextView) convertView
						.findViewById(R.id.popitem_name);
				holder.delButton = (ImageButton) convertView
						.findViewById(R.id.popitem_delbutton);
				holder.tagImageView = (ImageView) convertView
						.findViewById(R.id.popitem_playtag);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (pos == position) {
				if (mPlayService.isPlaying()) {
					holder.tagImageView
							.setImageResource(R.drawable.playlist_playing);
					holder.tagImageView.setVisibility(View.VISIBLE);
				} else {
					holder.tagImageView
							.setImageResource(R.drawable.list_icon_playing);
					holder.tagImageView.setVisibility(View.VISIBLE);
				}
			} else {
				holder.tagImageView.setVisibility(View.INVISIBLE);
			}
			OnlineSong song = songs.get(position);
			holder.nameTextView.setText(position + ". " + song.getSongName());

			holder.delButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (adapter != null) {
						MusicUntils.mOnlineSongs.remove(position);
						popTitleSongSize.setText(MusicUntils.mOnlineSongs
								.size()
								+ getResources().getString(
										R.string.pop_titlelasttext));
						adapter.setSongs(MusicUntils.mOnlineSongs);
						adapter.notifyDataSetChanged();
						mPlayService.play();

					}
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView nameTextView;
			ImageButton delButton;
			ImageView tagImageView;
		}
	}

	private TextView popClearView;
	private void onPopWindow() {
		View view = getLayoutInflater().inflate(R.layout.popwindow, null);
		PopupWindow mPopupWindow = new PopupWindow(view, App.screenWidth,
				App.screenHeight / 2);

		popTitleSongSize = (TextView) view.findViewById(R.id.pop_songssize);
		popPlayModeButton = (Button) view.findViewById(R.id.popsongsplaymode);
		popMusicListView = (ListView) view.findViewById(R.id.pop_listview);
		popClearView=(TextView)view.findViewById(R.id.pop_clear);
		popClearView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MusicUntils.mOnlineSongs.clear();
				adapter.clear();
				popTitleSongSize.setText(MusicUntils.mOnlineSongs.size()
						+ getResources().getString(R.string.pop_titlelasttext));
			}
		});
		showListView(popMusicListView);
		popTitleSongSize.setText(MusicUntils.mOnlineSongs.size()
				+ getResources().getString(R.string.pop_titlelasttext));
		int mode = getPref(this);
		switch (mode) {
		case LOOP:
			popPlayModeButton.setBackgroundResource(R.drawable.mode_normal);
			break;
		case SINGLE:
			popPlayModeButton.setBackgroundResource(R.drawable.mode_one);
			break;
		case RANDOM:
			popPlayModeButton.setBackgroundResource(R.drawable.mode_random);
			break;
		default:
			break;
		}
		popPlayModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PlayMode playmode = mPlayService.getPlayMode();
				if (playmode == PlayMode.LOOP_LIST) {
					popPlayModeButton
							.setBackgroundResource(R.drawable.mode_one);
					mPlayService.setPlayMode(PlayMode.LOOP_SINGLE);
					Toast.makeText(MainActivity.this, R.string.playmode_single,
							Toast.LENGTH_SHORT).show();
					savePref(MainActivity.this, SINGLE);
				}
				if (playmode == PlayMode.LOOP_SINGLE) {
					popPlayModeButton
							.setBackgroundResource(R.drawable.mode_random);
					mPlayService.setPlayMode(PlayMode.SHUFFLE);
					Toast.makeText(MainActivity.this, R.string.playmode_random,
							Toast.LENGTH_SHORT).show();
					savePref(MainActivity.this, RANDOM);
				}
				if (playmode == PlayMode.SHUFFLE) {
					popPlayModeButton
							.setBackgroundResource(R.drawable.mode_normal);
					mPlayService.setPlayMode(PlayMode.LOOP_LIST);
					Toast.makeText(MainActivity.this, R.string.playmode_loop,
							Toast.LENGTH_SHORT).show();
					savePref(MainActivity.this, LOOP);
				}
			}
		});
		mPopupWindow
				.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mPopupWindow.setAnimationStyle(R.style.popwin_anim);
		mPopupWindow.setFocusable(true);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}

	public static void savePref(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(PLAYMODE,
				MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(MODE, value);
		editor.commit();
	}

	public static int getPref(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PLAYMODE,
				MODE_PRIVATE);
		int mode = sp.getInt(MODE, 1);
		return mode;
	}

	private void initViews() {
		// TODO Auto-generated method stub
		LocalFragment localFragment = new LocalFragment();
		OnlineFragment onlineFragment = new OnlineFragment();
		mArrayList.add(localFragment);
		mArrayList.add(onlineFragment);
		mIndicator = (Indicator) findViewById(R.id.main_indicator);
		localTextView = (TextView) findViewById(R.id.tv_main_local);
		onlineTextView = (TextView) findViewById(R.id.tv_main_online);
		mViewPager = (ViewPager) findViewById(R.id.vp_main_container);
		searchButton = (Button) findViewById(R.id.search_btn);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,
						SearchActivity.class));
			}
		});
		mViewPager.setAdapter(mPagerAdapter);
		selectTab(0);
		mViewPager.setOnPageChangeListener(pageChangeListener);
		localTextView.setOnClickListener(this);
		onlineTextView.setOnClickListener(this);

	}

	OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			selectTab(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			mIndicator.scroll(arg0, arg1);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	private void selectTab(int index) {
		switch (index) {
		case 0:
			localTextView.setTextColor(getResources().getColor(R.color.white));
			onlineTextView.setTextColor(getResources().getColor(
					R.color.whitesmoke));
			break;
		case 1:
			localTextView.setTextColor(getResources().getColor(
					R.color.whitesmoke));
			onlineTextView.setTextColor(getResources().getColor(R.color.white));
			break;
		}
	}

	private FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(
			getSupportFragmentManager()) {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return mArrayList.get(arg0);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_main_local:
			mViewPager.setCurrentItem(0);
			break;

		case R.id.tv_main_online:
			mViewPager.setCurrentItem(1);
			break;
		}
	}

	@Override
	public void onPublish(int percent) {
		// TODO Auto-generated method stub
		Log.i("tag", "pos" + percent);
	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub
		Log.i("tag", "error");
	}

	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub
		Log.i("tag", "oncompletion");
	}

	@Override
	public void onChange(OnlineSong song) {
		// TODO Auto-generated method stub
		Log.i("tag", "onchange" + song.getArtistName());
		if (song != null) {
			changeMiniPlayer(song);
		}
	}

	@Override
	public void onAutoDownload(File file) {
		// TODO Auto-generated method stub

	}

	private void changeMiniPlayer(OnlineSong song) {

		imageLoader.displayImage(song.getImageUrl(60), miniIcon, options);
		minititleView.setText(song.getSongName());
		minisingerView.setText(song.getArtistName());
		if (adapter != null) {
			for (int i = 0; i < MusicUntils.mOnlineSongs.size(); i++) {
				if (song.equals(MusicUntils.mOnlineSongs.get(i))) {
					Log.i("SYM", "POS=" + i);
					adapter.setTag(i);
					adapter.notifyDataSetChanged();
					popMusicListView.setSelection(i);
					break;
				}
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();

			
			stopService(new Intent(MainActivity.this,PlayService.class));
		}
	}

	private boolean isPlay = false;

	private void changePopWindowList() {
		OnlineSong song = mPlayService.getCurrSong();
		if (song != null) {
			for (int i = 0; i < MusicUntils.mOnlineSongs.size(); i++) {
				if (song.equals(MusicUntils.mOnlineSongs.get(i))) {
					Log.i("SYM", "POS=" + i);
					if (adapter != null) {
						adapter.setTag(i);
						adapter.notifyDataSetChanged();
						popMusicListView.setSelection(i);
					}
					break;
				}
			}
		}
	}

	@Override
	public void onPlay(OnlineSong song) {
		// TODO Auto-generated method stub
		Log.i("mmm", "zheshi" + song.getSongName());
		miniplayButton.setBackgroundResource(R.drawable.player_btnpause);
		isPlay = true;
	}

	@Override
	public void bindService() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMusicPause() {
		// TODO Auto-generated method stub
		miniplayButton.setBackgroundResource(R.drawable.player_btnplay);
		isPlay = false;

	}

	@Override
	public void onplayReceive() {
		// TODO Auto-generated method stub
		if (mPlayService.getCurrSong() != null) {
			changeMiniPlayer(mPlayService.getCurrSong());
			miniplayButton.setBackgroundResource(R.drawable.player_btnpause);
			// changePopWindowList();
			isPlay = true;
		}
	}
}
