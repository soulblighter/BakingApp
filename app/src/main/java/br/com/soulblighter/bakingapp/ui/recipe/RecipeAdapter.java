package br.com.soulblighter.bakingapp.ui.recipe;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.ui.step.IngredientsFragment;
import br.com.soulblighter.bakingapp.ui.step.StepActivity;
import br.com.soulblighter.bakingapp.ui.step.StepFragment;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final AppCompatActivity mParentActivity;
    private Recipe mValues;
    private final boolean mTwoPane;
    private int mCurrentSelected; // show current selected step

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            selectFragment(position);
        }
    };

    void selectFragment(int position) {
        if (mTwoPane) {
            Fragment fragment;
            if (position == 0) {
                fragment = IngredientsFragment.newInstance(mValues);
            } else {
                fragment = StepFragment.newInstance(mValues, position);
            }

            mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout2, fragment)
                .commit();

            mCurrentSelected = position;
            notifyDataSetChanged();
        } else {
            Intent intent = new Intent(mParentActivity, StepActivity.class);
            intent.putExtra(StepActivity.ARG_ITEM, (Parcelable) mValues);
            intent.putExtra(StepActivity.ARG_POS, position);
            mParentActivity.startActivity(intent);
        }
    }

    RecipeAdapter(RecipeActivity parent, Recipe items, boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
        mCurrentSelected = 0;
        if (twoPane) {
            selectFragment(mCurrentSelected);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position == 0) {
            holder.mContentView.setText(R.string.recipe_ingredient);
        } else {
            holder.mContentView.setText(mValues.steps.get(position - 1).shortDescription);
        }
        holder.itemView.setTag(position);

        if (mTwoPane && mCurrentSelected == position) {
            holder.mCardView.setBackgroundColor(mParentActivity.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mCardView.setBackgroundColor(mParentActivity.getResources().getColor(android.R.color.white));
        }
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            int size = 0;
            // if has ingredients, add one list item for it
            if (mValues.ingredients != null && mValues.ingredients.size() > 0) {
                size += 1;
            }
            // add steps
            if (mValues.steps != null && mValues.steps.size() > 0) {
                size += mValues.steps.size();
            }
            return size;
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mContentView;
        final CardView mCardView;

        ViewHolder(View view) {
            super(view);
            mContentView = view.findViewById(R.id.info_text);
            mCardView = view.findViewById(R.id.card_view);
        }
    }

    public void setData(Recipe data) {
        mValues = data;
        notifyDataSetChanged();
    }
}
