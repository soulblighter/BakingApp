package br.com.soulblighter.bakingapp.ui.step;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class StepPagerAdapter extends FragmentPagerAdapter {

    Recipe mData;
    Context mContext;

    public StepPagerAdapter(FragmentManager fm, Context context, Recipe data) {
        super(fm);
        mData = data;
        mContext = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mData != null) {
            if (position == 0) {
                return mContext.getString(R.string.recipe_ingredient);
            } else {
                return mData.steps.get(position - 1).shortDescription;
            }
        }
        return mContext.getString(R.string.recipe_step_title_placeholder, position - 1);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = IngredientsFragment.newInstance(mData);
        } else {
            fragment = StepFragment.newInstance(mData, position);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (mData != null) {
            // first page for ingredients
            size += 1;
            // add steps
            if (mData.steps != null && mData.steps.size() > 0) {
                size += mData.steps.size();
            }
        }
        return size;
    }
}
