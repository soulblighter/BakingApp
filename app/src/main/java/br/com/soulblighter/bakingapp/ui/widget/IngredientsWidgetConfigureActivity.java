package br.com.soulblighter.bakingapp.ui.widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.provider.RecipesProvider;
import br.com.soulblighter.bakingapp.ui.main.MainAdapter;


public class IngredientsWidgetConfigureActivity extends AppCompatActivity implements MainAdapter
    .MainAdapterClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_LOADER_WIDGET_CONFIG = 202;

    private static final String PREFS_NAME = "br.com.soulblighter.bakingapp.ui.widget" +
        ".IngredientsWidgetConfigureActivity";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    RecyclerView mRecyclerView;
    private MainAdapter mAdapter;


    public IngredientsWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, int recipe_id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, recipe_id);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int recipe_id = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0);
        return recipe_id;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        setContentView(R.layout.ingredients_widget_configure);
        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new MainAdapter(getApplicationContext(), null, this);
        mRecyclerView.setAdapter(mAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(ID_LOADER_WIDGET_CONFIG, null, this);
    }

    @Override
    public void onMainAdapterClick(Recipe recipe) {
        final Context context = IngredientsWidgetConfigureActivity.this.getApplicationContext();

        // When the button is clicked, store the string locally
        saveTitlePref(context, mAppWidgetId, recipe.id);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        IngredientsWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_LOADER_WIDGET_CONFIG:
                //return new JsonFetchLoader(this, NetworkUtils.BAKING_JSON_PATH);
                return new CursorLoader(this, RecipesProvider.Recipes.CONTENT_URI, null, null, null, null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case ID_LOADER_WIDGET_CONFIG:
                if (data != null) {
                    updateData(data);
                } else {
                    Log.e(this.getClass().getSimpleName(), "onPostExecute: " + "parsedData is null");
                }
                break;

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateData(null);
    }

    private void updateData(Cursor data) {
        if (data != null) {
            List<Recipe> recipes = new ArrayList<>();
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                recipes.add(Recipe.loadFromCursor(this, data));
            }
            mAdapter.setData(recipes);
        } else {
            mAdapter.setData(null);
        }

        mAdapter.notifyDataSetChanged();
    }
}
