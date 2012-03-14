package com.appspot.pilo_shar.downloader;

import android.util.Log;

public class LogUtils {
	private static String LOG_TAG = "Sharex";

	public void print(Object owner, String message) {
		Log.v(LOG_TAG, owner.getClass().getName() + " : " + message);
	}

	public void printError(Object owner, String message) {
		Log.e(LOG_TAG, owner.getClass().getName() + " : " + message);
	}

	public void printException(Object owner, Exception exception) {
		printError(owner, exception.getMessage());
	}
}
