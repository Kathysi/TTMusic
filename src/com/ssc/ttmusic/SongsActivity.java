package com.ssc.ttmusic;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.fragment.OnlineFragment;
import com.ssc.ttmusic.untils.MusicUntils;
import com.ssc.ttmusic.view.PlayBgShape;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.RankListItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SongsActivity extends Activity {

	private RankListItem rankListItem;
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
		rankListItem = (RankListItem) getIntent().getSerializableExtra(
				RankActivity.RANKEXTRA);
		songListView = (ListView) findViewById(R.id.songs_listview);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		arrowButton.setText(rankListItem.getTitle());

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
				startActivity(new Intent(SongsActivity.this,
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
		backLayout = (RelativeLayout) view.findViewById(R.id.header_layout);
		logoImageView = (ImageView) view.findViewById(R.id.head_iconimg);
		playallButton = (Button) view.findViewById(R.id.head_playbutton);
		
		String uri = rankListItem.getLogo();
		new IconTask().execute(uri);
		songListView.addHeaderView(view);
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

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				logoImageView.setImageBitmap(result);
				backLayout.setBackgroundDrawable(new ShapeDrawable(
						new PlayBgShape(result,App.screenWidth, App.screenHeight/3)));

			}
		}

	}

	public void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	class SongAdapter extends BaseAdapter {
		private List<OnlineSong> songs;
		private Context context;

		public SongAdapter(Context context, List<OnlineSong> songs) {
			this.context = context;
			this.songs = songs;
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
			OnlineSong song = songs.get(position);

			imageLoader.displayImage(song.getImageUrl(60),
					viewHolder.logoImageView, options);
			viewHolder.titleTextView.setText(song.getSongName());

			viewHolder.artistTextView.setText(song.getArtistName());
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

	class SongsTask extends AsyncTask<String, Integer, List<OnlineSong>> {

		@Override
		protected List<OnlineSong> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<OnlineSong> songs = null;
			try {
				songs = App.mSdk.getRankSongsSync(rankListItem);
			} catch (Exception e) {
				// TODO: handle exception
			}

			return songs;
		}

		@Override
		protected void onPostExecute(final List<OnlineSong> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == null) {
				showMsg(getResources().getString(R.string.error_showtoast));
			} else {
				SongAdapter adapter = new SongAdapter(SongsActivity.this,
						result);
				songListView.setAdapter(adapter);
				playallButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MusicUntils.mOnlineSongs.addAll(0, result);
						Intent intent=new Intent(OnlineFragment.ACTION);
						SongsActivity.this.sendBroadcast(intent);
					}
				});
				songListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						final OnlineSong song=result.get(position-1);
						
						MusicUntils.mOnlineSongs.add(0, song);
						Intent intent=new Intent(OnlineFragment.ACTION);
						SongsActivity.this.sendBroadcast(intent);
					}
				});
			}
		}

	}

}
