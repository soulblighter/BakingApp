package br.com.soulblighter.bakingapp;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.data.Step;
import br.com.soulblighter.bakingapp.provider.RecipesProvider;
import br.com.soulblighter.bakingapp.ui.recipe.RecipeActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    @Rule
    public ActivityTestRule<RecipeActivity> mActivityTestRule = new ActivityTestRule<>(RecipeActivity.class);

    @Test
    public void RecipesPortraitTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<Recipe> recipes = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, null, null, null,
            Recipe.Columns.id + " ASC");
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            recipes.add(Recipe.loadFromCursor(context, cursor));
        }
        cursor.close();


        for (int a = 0; a < recipes.size(); a++) {
            Recipe recipe = recipes.get(a);

            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra(RecipeActivity.ARG_RECIPE, (Parcelable) recipe);

            mActivityTestRule.launchActivity(intent).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Ingredients
            onView(withId(R.id.include1)).check(matches(hasDescendant(withText(R.string.recipe_ingredient))));

            ViewInteraction recyclerView = onView(withId(R.id.include1));
            recyclerView.perform(actionOnItemAtPosition(0, click()));

            Espresso.pressBack();

            for (int b = 0; b < recipe.steps.size(); b++) {
                Step step = recipe.steps.get(b);

                onView(withId(R.id.include1)).check(matches(hasDescendant(withText(step.shortDescription))));

                recyclerView = onView(withId(R.id.include1));
                recyclerView.perform(scrollToPosition(b + 2)); // why "+2"? It should've be "+1", but didn't work
                recyclerView.perform(actionOnItemAtPosition(b + 1, click()));

                Espresso.pressBack();
            }
            mActivityTestRule.finishActivity();
        }
    }

    @Test
    public void RecipesLandscapeTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        List<Recipe> recipes = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, null, null, null,
            Recipe.Columns.id + " ASC");
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            recipes.add(Recipe.loadFromCursor(context, cursor));
        }
        cursor.close();

        for (int a = 0; a < recipes.size(); a++) {
            Recipe recipe = recipes.get(a);

            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra(RecipeActivity.ARG_RECIPE, (Parcelable) recipe);

            mActivityTestRule.launchActivity(intent).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // Ingredients
            onView(withId(R.id.include1)).check(matches(hasDescendant(withText(R.string.recipe_ingredient))));

            ViewInteraction recyclerView = onView(withId(R.id.include1));
            recyclerView.perform(actionOnItemAtPosition(0, click()));

            for (int b = 0; b < recipe.steps.size(); b++) {
                Step step = recipe.steps.get(b);

                onView(withId(R.id.include1)).check(matches(hasDescendant(withText(step.shortDescription))));

                recyclerView = onView(withId(R.id.include1));
                recyclerView.perform(scrollToPosition(b + 2)); // why "+2"? It should've be "+1", but didn't work
                recyclerView.perform(actionOnItemAtPosition(b + 1, click()));

            }
            mActivityTestRule.finishActivity();
        }
    }
}
