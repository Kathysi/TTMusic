package com.ssc.ttmusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.fragment.OnlineFragment;
import com.ssc.ttmusic.untils.MusicUntils;
import com.ssc.ttmusic.view.Indicator;
import com.xiami.sdk.callback.OnlineSongsCallback;
import com.xiami.sdk.entities.HotWord;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.QueryInfo;
import com.xiami.sdk.entities.SearchSummaryResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class SearchActivity extends Activity implements OnClickListener {

	private Button arrowButton;
	private Button searchButton;
	private EditText editText;
	private static final int HOTWORDS = 1;
	private static final int ERROR = 0;
	private static final int SEARCHRESULT = 2;
	// 定义TextView的Padding属性
	private static final int PADING_TOP_BOTTOM = 10;
	private static final int PADING_LEFT_RIGHT = 10;

	// 定义TextView的Margin属性
	private static final int MARGIN_LEFT_RIGHT = 10;
	private static final int MARGIN_TOP_BOTTOM = 10;
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	Drawable drawableUp;
	Drawable drawableDown;
	private LinearLayout hotWordsLayout;

	private LinearLayout resultLayout;

	// pager
	private ViewPager mPager;
	private Indicator mIndicator;
	private TextView tv1, tv2, tv3, tv4;
	private List<View> views = new ArrayList<View>();

	private ListView lv1, lv2, lv3, lv4;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private static int PAGEINDEX = 1;
	private static int PAGESIZE = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initView();
	}

	private void searchByWords(final String words) {
		// 通过关键字搜索
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					SearchSummaryResult results = App.mSdk.searchSummarySync(
							words, 100);

					Message msg = new Message();
					msg.what = SEARCHRESULT;
					msg.obj = results;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(ERROR);
				}

			}
		});

	}

	private void initPager() {
		initOption();
		LayoutInflater inflater = LayoutInflater.from(this);
		mPager = (ViewPager) findViewById(R.id.search_pager);
		mIndicator = (Indicator) findViewById(R.id.search_indicator);
		tv1 = (TextView) findViewById(R.id.search_songs);
		tv2 = (TextView) findViewById(R.id.search_albums);
		tv3 = (TextView) findViewById(R.id.search_collect);
		tv4 = (TextView) findViewById(R.id.search_singers);
		View view1 = inflater.inflate(R.layout.searchresult, null);
		View view2 = inflater.inflate(R.layout.searchresult, null);
		View view3 = inflater.inflate(R.layout.searchresult, null);
		View view4 = inflater.inflate(R.layout.searchresult, null);
		lv1 = (ListView) view1.findViewById(R.id.search_listview);
		lv2 = (ListView) view2.findViewById(R.id.search_listview);
		lv3 = (ListView) view3.findViewById(R.id.search_listview);
		lv4 = (ListView) view4.findViewById(R.id.search_listview);
		tv1.setOnClickListener(lvClickListener);
		tv2.setOnClickListener(lvClickListener);
		tv3.setOnClickListener(lvClickListener);
		tv4.setOnClickListener(lvClickListener);
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		mPager.setAdapter(new ResultPagerAdapter(views));
		mPager.setOnPageChangeListener(pageChangeListener);
		settitleColor(0);

	}

	OnClickListener lvClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.search_songs:
				mPager.setCurrentItem(0);
				break;
			case R.id.search_albums:
				mPager.setCurrentItem(1);
				break;
			case R.id.search_collect:
				mPager.setCurrentItem(2);
				break;
			case R.id.search_singers:
				mPager.setCurrentItem(3);
				break;

			default:
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

	private void init() {
		tv1.setTextColor(getResources().getColor(R.color.gray));
		tv2.setTextColor(getResources().getColor(R.color.gray));
		tv3.setTextColor(getResources().getColor(R.color.gray));
		tv4.setTextColor(getResources().getColor(R.color.gray));
	}

	private void settitleColor(int pos) {
		init();
		switch (pos) {
		case 0:

			tv1.setTextColor(getResources().getColor(R.color.caoluse));
			break;

		case 1:
			tv2.setTextColor(getResources().getColor(R.color.caoluse));
			break;
		case 2:
			tv3.setTextColor(getResources().getColor(R.color.caoluse));
			break;
		case 3:
			tv4.setTextColor(getResources().getColor(R.color.caoluse));
			break;
		default:
			break;
		}
	}

	OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			mIndicator.scroll(arg0, arg1);
		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub

			settitleColor(arg0);
		}

	};

	class ResultPagerAdapter extends PagerAdapter {
		public List<View> lists;

		public ResultPagerAdapter(List<View> lists) {
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(lists.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			View view = lists.get(position);
			container.addView(view);
			return view;
		}

	}

	private void initView() {
		// TODO Auto-generated method stub
		drawableDown = getResources().getDrawable(R.drawable.search_icon_down);
		drawableUp = getResources().getDrawable(R.drawable.search_icon_up);
		// drawableDown.setBounds(5, 0, drawableDown.getMinimumWidth(),
		// drawableDown.getMinimumHeight());
		// drawableUp.setBounds(5, 0, drawableUp.getMinimumWidth(),
		// drawableUp.getMinimumHeight());
		editText = (EditText) findViewById(R.id.search_edittext);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		searchButton = (Button) findViewById(R.id.search_btn);
		hotWordsLayout = (LinearLayout) findViewById(R.id.hotword_layout);
		resultLayout = (LinearLayout) findViewById(R.id.result_layout);
		arrowButton.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		executorService.submit(runnable);
		initPager();
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				final String searchText = editText.getText().toString().trim();
				if (searchText.length() == 0) {
					hotWordsLayout.setVisibility(View.VISIBLE);
					resultLayout.setVisibility(View.GONE);
				} else {
					hotWordsLayout.setVisibility(View.GONE);
					resultLayout.setVisibility(View.VISIBLE);
					searchByWords(searchText);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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

			case HOTWORDS:
				if (msg.obj != null) {
					@SuppressWarnings("unchecked")
					List<HotWord> list = (List<HotWord>) msg.obj;

					initHotwordsLayout(list);
				}
				break;

			case SEARCHRESULT:
				SearchSummaryResult result = (SearchSummaryResult) msg.obj;
				if (result != null) {
					final List<OnlineSong> songs = result.getSongs();
					final List<OnlineAlbum> albums = result.getAlbums();
					final List<OnlineArtist> artists = result.getArtists();
					final List<OnlineCollect> collects = result.getCollects();
					if (songs != null && songs.size() > 0) {

						List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < songs.size(); i++) {
							OnlineSong song = songs.get(i);
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("rank", i + ".");
							map.put("title", song.getSongName());
							map.put("detail", song.getSingers());
							map.put("url", song.getImageUrl(80));
							list.add(map);
						}
						MySimpleAdapter adapter = new MySimpleAdapter(
								SearchActivity.this,
								list,
								R.layout.songitem,
								new String[] { "rank", "title", "detail", "url" },
								new int[] { R.id.songsitem_ranklistview,
										R.id.songsitem_songnametextview,
										R.id.songsitem_songartisttextview,
										R.id.songsitem_logoimageview });
						lv1.setAdapter(adapter);
						lv1.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								MusicUntils.mOnlineSongs.add(0,
										songs.get(position));
								Intent intent = new Intent(
										OnlineFragment.ACTION);
								SearchActivity.this.sendBroadcast(intent);
							}
						});

					}
					if (albums != null && albums.size() > 0) {
						List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < albums.size(); i++) {
							OnlineAlbum album = albums.get(i);
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("rank", i + ".");
							map.put("title", album.getAlbumName());
							map.put("detail", album.getArtistName());
							map.put("url", album.getImageUrl(80));
							list.add(map);
						}
						MySimpleAdapter adapter = new MySimpleAdapter(
								SearchActivity.this,
								list,
								R.layout.songitem,
								new String[] { "rank", "title", "detail", "url" },
								new int[] { R.id.songsitem_ranklistview,
										R.id.songsitem_songnametextview,
										R.id.songsitem_songartisttextview,
										R.id.songsitem_logoimageview });
						lv2.setAdapter(adapter);
						lv2.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								OnlineAlbum album = albums.get(position);
								final long albumid = album.getAlbumId();
								executorService.submit(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										OnlineAlbum result = App.mSdk
												.getAlbumsDetailSync(albumid);
										List<OnlineSong> songs = result
												.getSongs();
										MusicUntils.mOnlineSongs.addAll(0,
												songs);
										Intent intent = new Intent(
												OnlineFragment.ACTION);
										SearchActivity.this
												.sendBroadcast(intent);
									}
								});

							}
						});
					}
					if (collects != null && collects.size() > 0) {
						List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < collects.size(); i++) {
							OnlineCollect collect = collects.get(i);
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("rank", i + ".");
							map.put("title", collect.getCollectName());
							map.put("detail", collect.getUserName());
							map.put("url", collect.getImageUrl(80));
							list.add(map);
						}
						MySimpleAdapter adapter = new MySimpleAdapter(
								SearchActivity.this,
								list,
								R.layout.songitem,
								new String[] { "rank", "title", "detail", "url" },
								new int[] { R.id.songsitem_ranklistview,
										R.id.songsitem_songnametextview,
										R.id.songsitem_songartisttextview,
										R.id.songsitem_logoimageview });
						lv3.setAdapter(adapter);
						lv3.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								final OnlineCollect collect = collects
										.get(position);
								final long collectid = collect.getListId();
								executorService.submit(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										OnlineCollect result = App.mSdk
												.getCollectDetailSync(collectid);
										List<OnlineSong> songs = result
												.getSongs();
										MusicUntils.mOnlineSongs.addAll(0,
												songs);
										Intent intent = new Intent(
												OnlineFragment.ACTION);
										SearchActivity.this
												.sendBroadcast(intent);
									}
								});

							}
						});
					}
					if (artists != null && artists.size() > 0) {
						List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < artists.size(); i++) {
							OnlineArtist artist = artists.get(i);
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("rank", i + ".");
							map.put("title", artist.getName());
							map.put("detail", artist.getArea());
							map.put("url", artist.getImageUrl(80));
							list.add(map);
						}
						MySimpleAdapter adapter = new MySimpleAdapter(
								SearchActivity.this,
								list,
								R.layout.songitem,
								new String[] { "rank", "title", "detail", "url" },
								new int[] { R.id.songsitem_ranklistview,
										R.id.songsitem_songnametextview,
										R.id.songsitem_songartisttextview,
										R.id.songsitem_logoimageview });
						lv4.setAdapter(adapter);
						lv4.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								OnlineArtist artist = artists.get(position);
								final long artistid = artist.getId();
								executorService.submit(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Pair<QueryInfo, List<OnlineSong>> pair = App.mSdk
												.fetchSongsByArtistIdSync(
														artistid, 40, PAGEINDEX);
										List<OnlineSong> songs = pair.second;

										MusicUntils.mOnlineSongs.addAll(0,
												songs);
										Intent intent = new Intent(
												OnlineFragment.ACTION);
										SearchActivity.this
												.sendBroadcast(intent);
									}
								});

							}
						});
					}
				}
				break;
			default:
				break;
			}
		}

	};

	class MySimpleAdapter extends SimpleAdapter {

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void setViewImage(ImageView v, String value) {
			// TODO Auto-generated method stub
			// super.setViewImage(v, value);
			if (v.getId() == R.id.songsitem_logoimageview) {
				imageLoader.displayImage(value, v, options);
			} else {
				super.setViewImage(v, value);
			}
		}

	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<HotWord> words = null;
			try {
				words = App.mSdk.fetchHotWordsSync();
			} catch (Exception e) {
				// TODO: handle exception
			}

			Message msg = new Message();
			msg.what = HOTWORDS;
			msg.obj = words;
			handler.sendMessage(msg);

		}
	};

	private void initHotwordsLayout(List<HotWord> words) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		int displayWidth = getWindowManager().getDefaultDisplay().getWidth();
		float rowWidth = 0f;
		LinearLayout rowLayout = null;
		for (int i = 0; i < words.size(); i++) {
			String text = words.get(i).query;
			int change = words.get(i).change;
			Button button;
			Log.i("TAG", "" + change);
			if (change >= 0) {
				button = createButton(text, true);
			} else {
				button = createButton(text, false);
			}
			float textWidth = button.getPaint().measureText(text)
					+ PADING_LEFT_RIGHT * 2 + MARGIN_LEFT_RIGHT * 2;
			rowWidth += textWidth;
			if (rowLayout == null || rowWidth > displayWidth) {
				rowWidth = textWidth;
				rowLayout = getHorizontalLinearLayout(params);
				hotWordsLayout.addView(rowLayout);
			}
			rowLayout.addView(button);
		}
	}

	private LinearLayout getHorizontalLinearLayout(LayoutParams lp) {
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(lp);
		layout.setGravity(Gravity.CENTER);
		return layout;
	}

	private Button createButton(String word, boolean isUp) {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.leftMargin = MARGIN_LEFT_RIGHT;
		params.rightMargin = MARGIN_LEFT_RIGHT;
		params.topMargin = MARGIN_TOP_BOTTOM;
		params.bottomMargin = MARGIN_TOP_BOTTOM;
		Button button = new Button(this);
		button.setText(word);
		button.setTextColor(getResources().getColor(android.R.color.black));
		if (isUp) {
			button.setCompoundDrawables(null, null, drawableUp, null);
		} else {
			button.setCompoundDrawables(null, null, drawableDown, null);
		}
		button.setTextSize(16);
		button.setLayoutParams(params);
		button.setGravity(Gravity.CENTER_VERTICAL);
		button.setPadding(PADING_LEFT_RIGHT, PADING_TOP_BOTTOM,
				PADING_LEFT_RIGHT, PADING_TOP_BOTTOM);
		button.setBackgroundResource(R.color.bantouming);

		button.setTag(word);
		button.setOnClickListener(wordClickListener);
		return button;
	}

	OnClickListener wordClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String word = (String) v.getTag();
			editText.setText(word);
			editText.setSelection(word.length());
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.arrow_btn:
			finish();
			break;

		case R.id.search_btn:

			break;

		default:
			break;
		}
	}

}
