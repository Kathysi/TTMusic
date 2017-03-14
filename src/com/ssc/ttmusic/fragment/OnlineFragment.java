package com.ssc.ttmusic.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.AlbumActivity;
import com.ssc.ttmusic.ArtistSongsActivity;
import com.ssc.ttmusic.MoreActivity;
import com.ssc.ttmusic.R;
import com.ssc.ttmusic.RadioActivity;
import com.ssc.ttmusic.RankActivity;
import com.ssc.ttmusic.SingerActivity;
import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.untils.MusicUntils;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.QueryInfo;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Pair;
import android.view.CollapsibleActionView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class OnlineFragment extends Fragment implements OnClickListener {
	private View view;
	private ViewPager viewPager;
	private Context context;
	/**
	 * 装点点的ImageView数组
	 */
	private ImageView[] tips;

	/**
	 * 装ImageView数组
	 */
	private List<ImageView> mImageViews = new ArrayList<ImageView>();

	/**
	 * 图片资源id
	 */
	private int[] imgIdArray;
	private LinearLayout mLayout;
	private int index = 0;
	Handler handler = new Handler();

	private Button rankButton, songButton, radioButton, singerButton;

	private LinearLayout likesongsLayout;
	private LinearLayout hotSingerLayout;
	private Button likesongsButton;
	private static int PAGEINDEX = 1;
	private static int PAGESIZE = 10;
	private static final int ERROR = 0;
	private static final int LIKESONG = 1;
	private static final int GEDAN = 2;
	private static final int HOTSINGER = 3;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private LayoutInflater inflater;
	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	private Button gedanMoreButton;
	private LinearLayout gedanLayout;
	private LinearLayout column1, column2, column3;

	public static final String MORECATEGORY = "morecategory";
	public static final int MORESONG = 1;
	public static final int MORECOLLECT = 2;
	public static String ACTION = "com.ssc.songaddaction";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context = getActivity();
		initOption();
		this.inflater = inflater;
		view = inflater.inflate(R.layout.fragment_online, null);
		initView();
		initPager();
		return view;
	}

	private void initOption() {
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.defaulticon)
				.showImageOnFail(R.drawable.defaulticon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		imageLoader = ImageLoader.getInstance();

	}

	private void initPager() {
		// TODO Auto-generated method stub
		for (int i = 0; i < imgIdArray.length; i++) {
			ImageView imageView = new ImageView(App.mContext);
			imageView.setImageResource(imgIdArray[i]);
			imageView.setScaleType(ScaleType.FIT_XY);
			mImageViews.add(imageView);
		}
		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				setImageBackground(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				viewPager.setCurrentItem(index);
				index++;
				if (index > viewPager.getChildCount()) {
					index = 0;
				}
				handler.postDelayed(this, 2000);
			}
		}, 2000);
	}

	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.img_page_indicator_selected);
			} else {
				tips[i].setBackgroundResource(R.drawable.img_page_indicator);
			}
		}
	}

	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(mImageViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			ImageView img = mImageViews.get(position);
			container.addView(img);
			return img;
		}

	}

	private void initView() {
		// TODO Auto-generated method stub
		mLayout = (LinearLayout) view.findViewById(R.id.viewGroup);
		hotSingerLayout = (LinearLayout) view
				.findViewById(R.id.hotsingerlayout);
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
		rankButton = (Button) view.findViewById(R.id.rankbutton);
		songButton = (Button) view.findViewById(R.id.songbutton);
		radioButton = (Button) view.findViewById(R.id.radiobutton);
		singerButton = (Button) view.findViewById(R.id.singerbutton);
		rankButton.setOnClickListener(this);
		songButton.setOnClickListener(this);
		radioButton.setOnClickListener(this);
		singerButton.setOnClickListener(this);
		imgIdArray = new int[] { R.drawable.banner1, R.drawable.banner2,
				R.drawable.banner3, R.drawable.banner4 };
		tips = new ImageView[imgIdArray.length];
		for (int i = 0; i < imgIdArray.length; i++) {
			ImageView imageView = new ImageView(App.mContext);
			LayoutParams params = new LayoutParams(15, 15);

			tips[i] = imageView;
			if (i == 0) {
				tips[i].setBackgroundResource(R.drawable.img_page_indicator_selected);
			} else {
				tips[i].setBackgroundResource(R.drawable.img_page_indicator);
			}
			params.leftMargin = 10;
			params.rightMargin = 10;
			tips[i].setLayoutParams(params);
			mLayout.addView(tips[i]);
		}

		likesongsLayout = (LinearLayout) view.findViewById(R.id.likesonglayout);
		likesongsButton = (Button) view.findViewById(R.id.likemorebutton);
		addLikeSongView();
		gedanMoreButton = (Button) view.findViewById(R.id.moregedanbutton);
		gedanLayout = (LinearLayout) view.findViewById(R.id.gedanlayout);
		column1 = (LinearLayout) view.findViewById(R.id.first_column);
		column2 = (LinearLayout) view.findViewById(R.id.second_column);
		column3 = (LinearLayout) view.findViewById(R.id.thrid_column);
		addHotSingerView();
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Pair<QueryInfo, List<OnlineCollect>> pair = App.mSdk
							.getNewCollectSync(9, PAGEINDEX);
					List<OnlineCollect> collects = pair.second;
					Message message = new Message();
					message.what = GEDAN;
					message.obj = collects;
					mHandler.sendMessage(message);
				} catch (Exception e) {
					// TODO: handle exception
					mHandler.sendEmptyMessage(ERROR);
				}

			}
		});
		gedanMoreButton.setOnClickListener(likeMoreClickListener);
		likesongsButton.setOnClickListener(likeMoreClickListener);
	}

	OnClickListener likeMoreClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			Intent intent = new Intent(context, MoreActivity.class);
			switch (v.getId()) {
			case R.id.likemorebutton:
				intent.putExtra(MORECATEGORY, MORESONG);

				break;

			case R.id.moregedanbutton:
				intent.putExtra(MORECATEGORY, MORECOLLECT);
				break;
			default:
				break;
			}
			startActivity(intent);
		}
	};

	private void addHotSingerView()
	{
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					List<OnlineArtist> hotArtists=App.mSdk.getHotArtistsSync(PAGESIZE);
					Message message = new Message();
					message.what = HOTSINGER;
					message.obj = hotArtists;
					mHandler.sendMessage(message);
				} catch (Exception e) {
					// TODO: handle exception
					mHandler.sendEmptyMessage(ERROR);
				}
			}
		});
	}
	private void addLikeSongView() {
		// TODO Auto-generated method stub
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Pair<QueryInfo, List<OnlineSong>> pair = App.mSdk
							.getRecommendSongsSync(PAGESIZE, PAGEINDEX);
					List<OnlineSong> songs = pair.second;
					Message message = new Message();
					message.what = LIKESONG;
					message.obj = songs;
					mHandler.sendMessage(message);
				} catch (Exception e) {
					// TODO: handle exception
					mHandler.sendEmptyMessage(ERROR);
				}

			}
		});
	}

	private void toastMsg(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ERROR:
				toastMsg(getResources().getString(R.string.error_showtoast));
				break;
			case LIKESONG:
				@SuppressWarnings("unchecked")
				List<OnlineSong> songs = (List<OnlineSong>) msg.obj;
				if (songs != null && songs.size() > 0) {
					addView(songs);
				}
				break;
			case GEDAN:
				@SuppressWarnings("unchecked")
				List<OnlineCollect> collects = (List<OnlineCollect>) msg.obj;
				if (collects != null && collects.size() > 0) {
					addGeDanView(collects);
				}
				break;
			case HOTSINGER:
				List<OnlineArtist> artists=(List<OnlineArtist>)msg.obj;
				if(artists!=null&&artists.size()>0)
				{
					addHotSingerView(artists);
				}
				break;
			default:
				break;
			}
		}

	};
	public static final String SONGID="songid";
	private void addHotSingerView(List<OnlineArtist> artists)
	{
		for(int i=0;i<artists.size();i++)
		{
			View itemView = inflater.inflate(R.layout.likesongitem, null);
			TextView name = (TextView) itemView.findViewById(R.id.like_name);
			TextView rank = (TextView) itemView.findViewById(R.id.like_rank);
			TextView singer = (TextView) itemView
					.findViewById(R.id.like_singer);
			ImageView icon = (ImageView) itemView.findViewById(R.id.like_icon);
			final OnlineArtist artist=artists.get(i);
			name.setText(artist.getName());
			rank.setText(i + ".");
			itemView.setClickable(true);
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					long artistId=artist.getId();
					Intent mIntent=new Intent(context,ArtistSongsActivity.class);
					mIntent.putExtra(SONGID, artistId);
					startActivity(mIntent);
				}
			});
			itemView.setBackgroundResource(R.drawable.buttonselector);
			singer.setText(artist.getEnglish_name());
			imageLoader.displayImage(artist.getImageUrl(80), icon, options);
			hotSingerLayout.addView(itemView);
			
		}
	}
	private void addGeDanView(List<OnlineCollect> collects) {
		for (int i = 0; i < collects.size(); i++) {
			View itemView = inflater.inflate(R.layout.gedanitem, null);
			ImageView icon = (ImageView) itemView.findViewById(R.id.gedan_logo);
			TextView title = (TextView) itemView.findViewById(R.id.gedan_title);
			final OnlineCollect collect = collects.get(i);
			itemView.setClickable(true);
			itemView.setBackgroundResource(R.drawable.buttonselector);
			imageLoader.displayImage(collect.getImageUrl(100), icon, options);
			title.setText(collect.getCollectName());
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.topMargin = 10;
			final long id = collect.getListId();
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					executorService.submit(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							OnlineCollect result = App.mSdk
									.getCollectDetailSync(id);
							List<OnlineSong> songs = result.getSongs();
							MusicUntils.mOnlineSongs.addAll(0, songs);
							Intent intent = new Intent(OnlineFragment.ACTION);
							context.sendBroadcast(intent);
						}
					});

				}
			});
			itemView.setLayoutParams(params);
			// gedanLayout.addView(itemView);
			// LayoutParams params = new LayoutParams(0,
			// LayoutParams.WRAP_CONTENT);
			// params.weight = 1;
			// itemView.setLayoutParams(params);
			if (i % 3 == 0) {
				column1.addView(itemView);
			}
			if (i % 3 == 1) {
				column2.addView(itemView);
			}
			if (i % 3 == 2) {
				column3.addView(itemView);
			}
		}
	}

	private void addView(List<OnlineSong> songs) {
		for (int i = 0; i < songs.size(); i++) {
			View itemView = inflater.inflate(R.layout.likesongitem, null);
			TextView name = (TextView) itemView.findViewById(R.id.like_name);
			TextView rank = (TextView) itemView.findViewById(R.id.like_rank);
			TextView singer = (TextView) itemView
					.findViewById(R.id.like_singer);
			ImageView icon = (ImageView) itemView.findViewById(R.id.like_icon);
			final OnlineSong song = songs.get(i);
			name.setText(song.getSongName());
			rank.setText(i + ".");
			itemView.setClickable(true);
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicUntils.mOnlineSongs.add(0, song);
					Intent intent = new Intent(OnlineFragment.ACTION);
					context.sendBroadcast(intent);

				}
			});
			itemView.setBackgroundResource(R.drawable.buttonselector);
			singer.setText(song.getSingers());
			imageLoader.displayImage(song.getImageUrl(80), icon, options);
			likesongsLayout.addView(itemView);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rankbutton:
			startActivity(new Intent(context, RankActivity.class));
			break;

		case R.id.songbutton:

			startActivity(new Intent(context, AlbumActivity.class));
			break;

		case R.id.radiobutton:
			startActivity(new Intent(context, RadioActivity.class));
			break;

		case R.id.singerbutton:
			startActivity(new Intent(context, SingerActivity.class));
			break;
		default:
			break;
		}
	}
}
