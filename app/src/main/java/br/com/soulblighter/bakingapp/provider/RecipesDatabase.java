package br.com.soulblighter.bakingapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import br.com.soulblighter.bakingapp.data.Ingredient;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.data.Step;

@Database(version = RecipesDatabase.VERSION)
public class RecipesDatabase {

    public static final int VERSION = 4;

    @Table(Ingredient.Columns.class)
    @IfNotExists
    public static final String INGREDIENTS = "ingredients";
    @Table(Step.Columns.class)
    @IfNotExists
    public static final String STEPS = "steps";
    @Table(Recipe.Columns.class)
    @IfNotExists
    public static final String RECIPES = "recipes";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
    }
}
