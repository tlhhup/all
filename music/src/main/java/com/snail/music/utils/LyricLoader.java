package com.snail.music.utils;

import java.io.File;

/**
 * 模拟歌词加载模块
 * @author Administrator
 *
 */
public class LyricLoader {
	private static String LYRIC_DIR = "/mnt/sdcard/audio";

	public static File loadLyricFile(String audioName){
		File file = new File(LYRIC_DIR, StringUtils.formatAudioName(audioName)+".txt");
		if(!file.exists()){
			file = new File(LYRIC_DIR, StringUtils.formatAudioName(audioName)+".lrc");
		}
		return file;
	}
}
