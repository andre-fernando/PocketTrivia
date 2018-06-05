package com.andre_fernando.pockettrivia.data.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings({"unused", "HardCodedStringLiteral"})
public class JsonTags {
    public static final String ResponseCode = "response_code";
    public static final String Results = "results";
    public static final String Category = "category";
    public static final String Type = "type";
    public static final String Difficulty = "difficulty";
    public static final String Question = "question";
    public static final String CorrectAnswer = "correct_answer";
    public static final String IncorrectAnswers = "incorrect_answers";

    @StringDef ({ResponseCode, Results, Category, Type,
                Difficulty, Question, CorrectAnswer, IncorrectAnswers})
    @Retention(RetentionPolicy.SOURCE)
    public @interface jsontag{}

}
