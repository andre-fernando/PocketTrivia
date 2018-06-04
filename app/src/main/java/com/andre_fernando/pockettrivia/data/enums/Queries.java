package com.andre_fernando.pockettrivia.data.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("unused")
public class Queries {
    public static final String Amount = "amount";
    public static final String Category = "category";
    public static final String Difficulty = "difficulty";
    public static final String Type = "type";

    @StringDef({ Amount, Category, Difficulty, Type})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Query{}
}
