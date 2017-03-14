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
import com.ssc.ttmusic.SongsActivity.IconTask;
import com.ssc.ttmusic.SongsActivity.SongAdapter.ViewHolder;
import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.fragment.OnlineFragment;
import com.ssc.ttmusic.untils.MusicUntils;
import com.ssc.ttmusic.view.PlayBgShape;
import com.xiami.music.model.Collect;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CollectDetailActivity extends Activity implements OnClickListener{

	private OnlineCollect collect;
	private ListView listView;
	private Button arrowButton, searchButton;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	RelativeLayout backLayout;
	ImageView logoImageView;
	Button playallButton;
	TextView textview;
	private RelativeLayout titleLayout;
	
	private RelativeLayout headLayout;
	private TextView headView;
	private Button headButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailalbum);
		collect = (OnlineCollect) getIntent().getSerializableExtra(
				AlbumActivity.DETAIL);
		initOption();
		initView();
		initData();

	}

	private void initOption() {
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.defaulticon)
				.showImageOnFail(R.drawable.defaulticon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		imageLoader = ImageLoader.getInstance();

	}

	private void initView() {
		// TODO Auto-generated method stub
		listView = (ListView) findViewById(R.id.album_listview);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		searchButton = (Button) findViewById(R.id.search_btn);
		headLayout=(RelativeLayout)findViewById(R.id.album_titlelayout);
		headView=(TextView)findViewById(R.id.album_titleview);
		headButton=(Button)findViewById(R.id.album_playbutton);
		headButton.setOnClickListener(this);
		titleLayout = (RelativeLayout) findViewById(R.id.title_relayout);
		titleLayout.getBackground().setAlpha(0);
		arrowButton.setOnClickListener(titleClickListener);
		searchButton.setOnClickListener(titleClickListener);
		addHeader();
		listView.setOnScrollListener(scrollListener);
	}

	OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(AbsListView listview, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			
			if (firstVisibleItem == 0) {
				
				View view = listview.getChildAt(0);

				if (view != null) {
					int top = -view.getTop();
					int height = view.getHeight();
					if(top+1<(-titleLayout.getHeight()+height))
					{
						headLayout.setVisibility(View.GONE);
					}
					else {
						headLayout.setVisibility(View.VISIBLE);
					}
					if ((top+1+titleLayout.getHeight() )< height && top >= 0) {
						float f = (float)( top +1+titleLayout.getHeight())/ (float) height;
						Log.i("xm", " alpha:" + (int) f * 255);
						titleLayout.getBackground().setAlpha((int) (f * 255));
						titleLayout.invalidate();
					}
				}
			} else if (firstVisibleItem > 0) {
				headLayout.setVisibility(View.VISIBLE);
				titleLayout.getBackground().setAlpha(255);
				titleLayout.invalidate();
			}
		}
	};
	  public static int dip2px(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	    } 
	public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
	private void addHeader() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(this).inflate(R.layout.collectheader,
				null);
		backLayout = (RelativeLayout) view.findViewById(R.id.header_layout);
		logoImageView = (ImageView) view.findViewById(R.id.head_iconimg);
		playallButton = (Button) view.findViewById(R.id.head_playbutton);

		playallButton.setOnClickListener(this);
		textview = (TextView) view.findViewById(R.id.head_titleview);
		textview.setText(collect.getPlayCount() + "次试听" + "  "
				+ collect.getSongCount() + "首歌曲");
		headView.setText(collect.getPlayCount() + "次试听" + "  "
				+ collect.getSongCount() + "首歌曲");
		imageLoader.displayImage(collect.getImageUrl(100), logoImageView,
				options);

		new IconTask().execute(collect.getImageUrl(500));
		listView.addHeaderView(view);
	}

	
	class IconTask extends AsyncTask<String, Integer, ShapeDrawable> {

		@Override
		protected ShapeDrawable doInBackground(String... params) {
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
					ShapeDrawable shapeDrawable=new ShapeDrawable(
							new PlayBgShape(bitmap, App.screenWidth,
									App.screenHeight / 2));
					return shapeDrawable;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(ShapeDrawable result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				backLayout.setBackgroundDrawable(result);

			}
		}

	}

	// 主界面标题
	OnClickListener titleClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.arrow_btn:
				finish();

				break;

			case R.id.search_btn:
				startActivity(new Intent(CollectDetailActivity.this,
						SearchActivity.class));
			default:
				break;
			}
		}
	};

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (songs != null && songs.size() > 0) {
				SongAdapter adapter = new SongAdapter(
						CollectDetailActivity.this, songs);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						final OnlineSong song = songs.get(position - 1);

						MusicUntils.mOnlineSongs.add(0, song);
						Intent intent = new Intent(OnlineFragment.ACTION);
						CollectDetailActivity.this.sendBroadcast(intent);
					}
				});
			}
		}

	};

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

	private List<OnlineSong> songs;

	private void initData() {
		// TODO Auto-generated method stub

		arrowButton.setText("精选集");

		App.executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					long id = collect.getListId();
					OnlineCollect detailCollect = App.mSdk
							.getCollectDetailSync(id);
					songs = detailCollect.getSongs();
				} catch (Exception e) {
					// TODO: handle exception
				}

				handler.sendEmptyMessage(0);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		MusicUntils.mOnlineSongs.addAll(0, songs);
		Intent intent=new Intent(OnlineFragment.ACTION);
		CollectDetailActivity.this.sendBroadcast(intent);
	}

}
