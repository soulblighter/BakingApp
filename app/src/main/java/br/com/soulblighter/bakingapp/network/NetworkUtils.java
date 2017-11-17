package br.com.soulblighter.bakingapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.soulblighter.bakingapp.data.Recipe;

public class NetworkUtils {

    public static final String BAKING_JSON_PATH = "http://go.udacity.com/android-baking-app-json";

    // https://stackoverflow.com/a/45253545/1615055
    private static HttpURLConnection openConnectionWithRedirection(String url) throws IOException {
        HttpURLConnection connection;
        boolean redirected;
        do {
            connection = (HttpURLConnection) new URL(url).openConnection();
            int code = connection.getResponseCode();
            redirected = code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP ||
                code == HttpURLConnection.HTTP_SEE_OTHER;
            if (redirected) {
                url = connection.getHeaderField("Location");
                connection.disconnect();
            }
        } while (redirected);
        return connection;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = openConnectionWithRedirection(url.toString());
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // https://stackoverflow.com/questions/1560788/how-to-check-internet
    // -access-on-android-inetaddress-never-times-out
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static List<Recipe> getRecipesFromJson(String jsonStr) throws JSONException {

        JSONArray resultsArray = new JSONArray(jsonStr);

        Gson gson = new Gson();
        List<Recipe> itens = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            Recipe item = gson.fromJson(String.valueOf(resultsArray.getJSONObject(i)), Recipe.class);
            itens.add(item);
        }

        return itens;
    }
}
