package com.ssc.ttmusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.application.App;
import com.xiami.sdk.entities.ArtistBook;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineRadio;
import com.xiami.sdk.entities.RadioCategoryNew;

import android.R.mipmap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Region;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class SingerActivity extends Activity implements OnItemClickListener{

	private LinearLayout categoryLayout;
	private Button arrowButton;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private Button searchButton;
	private ListView listView;
	private String[] regions;
	private static int PAGEINDEX = 1;
	private static int PAGESIZE = 200;
	public static final String SONGID="songid";
	ArtistRegion[] artistRegions = ArtistRegion.values();
	ExecutorService executorService = Executors.newFixedThreadPool(5);
	private static final int ERROR = 0;
	private static final int ARTIST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singer);
		initUI();
		initOption();
	}

	private void addSingerRegionView(String[] regions, int n) {

		int i = 0;
		categoryLayout.removeAllViews();
		for (String region : regions) {
			Button button = new Button(this);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			if (i == n) {
				button.setBackgroundResource(R.color.white);
				button.setTextColor(getResources().getColor(R.color.caoluse));
			} else {
				button.setTextColor(getResources().getColor(R.color.black));
				button.setBackgroundResource(R.color.Gainsboro);
			}
			params.topMargin = 20;
			params.bottomMargin = 20;
			button.setTag(i);
			button.setText(region);
			button.setTextSize(16);
			button.setSingleLine(true);
			button.setLayoutParams(params);
			button.setOnClickListener(buttonClickListener);
			categoryLayout.addView(button);
			i++;
		}
	}

	OnClickListener buttonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int pos = (Integer) v.getTag();
			addSingerRegionView(regions, pos);
			getSingers(pos);
		}
	};

	private void initUI() {
		// TODO Auto-generated method stub
		regions = getResources().getStringArray(R.array.artistregion);
		categoryLayout = (LinearLayout) findViewById(R.id.radio_sort);
		listView = (ListView) findViewById(R.id.radio_listview);
		listView.setOnItemClickListener(this);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		searchButton = (Button) findViewById(R.id.search_btn);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		arrowButton.setText(getResources()
				.getString(R.string.online_singertext));
		arrowButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SingerActivity.this,
						SearchActivity.class));
			}
		});
		addSingerRegionView(regions, 0);
		getSingers(0);
	}

	private void getSingers(int pos) {
		final ArtistRegion region = artistRegions[pos];
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ArtistBook artistBook = App.mSdk.fetchArtistBookSync(
							region, PAGESIZE, PAGEINDEX);
					List<OnlineArtist> artists = artistBook.getArtists();
					Message msg = new Message();
					msg.what = ARTIST;
					msg.obj = artists;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(ERROR);
				}

			}
		});
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ERROR:
				showMsg(getResources().getString(R.string.error_showtoast));
				break;
			case ARTIST:
				@SuppressWarnings("unchecked")
				List<OnlineArtist> artists = (List<OnlineArtist>) msg.obj;
				if (artists != null) {
					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

					for (OnlineArtist artist : artists) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("logo", artist.getImageUrl(80));
						map.put("name", artist.getName());
						map.put("id", ""+artist.getId());
						map.put("hot", "热度:" + artist.getCountLikes());
						list.add(map);
					}
					SimpleAdapter adapter = new SimpleAdapter(
							SingerActivity.this, list, R.layout.singeritem,
							new String[] { "logo", "name", "hot","id" }, new int[] {
									R.id.radio_logo, R.id.radio_title,
									R.id.radio_hot,R.id.radio_id }) {

						@Override
						public void setViewImage(ImageView v, String value) {
							// TODO Auto-generated method stub
							if (v.getId() == R.id.radio_logo) {
								imageLoader.displayImage(value, v, options);
							} else {
								super.setViewImage(v, value);
							}

						}

					};
					listView.setAdapter(adapter);
					adapter.notifyDataSetChanged();

					
				}
				break;
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

	public void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		TextView tv=(TextView)view.findViewById(R.id.radio_id);
		long artistId=Long.valueOf(tv.getText().toString());
		Intent mIntent=new Intent(SingerActivity.this,ArtistSongsActivity.class);
		mIntent.putExtra(SONGID, artistId);
		startActivity(mIntent);
	}
}
