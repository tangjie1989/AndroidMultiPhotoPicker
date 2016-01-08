package com.tj.library.utils;

import java.io.File;

public class FileInfo {

	public static boolean isFileExit(String filePath) {

		File f = new File(filePath);
		if (f.exists()) {
			return true;
		}
		return false;

	}

}
