package com.appspot.pilo_shar.downloader;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImgCacheManager {
	private static Object instanceLock = new Object();
	private static ImgCacheManager cacheManager;

	/*
	 * control threads
	 */
	private int MAX_THREADS = 64;
	private int TIME_WAIT = 1000;
	private int numOfThreads;

	private ImgCacheManager(Context context) {
		numOfThreads = 0;
	}

	public static ImgCacheManager getInstance(Context context) {
		synchronized (instanceLock) {
			if (cacheManager == null) {
				cacheManager = new ImgCacheManager(context);
			}
		}

		return cacheManager;
	}

	/*
	 * This use to Fill thumb image in offer list Before download image -->
	 * check if it exists in imageCache url == thumb image URL
	 */
	public void fillImage(String url, ImageView iv, boolean isAnimated) {
		if (iv == null) {
			return;
		}
		
		Log.v("TagName","Filling Image");

		Bitmap bitmap = getBitmapFromCache(url);
		LogUtils logUtils = new LogUtils();

		if (bitmap == null) {
			download(url, iv, isAnimated);
			logUtils.print(this,
					"*--***Image Call*** No image in cache, download from URL "
							+ url);
		}

		// bitmap exists, cancel any downloaders
		else {
			cancelPotentialDownload(url, iv);
			//iv.setBackgroundColor(Color.WHITE);
			
			iv.setImageBitmap(bitmap);
			logUtils.print(this, "*--***Image Call*** Use Image in cache" + url);
		}
	}

	/*
	 * This use to Fill Big Main image in merchant detailView Before download
	 * image --> check if it exists in imageCache url == Big Main image URL
	 * alterUrl == thumb Image URL
	 */
	public void fillImage(String url, ImageView iv, String alterUrl,
			boolean isAnimated) {
		if (iv == null) {
			return;
		}

		Bitmap bitmap = getBitmapFromCache(url);
		LogUtils logUtils = new LogUtils();

		if (bitmap == null) {
			download(url, iv, alterUrl, isAnimated);
			logUtils.print(this, "*--***Image Call*** Bitmap is null " + url);
		}

		// image exists in database, load it directly without need to download
		else {
			cancelPotentialDownload(url, iv);
			iv.setImageBitmap(bitmap);
			logUtils.print(this, "*--***Image Call*** Bitmap is not null ");
		}
	}

	/*
	 * This use to download the thumb image in offer list url == thumb Image URL
	 */
	public void download(String url, ImageView iv, boolean isAnimated) {
		if (url == null) {
			return;
		}

		// ian: make sure that there is only 1 instance of this url being
		// called?
		if (cancelPotentialDownload(url, iv)) {

			// Create the animation
			Animation animation = null;
			if (isAnimated) {
				animation = createAnimation();
			}

			// Is is animated, pass in the animation
			ImgFetcher task = new ImgFetcher(iv, this, animation);

			// Create the drawable
			ImageDrawable drawable = new ImageDrawable(task);

			// Save the task to the image view
			iv.setImageDrawable(drawable);

			executeTask(task, url);
		}
	}

	/*
	 * This use to download the Big Main image in merchant view url == Big Main
	 * image URL alterUrl == thumb Image URL
	 */
	public void download(String url, ImageView iv, String alterUrl,
			boolean isAnimated) {
		if (url == null) {
			return;
		}

		if (cancelPotentialDownload(url, iv)) {

			// Create the animation
			Animation animation = null;
			if (isAnimated) {
				animation = createAnimation();
			}

			// It is animated, pass in the animation
			ImgFetcher task = new ImgFetcher(iv, this, animation);

			// Do like this to support image transition
			iv.setTag(task);

			if (alterUrl != null && alterUrl.trim().length() != 0) {
				Bitmap alterBitmap = getBitmapFromCache(alterUrl);
				if (alterBitmap != null) {

					Bitmap scaledBitmap = null;

					// new code to not scale thumbnail before the full image
					// loads
					scaledBitmap = createBitmap(alterBitmap, 148, 148);
					iv.setScaleType(ScaleType.CENTER);

					/*
					 * old code where the thumbnail is scaled up first
					 * //maintain aspect ratio is image is a logo, otherwise can
					 * scale both x and y to fit.
					 * if(Utils.isImageLogo(alterBitmap)){ scaledBitmap =
					 * createBitmap(alterBitmap, 261, 261);
					 * iv.setScaleType(ScaleType.FIT_CENTER); } //image is not
					 * logo, scale to fit else{ scaledBitmap =
					 * createBitmap(alterBitmap, 261, 261);
					 * iv.setScaleType(ScaleType.CENTER); }
					 */

					iv.setImageBitmap(scaledBitmap);

				}
			}

			executeTask(task, url);
		}
	}

	/*
	 * The synchronized block is used to ensure that this method will be
	 * accessed one thread at a time, which makes them thread-safe
	 */
	public void decreaseNumOfThreads() {
		LogUtils logUtils = new LogUtils();

		logUtils.print(this, "Will Decrease Number of THREAD = " + numOfThreads);
		synchronized (instanceLock) {
			--numOfThreads;
			logUtils.print(this,
					"*--***Image Call*** Decrease threads. Current number of threads: "
							+ numOfThreads);
		}
	}

	/*
	 * The synchronized block is used to ensure that this method will be
	 * accessed one thread at a time, which makes them thread-safe
	 */
	private void executeTask(ImgFetcher fetcher, String url) {
		boolean isFull;
		new LogUtils().print(this, "Total Number of THREADS = " + numOfThreads);
		synchronized (instanceLock) {
			isFull = numOfThreads >= MAX_THREADS;
		}

		if (isFull) {
			waitTask(fetcher, url);
		} else {
			fetcher.execute(url);

			synchronized (instanceLock) {
				++numOfThreads;
			}
		}
	}

	// methods to wait for threads
	private void waitTask(ImgFetcher task, String url) {
		final Handler handler = new Handler();
		handler.postDelayed(new ExecuteTask(task, url), TIME_WAIT);
	}

	public static Bitmap createBitmap(Bitmap src, int newWidth, int newHeight) {

		Bitmap bitmap = Bitmap.createBitmap(src);
		if (bitmap == null) {
			return null;
		}

		Matrix matrix = new Matrix();
		// resize the bit map
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	private Animation createAnimation() {
		// load animation
		Animation animation = new AlphaAnimation(0, 1);
		animation.setDuration(1000);
		return animation;
	}

	private boolean cancelPotentialDownload(String url, ImageView iv) {
		ImgFetcher fetcher = getImgFetcher(iv);

		if (fetcher != null) {
			String bitmapUrl = fetcher.getUrl();
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				fetcher.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	public ImgFetcher getImgFetcher(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof ImageDrawable) {
				ImageDrawable offerImageDrawable = (ImageDrawable) drawable;
				return offerImageDrawable.getImgFetcher();
			}

			Object task = imageView.getTag();
			if (task instanceof ImgFetcher) {
				return (ImgFetcher) task;
			}
		}

		return null;
	}

	/*
	 * Code related to cache
	 */

	private static final int HARD_CACHE_CAPACITY = 100;

	// Hard cache, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2);

	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				try {
					sHardBitmapCache.put(url, bitmap);
				} catch (OutOfMemoryError ex) {
					// Oops, out of memory, clear cache
					clearCache();
				}
			}
		}
	}

	public Bitmap getBitmapFromCache(String url) {
		if (url == null) {
			return null;
		}

		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}

		return null;
	}

	public void clearCache() {
		sHardBitmapCache.clear();
	}

	private class ExecuteTask implements Runnable {
		private ImgFetcher fetcher;
		private String url;

		public ExecuteTask(ImgFetcher fetcher, String url) {
			this.fetcher = fetcher;
			this.url = url;
		}

		public void run() {
			ImgCacheManager.this.executeTask(fetcher, url);
		}
	}
}
