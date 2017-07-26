package com.example.retrievevideo.emergencymessaging;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.example.retrievevideo.BuildConfig;


import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Base64;
import android.util.Log;

public class NetworkUtils {
	public static final String TAG = "NetworkUtils";

	public static HttpResponseFormatDto getGETResponse(Context context,
			String url, Map<String, String> headerMap) throws Exception {

		HttpResponseFormatDto httpResponseFormatDto = new HttpResponseFormatDto();

		AndroidHttpClient httpClient = null;
		try {
			httpClient = AndroidHttpClient.newInstance("EM");
			HttpGet httpGet = new HttpGet(url);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "http get url " + url);
			}
			Utils.setTimeouts(httpGet);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
					Log.v(TAG, entry.getKey() + ":" + entry.getValue());
				}
			}
			httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");

			HttpResponse httpResponse = httpClient.execute(httpGet);
			String response = EntityUtils.toString(httpResponse.getEntity());

			httpResponseFormatDto.setData(response);
			httpResponseFormatDto.setStatusCode(httpResponse.getStatusLine()
					.getStatusCode());

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"http get response JSON: "
								+ httpResponseFormatDto.getData());
				Log.d(TAG,
						"http get response code: "
								+ httpResponseFormatDto.getStatusCode());
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}

		return httpResponseFormatDto;
	}

	
	
	public static HttpResponseFormatDto getPOSTResponse(Context context,
			String url, String requestJson, Map<String, String> headerMap)
			throws Exception {

		HttpResponseFormatDto httpResponseFormatDto = new HttpResponseFormatDto();

		AndroidHttpClient httpClient = null;
		try {
			httpClient = AndroidHttpClient.newInstance("EM");
			HttpPost httpPost = new HttpPost(url);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "http post url " + url);
				Log.d(TAG, "http post body " + requestJson);
			}
			Utils.setTimeouts(httpPost);

			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			httpPost.addHeader("Authorization", "Basic "+Base64.encodeToString("rat#1:rat".getBytes(),Base64.NO_WRAP));

			HttpEntity requestEntity = new StringEntity(requestJson);
			httpPost.setEntity(requestEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			String response = EntityUtils.toString(httpResponse.getEntity());

			httpResponseFormatDto.setData(response);
			httpResponseFormatDto.setStatusCode(httpResponse.getStatusLine()
					.getStatusCode());

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"http post response JSON: "
								+ httpResponseFormatDto.getData());
				Log.d(TAG,
						"http post response code: "
								+ httpResponseFormatDto.getStatusCode());
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}

		return httpResponseFormatDto;
	}

	
	public static HttpResponseFormatDto getPOSTResponse(
			String url, String requestJson, Map<String, String> headerMap)
			throws Exception {

		HttpResponseFormatDto httpResponseFormatDto = new HttpResponseFormatDto();

		AndroidHttpClient httpClient = null;
		try {
			httpClient = AndroidHttpClient.newInstance("EM");
			HttpPost httpPost = new HttpPost(url);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "http post url " + url);
				Log.d(TAG, "http post body " + requestJson);
			}
			Utils.setTimeouts(httpPost);

			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			httpPost.addHeader("Authorization", "Basic "+Base64.encodeToString("rat#1:rat".getBytes(),Base64.NO_WRAP));

			HttpEntity requestEntity = new StringEntity(requestJson);
			httpPost.setEntity(requestEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			String response = EntityUtils.toString(httpResponse.getEntity());

			httpResponseFormatDto.setData(response);
			httpResponseFormatDto.setStatusCode(httpResponse.getStatusLine()
					.getStatusCode());

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"http post response JSON: "
								+ httpResponseFormatDto.getData());
				Log.d(TAG,
						"http post response code: "
								+ httpResponseFormatDto.getStatusCode());
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}

		return httpResponseFormatDto;
	}
	
	public static HttpResponseFormatDto getDELETEResponse(Context context,
			String url, Map<String, String> headerMap) throws Exception {
		HttpResponseFormatDto httpResponseFormatDto = new HttpResponseFormatDto();

		AndroidHttpClient httpClient = null;
		try {
			httpClient = AndroidHttpClient.newInstance("EM");
			HttpDelete httpDelete = new HttpDelete(url);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "http delete url " + url);
			}
			Utils.setTimeouts(httpDelete);

			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpDelete.addHeader(entry.getKey(), entry.getValue());
				}
			}
			httpDelete.setHeader("Content-Type",
					"application/json;charset=UTF-8");

			HttpResponse httpResponse = httpClient.execute(httpDelete);
			String response = (httpResponse.getEntity() != null) ? EntityUtils
					.toString(httpResponse.getEntity()) : "{}";

			httpResponseFormatDto.setData(response);
			httpResponseFormatDto.setStatusCode(httpResponse.getStatusLine()
					.getStatusCode());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "http delete response JSON: "
						+ httpResponseFormatDto.getData());
				Log.d(TAG, "http delete response code: "
						+ httpResponseFormatDto.getStatusCode());
			}

		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
		return httpResponseFormatDto;
	}

}
