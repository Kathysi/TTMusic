package com.ssc.ttmusic.untils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.ssc.ttmusic.application.App;
import com.xiami.sdk.entities.OnlineSong;

public class LrcUntils {
	public static String getXiamiLrc(String musicName, String musicSinger,
			long id) {
		String path = null;
		path = getLocalLrc(musicName, musicSinger, "");
		if (path == null) {
			String lrcUrl;
			OnlineSong lrcSong = App.mSdk
					.findSongByIdSync(id, OnlineSong.Quality.L);
			if (lrcSong != null) {

				lrcUrl = lrcSong.getLyric().trim();
				String musiclrc = fetchLyricContent(lrcUrl, musicName,
						musicSinger);
				if (musiclrc != null)
					return musiclrc;
				else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return path;
		}
	}

	public static String getLocalLrc(String musicName, String musicSinger,
			String musicPath) {
		File pathFile = new File(MusicUntils.getLrcDir());
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		File[] listFiles = pathFile.listFiles();
		if (listFiles.length > 0) {
			for (File mFile : listFiles) {
				String lrcName = mFile.getName();
				String lrcPath = mFile.getPath();
				if (lrcName != null) {
					if (lrcName.contains(musicName)
							&& lrcName.contains(musicSinger)) {
						return lrcPath;
					}
				}
			}
		}
		return null;
	}

	public static String fetchLyricContent(String musicLrcPath,
			String musicName, String singerName) {
		if (!musicLrcPath.startsWith("http:")) {
			return null;
		}
		HttpGet getMethod = new HttpGet(musicLrcPath);
		HttpClient httpClient = new DefaultHttpClient();

		String content = null;
		try {
			HttpResponse response = httpClient.execute(getMethod);
			if (response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (content != null) {
			String folderPath = MusicUntils.getLrcDir();
			File savefolder = new File(folderPath);
			if (!savefolder.exists()) {
				savefolder.mkdirs();
			}

			String savePath = folderPath + File.separator + musicName + "-"
					+ singerName + ".lrc";
			saveLyric(content.toString(), savePath);
			return savePath;
		}

		return null;

	}

	public static void saveLyric(String content, String filePath) {
		File file = new File(filePath);
		try {
			OutputStream outstream = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			out.write(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
