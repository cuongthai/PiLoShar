package com.appspot.pilo_shar;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.appspot.pilo_shar.communicator.AnonymousCommunicator;
import com.appspot.pilo_shar.communicator.Communicator;
import com.appspot.pilo_shar.downloader.ImgCacheManager;

public class ImageActivity extends Activity {
	private GridView gridview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_grid_image);
		gridview = (GridView) findViewById(R.id.gv_image);
		new AsyncGetImageLinks().execute();
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private String[] mImagesUrls;

		public ImageAdapter(Context c, String[] imagesUrls) {
			mContext = c;
			mImagesUrls = imagesUrls;
		}

		public int getCount() {
			return mImagesUrls.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(200, 150));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
				
			} else {
				imageView = (ImageView) convertView;
			}

			// Fill images to gridview, Thanks to Nguyen Huy for this lovely
			// class
			ImgCacheManager.getInstance(mContext).fillImage(
					mImagesUrls[position] + "=s200", imageView, true);
			return imageView;
		}
	}

	private class AsyncGetImageLinks extends AsyncTask<Void, Void, Void> {
		private String[] urls;

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			Communicator communicator = new AnonymousCommunicator();
			String json = communicator.doGet(Urls.ALL_IMAGES_URL);
			try {
				JSONArray jsonArray=new JSONArray(json);
				urls=new String[jsonArray.length()];
				for (int i=0;i<jsonArray.length();i++){
					urls[i]=jsonArray.getString(i);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setProgressBarIndeterminateVisibility(false);
			gridview.setAdapter(new ImageAdapter(ImageActivity.this, urls));
		}

	}
}