package com.andre_fernando.pockettrivia.data.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings({"WeakerAccess", "HardCodedStringLiteral"})
public class QuestionTypes {
    //TypeDef for Type
    public static final String Any = "any";
    public static final String Multiple = "multiple";
    public static final String TrueOrFalse = "boolean";

    @StringDef({Any, Multiple, TrueOrFalse})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type{}
}
