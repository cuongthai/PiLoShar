package com.appspot.pilo_shar.communicator;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class AnonymousCommunicator extends Communicator {
	

	@Override
	public String doGet(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		try {
			HttpResponse response = client.execute(request);
			String jsonString = EntityUtils.toString(response.getEntity());
			return jsonString;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String doPost(String url, MultipartEntity entity) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		postRequest.setEntity(entity);
		try {

			HttpResponse response = httpClient.execute(postRequest
					);
			String jsonString = EntityUtils.toString(response
					.getEntity());

			return jsonString;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String doPost(String url, UrlEncodedFormEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
