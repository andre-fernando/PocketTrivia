package com.andre_fernando.pockettrivia.data.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable {
    private int id;
    private final int score;
    private final int totalQuestions;
    private final String name;
    private final String date;
    private final String time;
    private final String category;
    private final String difficulty;
    private final String type ;

    //region Constructors

    /**
     * Constructor with id. used to convert
     * cursor to object
     * @param id id int
     * @param score score int
     * @param totalQuestions total number of questions int
     * @param name user name string
     * @param date date string
     * @param time time string
     * @param category category string
     * @param difficulty difficulty string
     * @param type type of question string
     */
    public Result(int id, int score, int totalQuestions, String name,
                  String date, String time, String category,
                  String difficulty, String type) {
        this.id = id;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.name = name;
        this.date = date;
        this.time = time;
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;
    }

    /**
     * Constructor without id, which we get from the overview fragment
     * and use to add an entry into the database.
     * @param score score int
     * @param totalQuestions total number of questions int
     * @param name user name string
     * @param date date string
     * @param time time string
     * @param category category string
     * @param difficulty difficulty string
     * @param type type of question string
     */
    public Result(int score, int totalQuestions, String name, String date,
                  String time, String category, String difficulty, String type) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.name = name;
        this.date = date;
        this.time = time;
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;
    }

    //endregion Constructors

    //region Getters

    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }

    public String getScore() { return Integer.toString(score); }

    public String getTotalQuestions() { return Integer.toString(totalQuestions); }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getType() {
        return type;
    }

    //endregion Getters

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.score);
        dest.writeInt(this.totalQuestions);
        dest.writeString(this.name);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeString(this.category);
        dest.writeString(this.difficulty);
        dest.writeString(this.type);
    }

    @SuppressWarnings("WeakerAccess")
    protected Result(Parcel in) {
        this.id = in.readInt();
        this.score = in.readInt();
        this.totalQuestions = in.readInt();
        this.name = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.category = in.readString();
        this.difficulty = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel source) {
            return new Result(source);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    //endregion Parcelable
}
