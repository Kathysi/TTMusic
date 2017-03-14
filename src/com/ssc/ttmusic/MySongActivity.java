package com.ssc.ttmusic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.bean.LoveSong;
import com.ssc.ttmusic.fragment.OnlineFragment;
import com.ssc.ttmusic.tools.MusicOprate;
import com.ssc.ttmusic.untils.MusicUntils;
import com.xiami.sdk.entities.OnlineSong;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MySongActivity extends Activity {
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ListView songListView;
	private Button arrowButton, searchButton;
	private MusicOprate musicOprate;
	private ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs);
		init();
		initOption();
	}

	private void initOption() {
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.defaulticon)
				.showImageOnFail(R.drawable.defaulticon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		imageLoader = ImageLoader.getInstance();

	}

	private void init() {
		// TODO Auto-generated method stub
		musicOprate = new MusicOprate(this);
		songListView = (ListView) findViewById(R.id.songs_listview);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		arrowButton.setText(R.string.local_lovesongtext);

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
				startActivity(new Intent(MySongActivity.this,
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
		// titleLayout.setVisibility(View.GONE);
		TextView headTextView=(TextView)view.findViewById(R.id.head_titleview);
		headTextView.setText(R.string.clear_text);
		headTextView.setClickable(true);
		headTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				musicOprate.deleteAll();
				adapter.setclear();
			}
		});
		backLayout = (RelativeLayout) view.findViewById(R.id.header_layout);
		logoImageView = (ImageView) view.findViewById(R.id.head_iconimg);
		playallButton = (Button) view.findViewById(R.id.head_playbutton);
		// Bitmap bitmap=BitmapFactory.decodeResource(getResources(),
		// R.drawable.localbackimg);
		// logoImageView.setImageBitmap(bitmap);
		// backLayout.setBackgroundDrawable(new ShapeDrawable(
		// new PlayBgShape(bitmap,App.screenWidth, App.screenHeight/3)));

		backLayout.setBackgroundResource(R.drawable.localbackimg);
		// String uri = rankListItem.getLogo();
		// new IconTask().execute(uri);
		songListView.addHeaderView(view);
	}

	public void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	class SongAdapter extends BaseAdapter {
		private List<LoveSong> songs;
		private Context context;

		public SongAdapter(Context context, List<LoveSong> songs) {
			this.context = context;
			this.songs = songs;
		}

		public void setclear()
		{
			songs.clear();
			notifyDataSetChanged();
		}
		public void changeAdapter()
		{
			songs = musicOprate.searchAll();
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
			LoveSong song = songs.get(position);

			imageLoader.displayImage(song.getUrl(), viewHolder.logoImageView,
					options);
			viewHolder.titleTextView.setText(song.getName());

			viewHolder.artistTextView.setText(song.getSinger());
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
	class SongsTask extends AsyncTask<String, Integer, List<LoveSong>> {

		@Override
		protected List<LoveSong> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<LoveSong> list = null;
			list = musicOprate.searchAll();
			return list;
		}

		@Override
		protected void onPostExecute(final List<LoveSong> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == null) {
				showMsg(getResources().getString(R.string.no_showtoast));
			} else {
				adapter = new SongAdapter(MySongActivity.this,
						result);
				songListView.setAdapter(adapter);
				playallButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						executorService.submit(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {

									List<OnlineSong> list = new ArrayList<OnlineSong>();
									for (LoveSong loveSong : result) {

										OnlineSong lrcSong = App.mSdk
												.findSongByIdSync(
														loveSong.getSongid(),
														OnlineSong.Quality.H);
										list.add(lrcSong);

									}
									MusicUntils.mOnlineSongs.addAll(0, list);
									Intent intent = new Intent(
											OnlineFragment.ACTION);
									MySongActivity.this.sendBroadcast(intent);
								} catch (Exception e) {
									// TODO: handle exception
								}

							}
						});

					}
				});
				songListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						if (position == 0)

						{
							return;
						}
						LoveSong song = result.get(position - 1);
						final long songid = song.getSongid();
						executorService.submit(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									OnlineSong lrcSong = App.mSdk
											.findSongByIdSync(songid,
													OnlineSong.Quality.H);
									MusicUntils.mOnlineSongs.add(0, lrcSong);
									Intent intent = new Intent(
											OnlineFragment.ACTION);
									MySongActivity.this.sendBroadcast(intent);
								} catch (Exception e) {
									// TODO: handle exception
								}

							}
						});

					}
				});
				songListView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub

						if (position == 0)

						{
							return true;
						}
						LoveSong song = result.get(position - 1);
						long songid = song.getSongid();
						musicOprate.deleteBymId(songid);
						adapter.changeAdapter();
						return true;
					}
				});
			}
		}

	}
}
