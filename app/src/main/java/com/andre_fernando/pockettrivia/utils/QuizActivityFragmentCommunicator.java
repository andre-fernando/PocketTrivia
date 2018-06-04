package com.andre_fernando.pockettrivia.utils;

/**
 * This interface communicated between the Quiz
 * Activity and all it's fragments.
 */
public interface QuizActivityFragmentCommunicator {

    void processAnswer(int questionNumber, boolean isCorrect,
                       String userSelection);

    void saveHighScore(String name);
}
