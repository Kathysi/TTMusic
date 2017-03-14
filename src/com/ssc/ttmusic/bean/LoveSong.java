package com.ssc.ttmusic.bean;

public class LoveSong {
	private String name;//歌曲名字
	private String singer;//歌手名字
	private String url;//歌曲图片
	private long songid;//歌曲id
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getSongid() {
		return songid;
	}
	public void setSongid(long songid) {
		this.songid = songid;
	}
	

}
