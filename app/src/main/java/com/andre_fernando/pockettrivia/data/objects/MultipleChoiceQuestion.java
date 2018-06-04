package com.andre_fernando.pockettrivia.data.objects;

import android.os.Parcel;

import com.andre_fernando.pockettrivia.data.enums.QuestionTypes;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Builder Pattern : Child Object
 *
 * his class is a child class of question and
 * contains the logic of handling a multiple
 * choice question.
 */
public class MultipleChoiceQuestion extends Question{

    // For multiple Choice
    private final String answerMultiple;
    private String userSelection;
    private final String[] options;
    private final String[] shuffledOptions;


    /**
     * Constructor for the multiple choice question
     * @param questionNumber Question number = int
     * @param questionCategory Question category/topic = String
     * @param questionDifficulty Question Difficulty = String
     * @param questionString The Question = String
     * @param answerMultiple the answer = String
     * @param options the incorrect answers = String[]
     */
    public MultipleChoiceQuestion(int questionNumber, String questionCategory,
                                  String questionDifficulty, String questionString,
                                  String answerMultiple, String[] options) {
        super(questionNumber, questionCategory, QuestionTypes.Multiple, questionDifficulty, questionString);
        this.answerMultiple = answerMultiple;
        this.options = options;
        shuffledOptions = shuffleOptions(options);
    }

    //region Getters & Setters

    public String getAnswerMultiple() {
        return answerMultiple;
    }

    public String[] getOptions(){
        return options;
    }

    public String[] getShuffledOptions() { return shuffledOptions; }

    public String getOption(int index){
        return shuffledOptions[index];
    }

    public String getUserSelection() {
        return userSelection;
    }

    public void setUserSelection(String userSelection) {
        this.userSelection = userSelection;
    }

    //endregion Getters & Setters

    //region Helper methods

    private String[] shuffleOptions(String[] options){
        String[] toReturn = new String[4];
        ArrayList<String> toShuffle = new ArrayList<>();
        for (int x =0 ; x <4 ; x++){
            if (x==3){

                toShuffle.add(this.answerMultiple);//adds the Answer to the list to be shuffled
            } else{
                toShuffle.add(options[x]);
            }
        }
        Collections.shuffle(toShuffle);//shuffles the list

        for (int x =0 ; x <4 ; x++){
            toReturn[x] = toShuffle.get(x);//Converts to an array
        }
        return toReturn;
    }


    public boolean checkAns(int index) {
        boolean isCorrect = this.answerMultiple.equals(shuffledOptions[index]);
        setAnswered(true);
        setCorrect(isCorrect);
        setUserSelection(getOption(index));
        return isCorrect;
    }

    public void checkAns(String userSelection) {
        boolean isCorrect = this.answerMultiple.equals(userSelection);
        setAnswered(true);
        setCorrect(isCorrect);
        setUserSelection(userSelection);
    }

    //endregion of helper methods

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.answerMultiple);
        dest.writeString(this.userSelection);
        dest.writeStringArray(this.options);
        dest.writeStringArray(this.shuffledOptions);
    }

    @SuppressWarnings("WeakerAccess")
    protected MultipleChoiceQuestion(Parcel in) {
        super(in);
        this.answerMultiple = in.readString();
        this.userSelection = in.readString();
        this.options = in.createStringArray();
        this.shuffledOptions = in.createStringArray();
    }

    public static final Creator<MultipleChoiceQuestion> CREATOR = new Creator<MultipleChoiceQuestion>() {
        @Override
        public MultipleChoiceQuestion createFromParcel(Parcel source) {
            return new MultipleChoiceQuestion(source);
        }

        @Override
        public MultipleChoiceQuestion[] newArray(int size) {
            return new MultipleChoiceQuestion[size];
        }
    };

    //endregion Parcelable
}
