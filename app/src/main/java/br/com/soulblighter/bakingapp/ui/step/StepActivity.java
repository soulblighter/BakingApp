package br.com.soulblighter.bakingapp.ui.step;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.databinding.StepActivityBinding;
import br.com.soulblighter.bakingapp.ui.recipe.RecipeActivity;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeActivity}.
 */
public class StepActivity extends AppCompatActivity {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "recipe";
    public static final String ARG_POS = "pos";

    private StepActivityBinding mBinding;
    private StepPagerAdapter mSectionsPagerAdapter;
    private Recipe mRecipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_activity);
        mBinding = DataBindingUtil.setContentView(this, R.layout.step_activity);

        setSupportActionBar(mBinding.toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecipe = getIntent().getParcelableExtra(ARG_ITEM);
        int position = getIntent().getIntExtra(ARG_POS, 0);

        setTitle(mRecipe.name);
        mBinding.toolbar.setTitle(getTitle());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new StepPagerAdapter(getSupportFragmentManager(), this, mRecipe);

        // Set up the ViewPager with the sections adapter.
        mBinding.viewPager.setAdapter(mSectionsPagerAdapter);
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);
        mBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tabs));
        mBinding.tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mBinding.viewPager));
        mBinding.viewPager.setCurrentItem(position);


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
 /*       if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(StepFragment.ARG_ITEM, getIntent().getParcelableExtra(StepFragment.ARG_ITEM));
            StepFragment fragment = new StepFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.tabs, fragment).commit();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
