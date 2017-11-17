package br.com.soulblighter.bakingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.provider.RecipesDatabase;

public class Ingredient implements Serializable, Parcelable {
    public interface Columns {
        @DataType(DataType.Type.INTEGER)
        @PrimaryKey
        @AutoIncrement
        String _ID = "_id";

        @DataType(DataType.Type.INTEGER)
        @NotNull
        String quantity = "quantity";

        @DataType(DataType.Type.TEXT)
        @NotNull
        String measure = "measure";

        @DataType(DataType.Type.TEXT)
        @NotNull
        String ingredient = "ingredient";

        @DataType(DataType.Type.INTEGER)
        @References(table = RecipesDatabase.RECIPES, column = Recipe.Columns._ID)
        @NotNull
        String recipe = "recipe";
    }

    @SerializedName("quantity")
    @Expose
    public float quantity;

    @SerializedName("measure")
    @Expose
    public String measure;

    @SerializedName("ingredient")
    @Expose
    public String ingredient;


    public final static Parcelable.Creator<Ingredient> CREATOR = new Creator<Ingredient>() {

        @SuppressWarnings({"unchecked"})
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return (new Ingredient[size]);
        }

    };

    private final static long serialVersionUID = -2876876651202404364L;

    protected Ingredient(Parcel in) {
        this.quantity = ((float) in.readValue((float.class.getClassLoader())));
        this.measure = ((String) in.readValue((String.class.getClassLoader())));
        this.ingredient = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Ingredient() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(quantity);
        dest.writeValue(measure);
        dest.writeValue(ingredient);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Class c = getClass();

        sb.append(c.getSimpleName());

        Field[] fields = c.getFields();
        sb.append("{");
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                sb.append("\t").append(field.getName()).append(": ").append(field.get(this)).append("\n");
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static String getFormatedQuantity(Context context, float quantity, String measure) {
        int res = 0;
        switch (measure) {
            case "G":
                res = R.plurals.measure_G;
                break;
            case "K":
                res = R.plurals.measure_K;
                break;
            case "TSP":
                res = R.plurals.measure_TSP;
                break;
            case "TBLSP":
                res = R.plurals.measure_TBLSP;
                break;
            case "CUP":
                res = R.plurals.measure_CUP;
                break;
            case "UNIT":
                res = R.plurals.measure_UNIT;
                break;
            default:
                res = R.plurals.measure_UNIT;
                break;
        }

        DecimalFormat format = new DecimalFormat("#.#");
        int q = (int) quantity;
        if (q == 0) {
            q = 1;
        }

        return context.getResources().getQuantityString(res, q, format.format(quantity));
    }

    public static Ingredient loadFromCursor(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        int index;

        index = cursor.getColumnIndex(Columns.ingredient);
        if (index >= 0) {
            ingredient.ingredient = cursor.getString(index);
        }
        index = cursor.getColumnIndex(Columns.measure);
        if (index >= 0) {
            ingredient.measure = cursor.getString(index);
        }
        index = cursor.getColumnIndex(Columns.quantity);
        if (index >= 0) {
            ingredient.quantity = cursor.getInt(index);
        }

        return ingredient;
    }

}
