package com.ssc.ttmusic.untils;

import java.util.ArrayList;

import com.ssc.ttmusic.application.App;
import com.ssc.ttmusic.bean.Music;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;

public class LocalMusicUntils {
	// 根据id获取歌曲的uri
	public static String queryMusicbyId(int musicId) {

		String result = null;
		Cursor cursor = App.mContext.getContentResolver().query(
				Media.EXTERNAL_CONTENT_URI, new String[] { Media.DATA },
				Media._ID + "=?", new String[] { String.valueOf(musicId) },
				null);
		while (cursor.moveToNext()) {
			result = cursor.getString(cursor.getColumnIndex(Media.DATA));
			break;
		}
		cursor.close();
		return result;

	}

	// 获取cp中的所有歌曲
	public static ArrayList<Music> queryMusic(String dirName) {
		ArrayList<Music> results = new ArrayList<Music>();
		Cursor cursor = App.mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				MediaStore.Audio.Media.DATA + " like ?",
				new String[] { dirName + "%" },
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor == null)
			return results;

		// id title singer data time image
		Music music;
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			// 如果不是音乐
			String isMusic = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
			if (isMusic != null && isMusic.equals(""))
				continue;

			String title = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
			String artist = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

//			if (isRepeatMusic(title, artist))
//				continue;

			music = new Music();
			music.setId(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
			music.setTitle(title);
			music.setArtist(artist);
			music.setUri(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			music.setLength(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
			music.setImage(getAlbumImageUri(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));
			results.add(music);
		}

		cursor.close();
		return results;
	}

	// 根据歌曲名字和歌手判断是否是重复歌曲
	public static boolean isRepeatMusic(String title, String artist) {
		for (Music music : MusicUntils.sMusicList) {
			if (music.getArtist().equals(artist)
					&& music.getTitle().equals(title)) {
				return true;
			}
		}
		return false;
	}

	// 根据歌曲id和获取图片uri
	public static String getAlbumImageUri(int albumId) {
		String result = null;
		Cursor cursor = null;
		try {
			cursor = App.mContext.getContentResolver().query(
					Uri.parse("content://media/external/audio/albums/"
							+ albumId), new String[] { "album_art" }, null,
					null, null);
			while (cursor.moveToNext()) {
				result = cursor.getString(0);
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null)
				cursor.close();

		}
		return result;
	}
}
