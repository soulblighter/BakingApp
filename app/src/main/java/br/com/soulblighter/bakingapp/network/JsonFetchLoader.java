package br.com.soulblighter.bakingapp.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import java.io.IOException;
import java.net.URL;

public class JsonFetchLoader extends AsyncTaskLoader<String> {

    String mJson;
    String mUrl;

    public JsonFetchLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (mUrl == null) {
            return;
        }
        if (mJson != null) {
            deliverResult(mJson);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
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
    public void deliverResult(String json) {
        mJson = json;
        super.deliverResult(json);
    }
}
