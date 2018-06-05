package com.andre_fernando.pockettrivia.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.andre_fernando.pockettrivia.data.enums.JsonTags;
import com.andre_fernando.pockettrivia.data.objects.BooleanQuestion;
import com.andre_fernando.pockettrivia.data.objects.MultipleChoiceQuestion;
import com.andre_fernando.pockettrivia.data.objects.Question;

import org.json.JSONException;
import org.json.JSONObject;

import ru.bullyboo.encoder.Encoder;
import ru.bullyboo.encoder.methods.AES;

/**
 * This is helper class that allows users
 * to easily interact with the shared
 * preferences.
 *
 * It also encrypts the username and converts
 * the objects to Json strings. For the encryption
 * I am using a third party library by BullyBoo which is under the Apache 2.0 licence
 * https://github.com/BullyBoo/Encryption?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=5529
 *
 *
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SharedPreferenceHelper {

    private final SharedPreferences sharedPreferences;


    //region Keys
    private final String keyDailyTrivia = "daily trivia";
    private final String keyName = "name";
    private final String isAnswered = "isAnswered";
    private final String userSelection = "userSelection";
    private final String isCorrect = "isCorrect";

    //endregion Keys

    public SharedPreferenceHelper(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //region boolean check methods

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isInitialized(){
        return sharedPreferences.contains(keyDailyTrivia);
    }

    public boolean isLoggedIn() {
        String encryptedName_key = Encoder.BuilderAES()
                .message(keyName)
                .method(AES.Method.AES_GCM_NO_PADDING)
                .encrypt();
        return sharedPreferences.contains(encryptedName_key);
    }

    //endregion boolean check methods

    //region Json Conversion Methods

    @SuppressWarnings("ConstantConditions")
    private Question fromJson(@NonNull String jsonString){
        try {
            JSONObject object = new JSONObject(jsonString);
            Question question = QuestionBuilder.build(object,1);


            question.setDailyTrivia(true);


            boolean is_Answered = object.getBoolean(isAnswered);
            if (is_Answered){
                question.setAnswered(true);

                boolean is_Correct = object.getBoolean(isCorrect);
                question.setCorrect(is_Correct);

                String user_Selection = object.getString(userSelection);
                if (question.isMultiple()){
                    ((MultipleChoiceQuestion)question).setUserSelection(user_Selection);
                } else {
                    ((BooleanQuestion)question).setUserSelection(Boolean.valueOf(user_Selection));
                }
            }
            return question;
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            Log.e(SharedPreferenceHelper.class.getSimpleName(), "fromJson: Conversion failed!"+e.getMessage());//NON-NLS
            return null;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String toJson(Question question) throws ClassCastException{
        StringBuilder builder = new StringBuilder();
        String correctAnswer;
        builder.append("{");

        builder.append(getStringKeyValue(JsonTags.Category,question.getQuestionCategory()));
        builder.append(",");

        builder.append(getStringKeyValue(JsonTags.Type,question.getQuestionType()));
        builder.append(",");

        builder.append(getStringKeyValue(JsonTags.Difficulty,question.getQuestionDifficulty()));
        builder.append(",");

        builder.append(getStringKeyValue(JsonTags.Question,formatString(question.getQuestionString())));
        builder.append(",");

        if (question.isMultiple()){
            correctAnswer = ((MultipleChoiceQuestion) question).getAnswerMultiple();
            correctAnswer = formatString(correctAnswer);
        } else {
            correctAnswer = ((BooleanQuestion) question).getAnswerBoolean();
        }
        builder.append(getStringKeyValue(JsonTags.CorrectAnswer, correctAnswer));
        builder.append(",");

        if (question.isAnswered()){
            builder.append(getStringKeyValue(isAnswered,"true"));
            builder.append(",");

            String userAnswer;
            if (question.isMultiple()){
                userAnswer = ((MultipleChoiceQuestion) question).getUserSelection();
            } else {
                userAnswer = ((BooleanQuestion) question).getUserSelection();
            }

            builder.append(getStringKeyValue(userSelection, userAnswer));
            builder.append(",");

            builder.append(getStringKeyValue(isCorrect, Boolean.toString(question.isCorrect())));
        } else {
            builder.append(getStringKeyValue(isAnswered,"false"));
        }

        if (question.isMultiple()){
            builder.append(",");
            builder.append(getArrayKeyValue(JsonTags.IncorrectAnswers,
                    ((MultipleChoiceQuestion)question).getOptions()));
        }

        builder.append("}");
        return builder.toString();
    }

    private String getStringKeyValue(String key,String value){
        return String.format("\"%s\":\"%s\"",key,value);
    }

    @SuppressWarnings("SameParameterValue")
    private String getArrayKeyValue(String key, String[] values){
        StringBuilder builder = new StringBuilder();
        String temp;
        builder.append(String.format("\"%s\":[",key));
        for (int index =0 ; index<values.length; index++) {
            temp =formatString(values[index]);
            builder.append(String.format("\"%s\"",temp));
            //As we don't need a comma at the end of the last value
            if (index != (values.length-1)){
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private String formatString(String unformatted){
        String toReturn = unformatted;
        int changesCount = 0;

        for (int index =0; index < unformatted.length() ; index++){
            char x = unformatted.charAt(index);
            if (x == '\"'){
                toReturn = escapeCharAt(toReturn,index+changesCount);
                changesCount++;
            }
        }
        return toReturn;
    }

    private String escapeCharAt(String str, int position){
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index<str.length() ; index++){
            if (index == position){
                builder.append('\\');
                builder.append(str.charAt(index));
            } else {
                builder.append(str.charAt(index));
            }
        }
        return builder.toString();
    }

    //endregion of Json Conversion Methods

    //region Username Methods

    public void setUserName(String name){
        String encryptedName_value, encryptedName_key;

        encryptedName_value = Encoder.BuilderAES()
                                .message(name)
                                .method(AES.Method.AES_GCM_NO_PADDING)
                                .encrypt();

        encryptedName_key = Encoder.BuilderAES()
                                .message(keyName)
                                .method(AES.Method.AES_GCM_NO_PADDING)
                                .encrypt();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(encryptedName_key,encryptedName_value);
        editor.apply();

        sharedPreferences.edit()
                         .putString(encryptedName_key,encryptedName_value)
                         .apply();
    }

    public String getUserName(){
        String encryptedName_key, toReturn;
        encryptedName_key = Encoder.BuilderAES()
                .message(keyName)
                .method(AES.Method.AES_GCM_NO_PADDING)
                .encrypt();
        toReturn = sharedPreferences.getString(encryptedName_key,"");

        return Encoder.BuilderAES()
                .message(toReturn)
                .method(AES.Method.AES_GCM_NO_PADDING)
                .decrypt();

    }

    public void removeUsername(){
        String encryptedName_key = Encoder.BuilderAES()
                .message(keyName)
                .method(AES.Method.AES_GCM_NO_PADDING)
                .encrypt();
        sharedPreferences.edit().remove(encryptedName_key).apply();
    }

    //endregion of Username Methods

    //region Daily Trivia Methods

    public void setDailyTrivia(Question question){
        String jsonQuestion = toJson(question);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyDailyTrivia,jsonQuestion);
        editor.apply();
    }

    public Question getDailyTrivia(){
        String jsonQuestion = sharedPreferences.getString(keyDailyTrivia,"");
        return fromJson(jsonQuestion);
    }

    //endregion of Daily Trivia Methods

}
