package br.com.soulblighter.bakingapp.ui.recipe;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.databinding.RecipeActivityBinding;
import br.com.soulblighter.bakingapp.ui.step.StepActivity;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeActivity extends AppCompatActivity {

    public static final String ARG_RECIPE = "recipe";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecipeAdapter mAdapter;
    private RecipeActivityBinding mBinding;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);

        mBinding = DataBindingUtil.setContentView(this, R.layout.recipe_activity);

        setSupportActionBar(mBinding.toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.frameLayout2) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (getIntent().hasExtra(ARG_RECIPE)) {
            mRecipe = getIntent().getParcelableExtra(ARG_RECIPE);
            setTitle(mRecipe.name);
            mBinding.toolbar.setTitle(getTitle());
        } else {
            finish();
        }

        mAdapter = new RecipeAdapter(this, mRecipe, mTwoPane);
        mBinding.include1.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
