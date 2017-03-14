package com.ssc.ttmusic.untils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.bean.Music;
import com.xiami.sdk.entities.OnlineSong;

public class MusicUntils {

	// 存放本地歌曲列表
	public static ArrayList<Music> sMusicList = new ArrayList<Music>();

	public static ArrayList<OnlineSong> mOnlineSongs=new ArrayList<OnlineSong>();
	public static void initMusics() {
		mOnlineSongs.clear();
		sMusicList.clear();
		sMusicList.addAll(LocalMusicUntils.queryMusic(getBaseDir()));
	}

	public static List<Music> getLocalMusics()
	{
		return LocalMusicUntils.queryMusic(getBaseDir());
	}
	public static String getBaseDir() {

		String dir = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			dir = Environment.getExternalStorageDirectory() + File.separator;
		} else {
			dir = App.mContext.getFilesDir() + File.separator;

		}
		return dir;
	}

	public static String getAppLocalDir() {
		String dir = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			dir = Environment.getExternalStorageDirectory() + File.separator
					+ "ttmusic" + File.separator;
		} else {
			dir = App.mContext.getFilesDir() + File.separator + "ttmusic"
					+ File.separator;
		}
		return mkDir(dir);
	}

	public static String getMusicDir() {
		String musicDir = getAppLocalDir() + "music" + File.separator;
		return mkDir(musicDir);
	}

	/**
	 * 获取歌词存放目录
	 * 
	 * @return
	 */
	public static String getLrcDir() {
		String lrcDir = getAppLocalDir() + "lrc" + File.separator;
		return mkDir(lrcDir);
	}

	public static String mkDir(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}
}
