package br.com.soulblighter.bakingapp.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.RecipeViewHolder> {

    private final MainAdapterClickListener mCallback;
    private final Context mContext;
    private List<Recipe> mValues;

    public interface MainAdapterClickListener {
        void onMainAdapterClick(Recipe recipe);
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe item = (Recipe) view.getTag();
            if (mCallback != null) {
                mCallback.onMainAdapterClick(item);
            }
        }
    };

    public MainAdapter(Context context, List<Recipe> items, MainAdapterClickListener callback) {
        mValues = items;
        mCallback = callback;
        mContext = context;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_content, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        Recipe recipe = mValues.get(position);
        holder.name.setText(recipe.name);

        if (recipe.image != null && !recipe.image.isEmpty()) {
            Picasso.with(mContext).load(recipe.image).fit().into(holder.image, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.image.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.image.setVisibility(View.GONE);
                }
            });
        } else {
            holder.image.setVisibility(View.GONE);
        }

        Resources res = mContext.getResources();
        String servings = res.getQuantityString(R.plurals.serving_text, recipe.servings, recipe.servings);
        holder.servings.setText(servings);

        holder.itemView.setTag(recipe);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        } else {
            return 0;
        }
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView servings;
        final ImageView image;

        RecipeViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            servings = view.findViewById(R.id.servings);
            image = view.findViewById(R.id.image);
        }
    }

    public void setData(List<Recipe> data) {
        mValues = data;
        notifyDataSetChanged();
    }
}
