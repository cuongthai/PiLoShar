package com.appspot.pilo_shar.communicator;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;

public abstract class  Communicator {
	public abstract String doGet(String url);
	public abstract String doPost(String url,UrlEncodedFormEntity entity);
	public abstract String doPost(String url,MultipartEntity entity);
	
}
