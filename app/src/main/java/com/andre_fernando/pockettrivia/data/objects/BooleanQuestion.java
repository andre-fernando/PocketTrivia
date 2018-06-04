package com.andre_fernando.pockettrivia.data.objects;

import android.os.Parcel;

import com.andre_fernando.pockettrivia.data.enums.QuestionTypes;

/**
 * Builder Pattern Child Object
 *
 * This class is a child class of question and
 * contains the logic of handling a boolean question.
 */
public class BooleanQuestion extends Question{
    // For true and false
    private final boolean answerBoolean;
    private boolean userSelection;

    /**
     * Constructor for the Boolean type of question
     * @param questionNumber question number =int
     * @param questionCategory Question category/ topic = String
     * @param questionDifficulty The level of Difficulty = String
     * @param questionString The question = String
     * @param answerBoolean The answer = boolean
     */
    public BooleanQuestion(int questionNumber, String questionCategory,
                           String questionDifficulty, String questionString, boolean answerBoolean) {
        super(questionNumber, questionCategory, QuestionTypes.TrueOrFalse,
                questionDifficulty, questionString);

        this.answerBoolean = answerBoolean;
    }

    //region Getters & Setters

    /**
     * Getter for boolean answer
     * @return boolean answer
     */
    public String getAnswerBoolean() {
        return Boolean.toString(answerBoolean);
    }

    public String getUserSelection() { return Boolean.toString(userSelection); }

    public void setUserSelection(boolean userSelection) { this.userSelection = userSelection; }

    //endregion Getters & Setters

    /**
     * Checks the answer submitted with the correct answer
     * @param selectedAnswer answer given by the user
     * @return boolean if the user is correct
     */
    public boolean checkAnswer(boolean selectedAnswer){
        boolean isCorrect = selectedAnswer == this.answerBoolean;
        setAnswered(true);
        setCorrect(isCorrect);
        setUserSelection(selectedAnswer);
        return isCorrect;
    }

    //region Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.answerBoolean ? (byte) 1 : (byte) 0);
        dest.writeByte(this.userSelection ? (byte) 1 : (byte) 0);
    }

    @SuppressWarnings("WeakerAccess")
    protected BooleanQuestion(Parcel in) {
        super(in);
        this.answerBoolean = in.readByte() != 0;
        this.userSelection = in.readByte() != 0;
    }

    public static final Creator<BooleanQuestion> CREATOR = new Creator<BooleanQuestion>() {
        @Override
        public BooleanQuestion createFromParcel(Parcel source) {
            return new BooleanQuestion(source);
        }

        @Override
        public BooleanQuestion[] newArray(int size) {
            return new BooleanQuestion[size];
        }
    };

    //endregion Parcelable Methods
}
