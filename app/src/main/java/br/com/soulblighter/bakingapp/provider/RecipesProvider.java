package br.com.soulblighter.bakingapp.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import br.com.soulblighter.bakingapp.data.Ingredient;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.data.Step;


@ContentProvider(authority = RecipesProvider.AUTHORITY, database = RecipesDatabase.class)
public class RecipesProvider {

    public static final String AUTHORITY = "br.com.soulblighter.bakingapp.provider.RecipesProvider";

    @TableEndpoint(table = RecipesDatabase.STEPS)
    public static class Steps {
        @ContentUri(path = "steps", type = "vnd.android.cursor.dir/steps", defaultSort = Step.Columns.id + " DESC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/steps");
    }

    @TableEndpoint(table = RecipesDatabase.INGREDIENTS)
    public static class Ingredients {
        @ContentUri(path = "ingredients", type = "vnd.android.cursor.dir/ingredients", defaultSort = Ingredient
            .Columns._ID + " DESC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/ingredients");
    }

    @TableEndpoint(table = RecipesDatabase.RECIPES)
    public static class Recipes {
        @ContentUri(path = "recipes", type = "vnd.android.cursor.dir/recipes", defaultSort = Recipe.Columns.id + " " +
            "DESC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");
    }
}
