package com.ssc.ttmusic.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LastMusicHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "musics.db";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE = "create table lastmusics(_id integer primary key autoincrement,name text,singer text,img text,id long);";
	private static final String DELETE_TABLE = "drop table if exists lastmusics";

	public LastMusicHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DELETE_TABLE);
		onCreate(db);
	}
}
