package com.ssc.ttmusic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.handmark.pulltorefresh.library.R.string;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.application.App;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.QueryInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AlbumActivity extends Activity {

	private static final int PAGESIZE = 20;
	private static int PAGEINDEX = 1;
	private boolean isMore = true;
	private static final int GETAll = 0x01;
	private static final int ERROR = 0x02;
	public static final String DETAIL="album";
	// title
	private Button arrowButton, searchButton;
	private ListView albumListView;
	private TextView recomTextView, hotTextView, newTextView;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private LinearLayout bottomLayout;
	private List<OnlineCollect> allCollects = new ArrayList<OnlineCollect>();

	private Button categoryButton;
	private Drawable rightDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		initOption();
		initView();
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
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		searchButton = (Button) findViewById(R.id.search_btn);

		albumListView = (ListView) findViewById(R.id.album_listview);
		recomTextView = (TextView) findViewById(R.id.album_recomtextview);
		hotTextView = (TextView) findViewById(R.id.album_hottextview);
		newTextView = (TextView) findViewById(R.id.album_newtextview);
		categoryButton = (Button) findViewById(R.id.album_allbutton);
		
		rightDrawable=categoryButton.getCompoundDrawables()[2];
		categoryButton.setOnClickListener(categoryClickListener);
		View view = getLayoutInflater().inflate(R.layout.bottom, null);
		bottomLayout = (LinearLayout) view.findViewById(R.id.bottom);
		arrowButton.setText("精选集");
		arrowButton.setOnClickListener(titleClickListener);
		searchButton.setOnClickListener(titleClickListener);

		initTagText(0);
		searchAlbums(0);
		recomTextView.setOnClickListener(tagClickListener);
		hotTextView.setOnClickListener(tagClickListener);
		newTextView.setOnClickListener(tagClickListener);

		albumListView.addFooterView(view);
		albumListView.setOnScrollListener(scrollListener);
		albumListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position!=allCollects.size()-1)
				{
				Intent intent=new Intent(AlbumActivity.this,CollectDetailActivity.class);
				intent.putExtra(DETAIL, allCollects.get(position));
				startActivity(intent);
				}
			}
		});
	}

	
	OnClickListener categoryClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (view.getLastVisiblePosition() == view.getCount() - 1) {
					if (isMore) {
						Log.i("xm", "加载下一页");
						bottomLayout.setVisibility(View.VISIBLE);
						searchAlbums(category);
					} else {
						showtoast("没有更多的了");
						bottomLayout.setVisibility(View.GONE);
					}
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub

		}
	};

	
	private synchronized void  searchAlbums(final int id) {
		App.executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					List<OnlineCollect> collects = null;
					switch (id) {
					case 0:
						Pair<QueryInfo, List<OnlineCollect>> pair0 = App.mSdk
								.getCollectsRecommendSync(PAGESIZE, PAGEINDEX);
						QueryInfo queryInfo0 = pair0.first;
						collects = pair0.second;
						if (queryInfo0.isMore()) {
							isMore = true;
						} else {
							isMore = false;
						}
						PAGEINDEX++;
						break;

					case 1:

						Pair<QueryInfo, List<OnlineCollect>> pair1 = App.mSdk
								.getNewCollectSync(PAGESIZE, PAGEINDEX);
						QueryInfo queryInfo1 = pair1.first;
						collects = pair1.second;
						if (queryInfo1.isMore()) {
							isMore = true;
						} else {
							isMore = false;
						}
						PAGEINDEX++;
						break;
					case 2:

						Pair<QueryInfo, List<OnlineCollect>> pair2 = App.mSdk
								.getNewCollectSync(PAGESIZE, PAGEINDEX);
						QueryInfo queryInfo2 = pair2.first;
						collects = pair2.second;
						if (queryInfo2.isMore()) {
							isMore = true;
						} else {
							isMore = false;
						}
						PAGEINDEX++;
						break;
					default:
						break;
					}
					Message msg = new Message();
					msg.obj = collects;
					msg.what = GETAll;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(ERROR);
				}
			}
		});
	}

	private void showtoast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case GETAll:
				@SuppressWarnings("unchecked")
				List<OnlineCollect> collects = (List<OnlineCollect>) msg.obj;
				if (collects != null && collects.size() > 0) {
					allCollects.addAll(collects);

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					for (OnlineCollect collect : allCollects) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("title", collect.getCollectName());
						map.put("img", collect.getImageUrl(100));
						map.put("count", "" + collect.getPlayCount());
						map.put("description", collect.getDescription());
						SimpleDateFormat df = new SimpleDateFormat(
								"MM-dd HH:mm:ss", Locale.CHINESE);
						map.put("tag",
								df.format(new Date(collect.getCreateTime())));
						map.put("user", collect.getUserName());
						list.add(map);
					}
					MySimpleAdapter adapter = new MySimpleAdapter(
							AlbumActivity.this, list, R.layout.album_item,
							new String[] { "title", "img", "count",
									"description", "tag", "user" }, new int[] {
									R.id.albumitem_title, R.id.albumitem_icon,
									R.id.albumitem_playcount,
									R.id.albumitem_detailtextview,
									R.id.albumitem_categorytextview,
									R.id.albumitem_authortextview });
					int pos = albumListView.getFirstVisiblePosition();
					View v = albumListView.getChildAt(0);
					int y = (v == null ? 0 : v.getTop());
					albumListView.setAdapter(adapter);
					albumListView.setSelectionFromTop(pos, y);

				}

				break;

			case ERROR:
				showtoast("加载错误");
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
			if (v.getId() == R.id.albumitem_icon) {
				imageLoader.displayImage(value, v, options);
			} else {
				super.setViewImage(v, value);
			}
		}

	}

	private void initTagText(int id) {
		recomTextView.setTextColor(getResources().getColor(R.color.black));
		hotTextView.setTextColor(getResources().getColor(R.color.black));
		newTextView.setTextColor(getResources().getColor(R.color.black));
		switch (id) {
		case 0:
			//searchAlbums(0);
			recomTextView
					.setTextColor(getResources().getColor(R.color.caoluse));
			break;

		case 1:
			//searchAlbums(1);
			hotTextView.setTextColor(getResources().getColor(R.color.caoluse));
			break;
		case 2:
			//searchAlbums(2);
			newTextView.setTextColor(getResources().getColor(R.color.caoluse));
			break;
		default:
			break;
		}
		allCollects.clear();
		isMore = true;
		PAGEINDEX = 1;
		bottomLayout.setVisibility(View.VISIBLE);
	}

	private int category = 0;
	OnClickListener tagClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.album_recomtextview:
				initTagText(0);
				searchAlbums(0);
				category = 0;
				break;

			case R.id.album_hottextview:
				initTagText(1);
				searchAlbums(1);
				category = 1;
				break;

			case R.id.album_newtextview:
				initTagText(2);
				searchAlbums(2);
				category = 2;
				break;
			default:
				break;
			}
		}
	};
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
				startActivity(new Intent(AlbumActivity.this,
						SearchActivity.class));
			default:
				break;
			}
		}
	};
}
