package com.ssc.ttmusic.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xiami.sdk.XiamiSDK;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class App extends Application{

	public static Context mContext;
	public static int screenWidth;
	public static int screenHeight;
	public static XiamiSDK mSdk;
	public static final String XIAMI_KEY="825bdc1bf1ff6bc01cd6619403f1a072";
	public static final String XIAMI_SECRET="7ede04a287d0f92c366880ba515293fd";
	public static ExecutorService executorService=Executors.newFixedThreadPool(5);
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext=getApplicationContext();
		mSdk=new XiamiSDK(mContext, XIAMI_KEY, XIAMI_SECRET);
		WindowManager wm=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		screenWidth=metrics.widthPixels;
		screenHeight=metrics.heightPixels;
		initImageLoader(mContext);
	}
	public static void initImageLoader(Context context) {
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 1) 
				.denyCacheImageMultipleSizesInMemory() 
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.threadPoolSize(5)
				.build();
		ImageLoader.getInstance().init(config);

	}
}
