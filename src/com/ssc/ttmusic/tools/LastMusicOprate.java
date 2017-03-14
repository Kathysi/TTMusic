package com.ssc.ttmusic.tools;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.ssc.ttmusic.bean.LoveSong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class LastMusicOprate {

	private static final String TABLE_NAME = "musics";

	private MusicHelper mHelper;

	public LastMusicOprate(Context context) {
		mHelper = new MusicHelper(context);
	}

	public void deleteBymId(long mId) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete(TABLE_NAME, "id=?", new String[] { String.valueOf(mId) });
		db.close();
	}

	public void deleteAll() {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		String sql1 = "update sqlite_sequence set seq=0 where name='musics'";
		db.execSQL(sql1);
		String sql = "delete from musics";
		db.execSQL(sql);
		db.close();
	}

	public List<LoveSong> searchBymId(long mId) {
		List<LoveSong> list = new ArrayList<LoveSong>();
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, "id=?",
				new String[] { String.valueOf(mId) }, null, null, null, null);
		while (cursor.moveToNext()) {
			LoveSong song = new LoveSong();
			song.setName(cursor.getString(cursor.getColumnIndex("name")));
			song.setSinger(cursor.getString(cursor.getColumnIndex("singer")));
			song.setSongid(cursor.getLong(cursor.getColumnIndex("id")));
			String img = cursor.getString(cursor.getColumnIndex("img"));
			//Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
			song.setUrl(img);
			list.add(song);

		}
		db.close();
		return list;

	}
	public boolean searchSongs(long mId) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, "id=?",
				new String[] { String.valueOf(mId) }, null, null, null, null);
		if(cursor!=null&&cursor.getCount()!=0)
		{
			cursor.close();
			return true;
		}
		else {
			cursor.close();
			return false;
		}

	}
	public List<LoveSong> searchAll() {
		List<LoveSong> list = new ArrayList<LoveSong>();
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				null, null);
		while (cursor.moveToNext()) {
			LoveSong song = new LoveSong();
			song.setName(cursor.getString(cursor.getColumnIndex("name")));
			song.setSinger(cursor.getString(cursor.getColumnIndex("singer")));
			song.setSongid(cursor.getLong(cursor.getColumnIndex("id")));
			String img = cursor.getString(cursor.getColumnIndex("img"));
			//Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
			song.setUrl(img);
			list.add(song);

		}
		db.close();
		return list;
	}

	public void insert(LoveSong song) {

		
		SQLiteDatabase db = mHelper.getWritableDatabase();
		if(searchSongs(song.getSongid()))
		{
			return;
		}
		ContentValues values = new ContentValues();
		values.put("id", song.getSongid());
		values.put("name", song.getName());
		values.put("singer", song.getSinger());
		//Bitmap bitmap = song.getBitmap();
		//ByteArrayOutputStream os = new ByteArrayOutputStream();
		//bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		values.put("img", song.getUrl());
		db.insert(TABLE_NAME, null, values);
		db.close();
	}

}
