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
import com.ssc.ttmusic.fragment.OnlineFragment;
import com.ssc.ttmusic.untils.MusicUntils;
import com.xiami.sdk.entities.OnlineRadio;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.QueryInfo;
import com.xiami.sdk.entities.RadioCategoryNew;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class RadioActivity extends Activity {

	private static final int ERROR = 0;
	private static final int RADIOCATEGORY = 1;
	private static final int RADIO = 2;
	private static int PAGEINDEX = 1;
	private static int PAGESIZE = 100;
	private LinearLayout categoryLayout;
	private ListView listView;
	private Button arrowButton;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private Button searchButton;
	List<RadioCategoryNew> rCategoryNews = new ArrayList<RadioCategoryNew>();
	ExecutorService executorService = Executors.newFixedThreadPool(5);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radio);
		initOption();
		initUI();
		executorService.submit(runnable);
	}

	private void initOption() {
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.defaulticon)
				.showImageOnFail(R.drawable.defaulticon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		imageLoader = ImageLoader.getInstance();

	}

	private void initUI() {
		// TODO Auto-generated method stub
		categoryLayout = (LinearLayout) findViewById(R.id.radio_sort);
		listView = (ListView) findViewById(R.id.radio_listview);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		searchButton = (Button) findViewById(R.id.search_btn);
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		arrowButton
				.setText(getResources().getString(R.string.online_radiotext));
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
				startActivity(new Intent(RadioActivity.this,
						SearchActivity.class));
			}
		});
	}

	public void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ERROR:
				showMsg(getResources().getString(R.string.error_showtoast));
				break;

			case RADIOCATEGORY:

				rCategoryNews.clear();
				rCategoryNews = (List<RadioCategoryNew>) msg.obj;
				if (rCategoryNews != null) {
					addRadioCategorysView(rCategoryNews, 0);
					getRadios(rCategoryNews.get(0));
				}
				break;

			case RADIO:
				final List<OnlineRadio> radios = (List<OnlineRadio>) msg.obj;
				if (radios != null) {
					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

					for (OnlineRadio radio : radios) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("logo", radio.getRadioLogo());
						map.put("name", radio.getRadioName());
						map.put("hot", "热度:" + radio.getPlayCount());
						list.add(map);
					}
					SimpleAdapter adapter = new SimpleAdapter(
							RadioActivity.this, list, R.layout.radioitem,
							new String[] { "logo", "name", "hot" }, new int[] {
									R.id.radio_logo, R.id.radio_title,
									R.id.radio_hot }) {

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
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, final int position, long id) {
							// TODO Auto-generated method stub
							executorService.submit(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									List<OnlineSong> songs = App.mSdk
											.fetchRadioDetailSync(
													radios.get(position), 100);
									MusicUntils.mOnlineSongs.addAll(0, songs);
									Intent intent = new Intent(
											OnlineFragment.ACTION);
									RadioActivity.this.sendBroadcast(intent);
								}
							});
						}
					});
				}
				break;
			default:
				break;
			}
		}

	};

	private void addRadioCategorysView(List<RadioCategoryNew> category, int n) {

		int i = 0;
		categoryLayout.removeAllViews();
		for (RadioCategoryNew radioCategoryNew : category) {
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
			button.setText(radioCategoryNew.getCategoryName());
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
			RadioCategoryNew rNew = rCategoryNews.get(pos);
			addRadioCategorysView(rCategoryNews, pos);
			getRadios(rNew);
		}
	};

	private void getRadios(final RadioCategoryNew rNew) {
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Pair<QueryInfo, List<OnlineRadio>> pair = App.mSdk
							.fetchRadioListSync(rNew, PAGESIZE, PAGEINDEX);
					List<OnlineRadio> radios = pair.second;
					Message msg = new Message();
					msg.what = RADIO;
					msg.obj = radios;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(ERROR);
				}

			}
		});
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				List<RadioCategoryNew> rCategoryNews = App.mSdk
						.fetchRadioCategorySync();
				Message msg = new Message();
				msg.what = RADIOCATEGORY;
				msg.obj = rCategoryNews;
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				handler.sendEmptyMessage(ERROR);
			}

		}
	};
}
