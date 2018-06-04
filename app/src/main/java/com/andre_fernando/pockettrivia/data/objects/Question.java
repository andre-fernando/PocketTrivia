package com.andre_fernando.pockettrivia.data.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.andre_fernando.pockettrivia.data.enums.QuestionDifficulty;
import com.andre_fernando.pockettrivia.data.enums.QuestionTypes;

/**
 * Builder Pattern: Parent Object
 *
 * The is the parent question class, it is
 * extended by BooleanQuestion and MultipleChoiceQuestion.
 */
@SuppressWarnings("WeakerAccess")
public class Question implements Parcelable {

    private final int questionNumber;
    private final String questionCategory;
    @QuestionTypes.Type
    private final String questionType;
    @QuestionDifficulty.Difficulty
    private final String questionDifficulty;
    private final String questionString;
    private boolean isAnswered;
    private boolean isCorrect;
    private boolean isDailyTrivia;

    public Question(int questionNumber, String questionCategory, String questionType,
                    String questionDifficulty, String questionString) {
        this.questionNumber = questionNumber;
        this.questionCategory = questionCategory;
        this.questionType = questionType;
        this.questionDifficulty = questionDifficulty;
        this.questionString = questionString;
        this.isAnswered = false;
        this.isCorrect = false;
        this.isDailyTrivia=false;
    }



    //region Getters

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionCategory() {
        return questionCategory;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getQuestionDifficulty() {
        return questionDifficulty;
    }

    public String getQuestionString() {
        return questionString;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public boolean isDailyTrivia() {
        return isDailyTrivia;
    }

    public boolean isMultiple() { return questionType.equals(QuestionTypes.Multiple); }

    //endregion Getters

    //region Setters

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setDailyTrivia(boolean dailyTrivia) {
        isDailyTrivia = dailyTrivia;
    }

    //endregion Setters

    //region Parcelable

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.questionNumber);
        dest.writeString(this.questionCategory);
        dest.writeString(this.questionType);
        dest.writeString(this.questionDifficulty);
        dest.writeString(this.questionString);
        dest.writeByte(this.isAnswered ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCorrect ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDailyTrivia ? (byte) 1 : (byte) 0);
    }

    protected Question(Parcel in) {
        this.questionNumber = in.readInt();
        this.questionCategory = in.readString();
        this.questionType = in.readString();
        this.questionDifficulty = in.readString();
        this.questionString = in.readString();
        this.isAnswered = in.readByte() != 0;
        this.isCorrect = in.readByte() != 0;
        this.isDailyTrivia = in.readByte() != 0;
    }

    //endregion Parcelable
}


