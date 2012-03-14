package com.appspot.pilo_shar.downloader;

import java.lang.ref.WeakReference;

import android.graphics.drawable.ColorDrawable;

public class ImageDrawable extends ColorDrawable {
	private final WeakReference<ImgFetcher> imageFetcherReference;

	public ImageDrawable(ImgFetcher imgFetcher) {
		super(android.R.color.transparent);
		imageFetcherReference = new WeakReference<ImgFetcher>(imgFetcher);
	}

	public ImgFetcher getImgFetcher() {
		return imageFetcherReference.get();
	}
}
