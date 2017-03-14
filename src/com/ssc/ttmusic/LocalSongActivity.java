package com.ssc.ttmusic;

import java.util.List;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.bean.Music;
import com.ssc.ttmusic.untils.MusicUntils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LocalSongActivity extends Activity {
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ListView songListView;
	private Button arrowButton, searchButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs);
		init();
		initOption();
		registerReceiver();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(
				Intent.ACTION_MEDIA_SCANNER_STARTED);
		filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		filter.addDataScheme("file");
		registerReceiver(mScanSDCardReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mScanSDCardReceiver);
	}

	private BroadcastReceiver mScanSDCardReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
				if (adapter != null) {
					List<Music> list = MusicUntils.getLocalMusics();
					adapter.change(list);
				}
			}
		}
	};

	private void initOption() {
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.defaulticon)
				.showImageOnFail(R.drawable.defaulticon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		imageLoader = ImageLoader.getInstance();

	}

	private void init() {
		songListView = (ListView) findViewById(R.id.songs_listview);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		arrowButton.setText(R.string.local_localsongtext);

		arrowButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		searchButton = (Button) findViewById(R.id.search_btn);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LocalSongActivity.this,
						SearchActivity.class));
			}
		});
		new SongsTask().execute("");

		addHeader();
	}

	RelativeLayout backLayout;
	ImageView logoImageView;
	Button playallButton;

	private void addHeader() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(this).inflate(R.layout.header, null);
		RelativeLayout titleLayout = (RelativeLayout) view
				.findViewById(R.id.header_titlelayout);
		titleLayout.setVisibility(View.GONE);
		backLayout = (RelativeLayout) view.findViewById(R.id.header_layout);
		logoImageView = (ImageView) view.findViewById(R.id.head_iconimg);
		playallButton = (Button) view.findViewById(R.id.head_playbutton);
		backLayout.setBackgroundResource(R.drawable.localbackimg);
		songListView.addHeaderView(view);
	}

	public void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	class SongAdapter extends BaseAdapter {
		private List<Music> songs;
		private Context context;

		public SongAdapter(Context context, List<Music> songs) {
			this.context = context;
			this.songs = songs;
		}

		public void change(List<Music> songs) {
			this.songs = songs;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return songs.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.songitem, null);
				viewHolder.logoImageView = (ImageView) convertView
						.findViewById(R.id.songsitem_logoimageview);
				viewHolder.titleTextView = (TextView) convertView
						.findViewById(R.id.songsitem_songnametextview);
				viewHolder.artistTextView = (TextView) convertView
						.findViewById(R.id.songsitem_songartisttextview);
				viewHolder.rankTextView = (TextView) convertView
						.findViewById(R.id.songsitem_ranklistview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Music song = songs.get(position);

			imageLoader.displayImage(song.getUri(), viewHolder.logoImageView,
					options);
			viewHolder.titleTextView.setText(song.getTitle() == null ? "未知音乐"
					: song.getTitle());

			viewHolder.artistTextView
					.setText(song.getArtist() == null ? "未知艺术家" : song
							.getArtist());
			viewHolder.rankTextView.setText(position + ".");
			return convertView;
		}

		class ViewHolder {
			ImageView logoImageView;
			TextView titleTextView;
			TextView artistTextView;
			TextView rankTextView;
		}
	}

	SongAdapter adapter;

	class SongsTask extends AsyncTask<String, Integer, List<Music>> {

		@Override
		protected List<Music> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<Music> list = null;
			list = MusicUntils.getLocalMusics();
			return list;
		}

		@Override
		protected void onPostExecute(final List<Music> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == null) {
				showMsg(getResources().getString(R.string.no_showtoast));
			} else {
				adapter = new SongAdapter(LocalSongActivity.this, result);
				songListView.setAdapter(adapter);
				songListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

					}
				});
			}
		}

	}
}
