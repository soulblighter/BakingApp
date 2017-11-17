package br.com.soulblighter.bakingapp.data;

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

import br.com.soulblighter.bakingapp.provider.RecipesDatabase;

public class Step implements Serializable, Parcelable {
    public interface Columns {
        @DataType(DataType.Type.INTEGER)
        @PrimaryKey
        @AutoIncrement
        String _ID = "_id";
        @DataType(DataType.Type.INTEGER)
        @NotNull
        String id = "id";
        @DataType(DataType.Type.TEXT)
        String shortDescription = "shortDescription";
        @DataType(DataType.Type.TEXT)
        String description = "description";
        @DataType(DataType.Type.TEXT)
        String videoURL = "videoURL";
        @DataType(DataType.Type.TEXT)
        String thumbnailURL = "thumbnailURL";

        @DataType(DataType.Type.INTEGER)
        @References(table = RecipesDatabase.RECIPES, column = Recipe.Columns._ID)
        @NotNull
        String recipe = "recipe";
    }

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("shortDescription")
    @Expose
    public String shortDescription;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("videoURL")
    @Expose
    public String videoURL;

    @SerializedName("thumbnailURL")
    @Expose
    public String thumbnailURL;

    public final static Parcelable.Creator<Step> CREATOR = new Creator<Step>() {


        @SuppressWarnings({"unchecked"})
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        public Step[] newArray(int size) {
            return (new Step[size]);
        }

    };

    private final static long serialVersionUID = -1577049701077724086L;

    protected Step(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.shortDescription = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.videoURL = ((String) in.readValue((String.class.getClassLoader())));
        this.thumbnailURL = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Step() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(shortDescription);
        dest.writeValue(description);
        dest.writeValue(videoURL);
        dest.writeValue(thumbnailURL);
    }

    public int describeContents() {
        return 0;
    }

    public static Step loadFromCursor(Cursor cursor) {
        Step step = new Step();
        int index;

        index = cursor.getColumnIndex(Columns.id);
        if (index >= 0) {
            step.id = cursor.getInt(index);
        }
        index = cursor.getColumnIndex(Columns.description);
        if (index >= 0) {
            step.description = cursor.getString(index);
        }
        index = cursor.getColumnIndex(Columns.shortDescription);
        if (index >= 0) {
            step.shortDescription = cursor.getString(index);
        }
        index = cursor.getColumnIndex(Columns.thumbnailURL);
        if (index >= 0) {
            step.thumbnailURL = cursor.getString(index);
        }
        index = cursor.getColumnIndex(Columns.videoURL);
        if (index >= 0) {
            step.videoURL = cursor.getString(index);
        }

        return step;
    }
}
