package br.com.soulblighter.bakingapp;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import br.com.soulblighter.bakingapp.data.Ingredient;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.data.Step;
import br.com.soulblighter.bakingapp.provider.RecipesProvider;
import br.com.soulblighter.bakingapp.ui.step.StepActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class StepActivityTest {

    // Unfortunatelly, using this rule didn't work only for this test
    //@Rule
    //public ActivityTestRule<StepActivity> mActivityTestRule = new ActivityTestRule<StepActivity>(StepActivity.class);

    @Test
    public void StepsTest() {
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

            // [START] Ingredients test
            Intent intent = new Intent(context, StepActivity.class);
            intent.putExtra(StepActivity.ARG_ITEM, (Parcelable) recipe);
            intent.putExtra(StepActivity.ARG_POS, 0);
            ActivityTestRule<StepActivity> mActivityTestRule = new ActivityTestRule<>(StepActivity.class);
            mActivityTestRule.launchActivity(intent);

            for (int c = 0; c < recipe.ingredients.size(); c++) {
                Ingredient ingredient = recipe.ingredients.get(0);

                ViewInteraction recyclerView = onView(withId(R.id.recyclerView));
                recyclerView.perform(scrollToPosition(c + 2)); // why "+2"? It should've be "+1", but didn't work
                recyclerView.check(matches(hasDescendant(withText(ingredient.ingredient))));
            }
            mActivityTestRule.finishActivity();
            // [END] Ingredients test


            // [START] Steps test
            for (int b = 0; b < recipe.steps.size(); b++) { // "<=" one more for ingredients
                Step step = recipe.steps.get(b);

                intent = new Intent(context, StepActivity.class);
                intent.putExtra(StepActivity.ARG_ITEM, (Parcelable) recipe);
                intent.putExtra(StepActivity.ARG_POS, b + 1);

                mActivityTestRule.launchActivity(intent);

                //if(step.thumbnailURL!= null && !step.thumbnailURL.isEmpty()) {
                //}
                //if(step.videoURL != null && !step.videoURL.isEmpty()) {
                //}

                onView(Matchers.allOf(withId(R.id.description), isDisplayed())).check(matches(withText(step
                    .description)));

                mActivityTestRule.finishActivity();
            }
            // [END] Steps test

        }
    }
}
