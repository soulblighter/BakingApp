package br.com.soulblighter.bakingapp.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.net.URL;

public class JsonFetchTask extends AsyncTask<String, Void, String> {

    public interface JsonFetchTaskCallback {
        void onJsonFetchTaskStart();

        void onJsonFetchTaskEnd(String result);
    }

    JsonFetchTaskCallback mCallback;

    public JsonFetchTask(JsonFetchTaskCallback callback) {
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        if (mCallback != null) {
            mCallback.onJsonFetchTaskStart();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String mUrl;
        if (params != null && params.length > 0) {
            mUrl = params[0];
        } else {
            return null;
        }

        if (mUrl == null || TextUtils.isEmpty(mUrl)) {
            return null;
        }
        try {
            URL taskUrl = new URL(mUrl);
            return NetworkUtils.getResponseFromHttpUrl(taskUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (mCallback != null) {
            mCallback.onJsonFetchTaskEnd(result);
        }
    }

}
