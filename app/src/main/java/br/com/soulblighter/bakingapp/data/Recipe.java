package br.com.soulblighter.bakingapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.Constraints;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;
import net.simonvt.schematic.annotation.UniqueConstraint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.soulblighter.bakingapp.provider.RecipesProvider;

public class Recipe implements Serializable, Parcelable {
    @Constraints(unique = @UniqueConstraint(name = "UNQ_ID_RECIPE", columns = {Columns.id}, onConflict =
        ConflictResolutionType.REPLACE))
    public interface Columns {
        @DataType(DataType.Type.INTEGER)
        @PrimaryKey
        @AutoIncrement
        String _ID = "_id";
        @DataType(DataType.Type.INTEGER)
        @Unique
        @NotNull
        String id = "id";
        @DataType(DataType.Type.TEXT)
        @NotNull
        String name = "name";
        @DataType(DataType.Type.INTEGER)
        @NotNull
        String servings = "servings";
        @DataType(DataType.Type.TEXT)
        String image = "image";
    }


    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("ingredients")
    @Expose
    public List<Ingredient> ingredients = new ArrayList<Ingredient>();

    @SerializedName("steps")
    @Expose
    public List<Step> steps = new ArrayList<Step>();

    @SerializedName("servings")
    @Expose
    public int servings;

    @SerializedName("image")
    @Expose
    public String image;


    public final static Parcelable.Creator<Recipe> CREATOR = new Creator<Recipe>() {

        @SuppressWarnings({"unchecked"})
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return (new Recipe[size]);
        }

    };

    private final static long serialVersionUID = 5159665761392114880L;

    protected Recipe(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.ingredients, (Ingredient.class.getClassLoader()));
        in.readList(this.steps, (Step.class.getClassLoader()));
        this.servings = ((int) in.readValue((int.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Recipe() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeList(ingredients);
        dest.writeList(steps);
        dest.writeValue(servings);
        dest.writeValue(image);
    }

    public int describeContents() {
        return 0;
    }


    public static Recipe loadFromCursor(Context context, Cursor cursor) {
        Recipe recipe = new Recipe();
        int index;

        index = cursor.getColumnIndex(Columns.id);
        if (index >= 0) {
            recipe.id = cursor.getInt(index);
        }
        index = cursor.getColumnIndex(Columns.name);
        if (index >= 0) {
            recipe.name = cursor.getString(index);
        }
        index = cursor.getColumnIndex(Columns.image);
        if (index >= 0) {
            recipe.image = cursor.getString(index);
        }
        index = cursor.getColumnIndex(Columns.servings);
        if (index >= 0) {
            recipe.servings = cursor.getInt(index);
        }
        index = cursor.getColumnIndex(Columns._ID);
        int _id = -1;
        if (index >= 0) {
            _id = cursor.getInt(index);
        }

        // Load Steps
        Cursor cursorSteps = context.getContentResolver().query(RecipesProvider.Steps.CONTENT_URI, null, Step.Columns
            .recipe + " = ?", new String[]{String.valueOf(_id)}, Step.Columns.id + " ASC");
        for (cursorSteps.moveToFirst(); !cursorSteps.isAfterLast(); cursorSteps.moveToNext()) {
            recipe.steps.add(Step.loadFromCursor(cursorSteps));
        }
        cursorSteps.close();

        // Load Ingredients
        Cursor cursorIngredients = context.getContentResolver().query(RecipesProvider.Ingredients.CONTENT_URI, null,
            Ingredient.Columns.recipe + " = ?", new String[]{String.valueOf(_id)}, Ingredient.Columns._ID + " DESC");
        for (cursorIngredients.moveToFirst(); !cursorIngredients.isAfterLast(); cursorIngredients.moveToNext()) {
            recipe.ingredients.add(Ingredient.loadFromCursor(cursorIngredients));
        }
        cursorIngredients.close();

        return recipe;
    }

    public void insertOnDb(Context context) {
        Cursor c = context.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, new String[]{Columns.id},
            Columns.id + " = ?", new String[]{String.valueOf(this.id)}, null);

        if (c.getCount() == 0) {

            ContentValues cv = new ContentValues();
            cv.put(Columns.id, this.id);
            cv.put(Columns.image, this.image);
            cv.put(Columns.name, this.name);
            cv.put(Columns.servings, this.servings);
            Uri uri = context.getContentResolver().insert(RecipesProvider.Recipes.CONTENT_URI, cv);
            int _id = Integer.parseInt(uri.getLastPathSegment());

            for (Step step : this.steps) {
                cv = new ContentValues();
                cv.put(Step.Columns.id, step.id);
                cv.put(Step.Columns.description, step.description);
                cv.put(Step.Columns.recipe, _id);
                cv.put(Step.Columns.shortDescription, step.shortDescription);
                cv.put(Step.Columns.thumbnailURL, step.thumbnailURL);
                cv.put(Step.Columns.videoURL, step.videoURL);
                context.getContentResolver().insert(RecipesProvider.Steps.CONTENT_URI, cv);
            }

            for (Ingredient ingredient : this.ingredients) {
                cv = new ContentValues();
                cv.put(Ingredient.Columns.ingredient, ingredient.ingredient);
                cv.put(Ingredient.Columns.measure, ingredient.measure);
                cv.put(Ingredient.Columns.quantity, ingredient.quantity);
                cv.put(Step.Columns.recipe, _id);
                context.getContentResolver().insert(RecipesProvider.Ingredients.CONTENT_URI, cv);
            }
        }
        c.close();
    }

    public void deleteFromDb(Context context) {
        Cursor c = context.getContentResolver().query(RecipesProvider.Recipes.CONTENT_URI, new String[]{Columns.id},
            Columns.id + " = ?", new String[]{String.valueOf(this.id)}, null);

        if (c.getCount() > 0) {
            context.getContentResolver().delete(RecipesProvider.Steps.CONTENT_URI, Step.Columns.recipe + " = ?", new
                String[]{String.valueOf(this.id)});
            context.getContentResolver().delete(RecipesProvider.Ingredients.CONTENT_URI, Ingredient.Columns.recipe +
                " = ?", new String[]{String.valueOf(this.id)});
            context.getContentResolver().delete(RecipesProvider.Recipes.CONTENT_URI, Columns.id + " = ?", new
                String[]{String.valueOf(this.id)});
        }
        c.close();
    }
}
