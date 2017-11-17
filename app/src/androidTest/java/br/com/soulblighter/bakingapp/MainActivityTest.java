package br.com.soulblighter.bakingapp;


import android.content.Context;
import android.database.Cursor;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.provider.RecipesProvider;
import br.com.soulblighter.bakingapp.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void RecipesTest() {
        Context context = mActivityTestRule.getActivity();
        List<Recipe> recipes = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, null, null, null,
            Recipe.Columns.id + " ASC");
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            recipes.add(Recipe.loadFromCursor(context, cursor));
        }
        cursor.close();

        for (int i = 0; i < recipes.size(); i++) {

            Recipe recipe = recipes.get(i);

            onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText(recipe.name))));

            ViewInteraction recyclerView = onView(withId(R.id.recyclerView));
            recyclerView.perform(actionOnItemAtPosition(i, click()));

            Espresso.pressBack();
        }
    }
/*
    @Test
    public void CheesecakeTest() {
        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Cheesecake"))));

        ViewInteraction recyclerView = onView(withId(R.id.recyclerView));
        recyclerView.perform(actionOnItemAtPosition(1, click()));
    }

    @Test
    public void YellowCakeTest() {
        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Yellow Cake"))));

        ViewInteraction recyclerView = onView(withId(R.id.recyclerView));
        recyclerView.perform(actionOnItemAtPosition(1, click()));
    }

    @Test
    public void BrowniesTest() {
        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Brownies"))));

        ViewInteraction recyclerView = onView(withId(R.id.recyclerView));
        recyclerView.perform(actionOnItemAtPosition(2, click()));
    }

    @Test
    public void NutellaPieTest() {
        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Nutella Pie"))));

        ViewInteraction recyclerView = onView(withId(R.id.recyclerView));
        recyclerView.perform(actionOnItemAtPosition(3, click()));
    }

    // Remember to unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }*/
}
