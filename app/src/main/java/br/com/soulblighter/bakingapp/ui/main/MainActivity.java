package br.com.soulblighter.bakingapp.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import br.com.soulblighter.bakingapp.IdlingResource.SimpleIdlingResource;
import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.databinding.MainActivityBinding;
import br.com.soulblighter.bakingapp.network.JsonFetchTask;
import br.com.soulblighter.bakingapp.network.NetworkUtils;
import br.com.soulblighter.bakingapp.provider.RecipesProvider;
import br.com.soulblighter.bakingapp.ui.recipe.RecipeActivity;
import br.com.soulblighter.bakingapp.ui.step.StepActivity;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, JsonFetchTask
    .JsonFetchTaskCallback, MainAdapter.MainAdapterClickListener {

    private static final int ID_LOADER_MAIN = 101;

    private MainAdapter mAdapter;
    GridLayoutManager mLayoutManager;

    private MainActivityBinding mBinding;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setTitle(getTitle());

        int display_mode = getResources().getConfiguration().orientation;

        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            mBinding.recyclerView.setLayoutManager(mLayoutManager);
        } else {
            mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            mBinding.recyclerView.setLayoutManager(mLayoutManager);
        }

        mAdapter = new MainAdapter(this, null, this);

        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtils.isOnline(MainActivity.this)) {
                    showError(R.string.error_no_connection);
                    return;
                }
                new JsonFetchTask(MainActivity.this).execute(NetworkUtils.BAKING_JSON_PATH);
            }
        });

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(ID_LOADER_MAIN, null, this);

        loadData();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_LOADER_MAIN:
                //return new JsonFetchLoader(this, NetworkUtils.BAKING_JSON_PATH);
                return new CursorLoader(this, RecipesProvider.Recipes.CONTENT_URI, null, null, null, Recipe.Columns
                    .id + " ASC");
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case ID_LOADER_MAIN:
                if (data != null) {
                    updateData(data);
                    showDataGrid();
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

    private void showDataGrid() {
        mBinding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(int error) {
        mBinding.recyclerView.setVisibility(View.GONE);
        Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_LONG).show();
    }

    private void loadData() {
        if (!NetworkUtils.isOnline(this)) {
            showError(R.string.error_no_connection);
            return;
        }

        Cursor c = this.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, new String[]{Recipe.Columns
            .id}, null, null, null);

        // No  Recipe on database, lets fetch from internet
        if (c.getCount() == 0) {
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(false);
            }
            new JsonFetchTask(this).execute(NetworkUtils.BAKING_JSON_PATH);
        }
        c.close();

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


    @Override
    public void onJsonFetchTaskStart() {
        mBinding.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onJsonFetchTaskEnd(String result) {
        mBinding.swipeRefreshLayout.setRefreshing(false);
        List<Recipe> recipes;
        try {
            recipes = NetworkUtils.getRecipesFromJson(result);
            for (Recipe recipe : recipes) {
                recipe.insertOnDb(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Snackbar.make(mBinding.getRoot(), R.string.error_parse_json, Snackbar.LENGTH_LONG).show();
        }
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }

    @Override
    public void onMainAdapterClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(RecipeActivity.ARG_RECIPE, (Parcelable) recipe);
        startActivity(intent);
    }
}
