package com.ssc.ttmusic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ssc.ttmusic.application.App;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.RankList;
import com.xiami.sdk.entities.RankListItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RankActivity extends Activity implements OnClickListener {

	private PullToRefreshListView mPullRefreshListView;
	private Button arrowButton, searchButton;
	private static final int RANKLIST = 1;
	private static final int ERROR = 0;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	ProgressBar pBar;
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	private List<RankListItem> allItems = new ArrayList<RankListItem>();

	public static final String RANKEXTRA = "rankextra";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank);
		initView();
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

	@SuppressLint("HandlerLeak") Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case RANKLIST:
				RankAdapter adapter = new RankAdapter(allItems);
				adapter.notifyDataSetChanged();
				mPullRefreshListView.setAdapter(adapter);

				break;

			case ERROR:
				showMsg(getResources().getString(R.string.error_showtoast));
				break;
			default:
				break;
			}
			mPullRefreshListView.onRefreshComplete();

		}
	};

	class RankAdapter extends BaseAdapter {
		private List<RankListItem> items;
		private Context context;

		public RankAdapter(List<RankListItem> items) {
			this.context = RankActivity.this;
			this.items = items;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
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
						R.layout.rankitem, null);
				viewHolder.logoImageView = (ImageView) convertView
						.findViewById(R.id.rank_logo);
				viewHolder.titleTextView = (TextView) convertView
						.findViewById(R.id.rank_title);
				viewHolder.rank1TextView = (TextView) convertView
						.findViewById(R.id.rankname1);
				viewHolder.rank2TextView = (TextView) convertView
						.findViewById(R.id.rankname2);
				viewHolder.rank3TextView = (TextView) convertView
						.findViewById(R.id.rankname3);
				viewHolder.rank4TextView = (TextView) convertView
						.findViewById(R.id.rankname4);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			RankListItem rItem = items.get(position);
			String logoUri = rItem.getLogoMiddle();
			viewHolder.logoImageView.setTag(logoUri);
			imageLoader
					.displayImage(logoUri, viewHolder.logoImageView, options);
			viewHolder.titleTextView.setText(rItem.getTitle());

			List<OnlineSong> list = rItem.getSongs();
			Log.i("tag", list.size() + "");
			if (list != null && list.size() > 0) {
				switch (list.size()) {
				case 1:
					viewHolder.rank1TextView.setText("1." + list.get(0).getSongName());
					break;

				case 2:
					viewHolder.rank1TextView.setText("1." + list.get(0).getSongName());
					viewHolder.rank2TextView.setText("2." + list.get(1).getSongName());
					break;
				case 3:
					viewHolder.rank1TextView.setText("1." + list.get(0).getSongName());
					viewHolder.rank2TextView.setText("2." + list.get(1).getSongName());
					viewHolder.rank3TextView.setText("3." + list.get(2).getSongName());
					break;					
				default:
					viewHolder.rank1TextView.setText("1." + list.get(0).getSongName());
					viewHolder.rank2TextView.setText("2." + list.get(1).getSongName());
					viewHolder.rank3TextView.setText("3." + list.get(2).getSongName());
					viewHolder.rank4TextView.setText("4." + list.get(3).getSongName());
					break;
				}
			}

			return convertView;
		}

		class ViewHolder {
			ImageView logoImageView;
			TextView titleTextView;
			TextView rank1TextView;
			TextView rank2TextView;
			TextView rank3TextView;
			TextView rank4TextView;
		}
	}

	public void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void initView() {
		// TODO Auto-generated method stub
		arrowButton = (Button) findViewById(R.id.arrow_btn);
		searchButton = (Button) findViewById(R.id.search_btn);
		pBar = (ProgressBar) findViewById(R.id.pb_bar);
		arrowButton.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setOnRefreshListener(refreshListener);
		mPullRefreshListView.setOnItemClickListener(itemClickListener);
		executorService.submit(runnable);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			RankListItem item = allItems.get(position - 1);
			Intent mIntent = new Intent(RankActivity.this, SongsActivity.class);
			mIntent.putExtra(RANKEXTRA, item);
			startActivity(mIntent);
		}
	};
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				allItems.clear();
				List<RankList> ranks = App.mSdk.getRankListsSync();

				for (RankList rankList : ranks) {
					List<RankListItem> listItems = rankList.getItems();
					for (RankListItem item : listItems) {

						allItems.add(item);
					}
				}
				Message msg = new Message();
				msg.what = RANKLIST;
				msg.obj = allItems;
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				handler.sendEmptyMessage(ERROR);
			}

		}
	};
	OnRefreshListener<ListView> refreshListener = new OnRefreshListener<ListView>() {

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub

			String label = DateUtils.formatDateTime(getApplicationContext(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);

			// Update the LastUpdatedLabel
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

			// 刷新列表

			executorService.submit(runnable);
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
			startActivity(new Intent(RankActivity.this, SearchActivity.class));
		default:
			break;
		}
	}
}
