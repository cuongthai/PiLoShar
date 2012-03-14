package com.appspot.pilo_shar.downloader;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.animation.Animation;
import android.widget.ImageView;

public class ImgFetcher extends AsyncTask<String, Void, Bitmap> {
	private String url;
	private ImgCacheManager cacheManager;
	private final WeakReference<ImageView> imageViewReference;
	private Animation animation;

	public ImgFetcher(ImageView imageView, ImgCacheManager cacheManager,
			Animation animation) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.cacheManager = cacheManager;
		this.animation = animation;
	}

	public String getUrl() {
		return url;
	}

	protected Bitmap doInBackground(String... urls) {
		new LogUtils().print(this, "*--***Image Call*** Downloading... "
				+ urls[0]);
		url = urls[0];

		return new ImageGetter().getBitmap(url);
	}

	protected void onPostExecute(Bitmap bitmap) {
		new LogUtils().print(this, "--- OnPost Execute");

		cacheManager.decreaseNumOfThreads();

		if (isCancelled() || bitmap == null) {
			return;
		}

		cacheManager.addBitmapToCache(url, bitmap);

		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();

			// Get the fetcher
			ImgFetcher fetcher = cacheManager.getImgFetcher(imageView);

			// Change bitmap only if this process is still associated with it
			// Or if we don't use any bitmap to task association
			if (fetcher == this && url.equals(fetcher.url)) {

				// loading full image section
				if (bitmap.getWidth() > 120) {

					// this is the Citi provided dimensions of the original
					// image
					imageView.setImageBitmap(ImgCacheManager.createBitmap(
							bitmap, bitmap.getWidth(), bitmap.getHeight()));
				}

				// loading thumbnail section
				else {
					// maintain aspect ratio is image is a logo, otherwise can
					// scale both x and y to fit.

					// this is the Citi provided dimensions
					imageView.setImageBitmap(ImgCacheManager.createBitmap(
							bitmap, bitmap.getWidth(), bitmap.getHeight()));
				}

				// Start the animation if ok
				if (animation != null && imageView.getAnimation() == null) {

					imageView.startAnimation(animation);
					imageView.invalidate();

				}
			}
		}
	}

	@Override
	protected void onCancelled() {
		/*
		 * When canceling a THREAD --> remember to decreaseNumOfThreads() If not
		 * the numOfThread will equal MaxThread and the loading will stuck
		 */
		cacheManager.decreaseNumOfThreads();
		super.onCancelled();
	}
}
