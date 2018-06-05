package com.andre_fernando.pockettrivia.data.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings({"WeakerAccess", "HardCodedStringLiteral"})
public class QuestionDifficulty {
    public static final String Any = "any";
    public static final String Easy = "easy";
    public static final String Medium = "medium";
    public static final String Hard = "hard";

    @StringDef({Any, Easy,
            Medium, Hard})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Difficulty {}

}
