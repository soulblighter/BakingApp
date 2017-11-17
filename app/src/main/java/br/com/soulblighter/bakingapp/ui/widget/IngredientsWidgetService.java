package br.com.soulblighter.bakingapp.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Ingredient;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.provider.RecipesProvider;

public class IngredientsWidgetService extends RemoteViewsService {
    public final static String ARG_RECIPE_ID = "recipe_id";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int recipeId = intent.getIntExtra(ARG_RECIPE_ID, -1);
        return new IngredientsRemoteViewsFactory(this.getApplicationContext(), recipeId);
    }
}

class IngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    Cursor mRecipeCursor;
    int mRecipeId;

    public IngredientsRemoteViewsFactory(Context applicationContext, int recipeId) {
        mContext = applicationContext;
        mRecipeId = recipeId;
    }

    @Override
    public void onCreate() {
        mRecipeCursor = mContext.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, null, Recipe.Columns
            .id + " = ?", new String[]{String.valueOf(mRecipeId)}, Recipe.Columns.id + " ASC");
        mRecipeCursor.moveToFirst();
    }

    @Override
    public void onDataSetChanged() {
        if (mRecipeCursor != null) {
            mRecipeCursor.close();
        }
        mRecipeCursor = mContext.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, null, Recipe.Columns
            .id + " = ?", new String[]{String.valueOf(mRecipeId)}, Recipe.Columns.id + " ASC");
        mRecipeCursor.moveToFirst();
    }

    @Override
    public void onDestroy() {
        if (mRecipeCursor != null) {
            mRecipeCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mRecipeCursor == null || mRecipeCursor.getCount() == 0) {
            return 0;
        }
        Recipe recipe = Recipe.loadFromCursor(mContext, mRecipeCursor);
        return recipe.ingredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mRecipeCursor == null || mRecipeCursor.getCount() == 0)
            return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredients_list_content);

        Recipe recipe = Recipe.loadFromCursor(mContext, mRecipeCursor);
        Ingredient ingredient = recipe.ingredients.get(position);

        views.setTextViewText(R.id.ingredient, ingredient.ingredient);
        views.setTextViewText(R.id.quantity_measure, Ingredient.getFormatedQuantity(mContext, ingredient.quantity,
            ingredient.measure));

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        //Intent fillInIntent = new Intent();
        //fillInIntent.setAction(Long.toString(System.currentTimeMillis()));
        //fillInIntent.putExtra(RecipeActivity.ARG_RECIPE, (Parcelable)recipe);
        //views.setOnClickFillInIntent(R.id.ingredient, fillInIntent);
        //views.setOnClickFillInIntent(R.id.quantity_measure, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
