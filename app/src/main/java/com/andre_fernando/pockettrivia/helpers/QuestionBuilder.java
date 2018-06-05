package com.andre_fernando.pockettrivia.helpers;

import android.util.Log;

import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.data.enums.QuestionTypes;
import com.andre_fernando.pockettrivia.data.objects.BooleanQuestion;
import com.andre_fernando.pockettrivia.data.objects.MultipleChoiceQuestion;
import com.andre_fernando.pockettrivia.data.objects.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

/**
 * Builder Pattern : Builder Class
 *
 * This class with the data objects follow the builder pattern from
 * the gang of four.
 */
class QuestionBuilder {

    public static Question build(JSONObject questionJsonObject, int questionNumber){
        try {
            String categoryName,type,difficulty,question;
            categoryName = questionJsonObject.getString(JsonTags.Category);
            difficulty = questionJsonObject.getString(JsonTags.Difficulty);
            question= Jsoup.parse(questionJsonObject.getString(JsonTags.Question)).text();
            type = questionJsonObject.getString(JsonTags.Type);
            if (type.equals(QuestionTypes.Multiple)){
                String ans_str = Jsoup.parse(questionJsonObject.getString(JsonTags.CorrectAnswer)).text();
                JSONArray temp =  questionJsonObject.getJSONArray(JsonTags.IncorrectAnswers);
                String[] incorrect_answers = new String[temp.length()];
                for (int index =0; index<temp.length(); index++){
                    incorrect_answers[index]=Jsoup.parse(temp.getString(index)).text();
                }
                return new MultipleChoiceQuestion(questionNumber+1,categoryName,
                        difficulty, question,ans_str,incorrect_answers);
            } else{
                boolean ans_boolean = questionJsonObject.getBoolean(JsonTags.CorrectAnswer);
                return new BooleanQuestion(questionNumber+1,categoryName,difficulty,
                        question,ans_boolean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(QuestionBuilder.class.getSimpleName(), "build: "+"failed to build question!:" + e.getMessage()); //NON-NLS
            return null;
        }
    }
}
