package br.com.soulblighter.bakingapp.ui.step;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Ingredient;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.ui.recipe.RecipeActivity;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link StepActivity}
 * on handsets.
 */
public class IngredientsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IngredientAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IngredientsFragment() {
    }

    public static IngredientsFragment newInstance(Recipe recipe) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(StepActivity.ARG_ITEM, recipe);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(StepActivity.ARG_ITEM)) {
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        //int pos = getArguments().getInt(StepActivity.ARG_POS);
        Recipe item = getArguments().getParcelable(StepActivity.ARG_ITEM);

        rootView = inflater.inflate(R.layout.ingredents_fragment, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new IngredientAdapter(item.ingredients);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        final TextView quantity_measure;
        final TextView ingredient;

        IngredientViewHolder(View view) {
            super(view);
            quantity_measure = view.findViewById(R.id.quantity_measure);
            ingredient = view.findViewById(R.id.ingredient);
        }
    }

    public class IngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder> {

        List<Ingredient> mData;

        IngredientAdapter(List<Ingredient> data) {
            mData = data;
        }

        @Override
        public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredients_list_content, parent,
                false);
            return new IngredientViewHolder(view);
        }

        @Override
        public void onBindViewHolder(IngredientViewHolder holder, int position) {
            Ingredient ingredient = mData.get(position);

            holder.ingredient.setText(ingredient.ingredient);
            holder.quantity_measure.setText(Ingredient.getFormatedQuantity(getActivity(), ingredient.quantity,
                ingredient.measure));
        }

        @Override
        public int getItemCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }
    }
}
