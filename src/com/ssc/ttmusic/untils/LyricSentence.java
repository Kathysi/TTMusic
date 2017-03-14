/**
 * Copyright (c) www.longdw.com
 */
package com.ssc.ttmusic.untils;

/**
 * 歌词句子，是�?个时间戳和一行歌词组成，如�?�[00.03.21.56]还记得许多年前的春天�?
 */
public class LyricSentence {
    private long startTime = 0;
    private long duringTime = 0;
    private String contentText = "";
    public LyricSentence(long time, String text) {
        startTime = time;
        contentText = text;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public long getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(long duringTime) {
        this.duringTime = duringTime;
    }
}
