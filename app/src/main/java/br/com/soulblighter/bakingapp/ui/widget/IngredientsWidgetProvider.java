package br.com.soulblighter.bakingapp.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.widget.RemoteViews;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.provider.RecipesProvider;
import br.com.soulblighter.bakingapp.ui.main.MainActivity;
import br.com.soulblighter.bakingapp.ui.recipe.RecipeActivity;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        int recipe_id = IngredientsWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        Cursor c = context.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, null, Recipe.Columns.id +
            "=?", new String[]{String.valueOf(recipe_id)}, null);

        RemoteViews views;
        // No  Recipe on database, lets fetch from internet
        if (c.getCount() == 0) {
            views = getEmptyRemoteView(context);
        } else {
            c.moveToFirst();
            Recipe recipe = Recipe.loadFromCursor(context, c);
            views = getRecipeRemoteView(context, recipe);
        }
        c.close();

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    private static RemoteViews getEmptyRemoteView(Context context) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_empty);

        // Create an Intent to launch MainActivity when clicked
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent
            .FLAG_UPDATE_CURRENT);
        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.linearLayout, mainPendingIntent);
        return views;
    }

    private static RemoteViews getRecipeRemoteView(Context context, Recipe recipe) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

        Intent serviceIntent = new Intent(context, IngredientsWidgetService.class);
        serviceIntent.putExtra(IngredientsWidgetService.ARG_RECIPE_ID, recipe.id);
        views.setRemoteAdapter(R.id.widgetListView, serviceIntent);

        views.setTextViewText(R.id.headerText, recipe.name);

        // Create an Intent to launch MainActivity when clicked
        Intent mainIntent = new Intent(context, RecipeActivity.class);
        //mainIntent.setAction(Long.toString(System.currentTimeMillis()));
        mainIntent.putExtra(RecipeActivity.ARG_RECIPE, (Parcelable) recipe);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, recipe.id, mainIntent, PendingIntent
            .FLAG_CANCEL_CURRENT);
        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.headerText, mainPendingIntent);
/*
        // Create an Intent to launch RecipeActivity when clicked
        Intent appIntent = new Intent(context, RecipeActivity.class);
        appIntent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, recipe.id, appIntent,
            PendingIntent.FLAG_CANCEL_CURRENT);
        // Widgets allow click handlers to only launch pending intents
        views.setPendingIntentTemplate(R.id.widgetListView, appPendingIntent);
*/
        return views;
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // When a new Widget is added on homescreen we must assing it to an Recipe available

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            IngredientsWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

